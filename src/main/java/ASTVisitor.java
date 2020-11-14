import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ASTVisitor extends VoidVisitorAdapter<Collector> {
    String methodname;
    int cur_depth = 0, max_depth = 0, sum_depth = 0;
    static Set<String> conditions = new HashSet<>();

    static {
        conditions.add("<");
        conditions.add("<=");
        conditions.add(">");
        conditions.add(">=");
        conditions.add("==");
        conditions.add("!=");
        conditions.add("!");
    }

    @Override
    public void visit(MethodDeclaration node, Collector counter){
        methodname = node.getNameAsString();
        cur_depth = 0;
        max_depth = 0;
        sum_depth = 0;
        int loc = node.getRange().map(range -> range.end.line - range.begin.line).orElse(0);
        counter.addOthers(methodname, "LinesOfCode", loc);
        counter.addOthers(methodname, "NumberOfParams", node.getParameters().size());
        super.visit(node, counter);
        counter.addOthers(methodname, "MaxNestDepth", max_depth);
        counter.addOthers(methodname, "TotalNestDepth", sum_depth);
    }

    @Override
    public void visit(CastExpr expr, Collector counter) {
        counter.addOthers(methodname, "NumberOfClassCast", 1);
        super.visit(expr, counter);
    }

    @Override
    public void visit(ForEachStmt node, Collector counter) {
        counter.addOthers(methodname, "NumberOfLoops", 1);
        counter.addCC(methodname);
        cur_depth += 1;
        super.visit(node, counter);
        cur_depth -= 1;
    }

    @Override
    public void visit(MethodCallExpr node, Collector counter) {
        counter.addOthers(methodname, "NumberOfBranch", 1);
        counter.addOthers(methodname, "NumberOfArguments", node.getArguments().size());
        super.visit(node, counter);
    }

    @Override
    public void visit(ObjectCreationExpr node, Collector counter) {
        counter.addOthers(methodname, "NumberOfBranch", 1);
        super.visit(node, counter);
    }

    @Override
    public void visit(ForStmt node, Collector counter) {
        counter.addOthers(methodname, "NumberOfLoops", 1);
        counter.addCC(methodname);
        cur_depth += 1;
        super.visit(node, counter);
        cur_depth -= 1;
    }

    @Override
    public void visit(IfStmt node, Collector counter) {
        counter.addCC(methodname);
        if(node.getElseStmt().isPresent()) {
            counter.addCC(methodname);
            counter.addOthers(methodname, "NumberOfCond", 1);
        }
        String condition = node.getCondition().toString();
        regexCheck(condition, Pattern.compile("/(\\s|\\w|\\d)&(\\s|\\w|\\d)/xg"),  "BITWISE_AND_OPERATOR",  counter);
        regexCheck(condition, Pattern.compile("/(\\s|\\w|\\d)\\|(\\s|\\w|\\d)/xg"),  "BITWISE_OR_OPERATOR",   counter);
        regexCheck(condition, Pattern.compile("/(\\s|\\w|\\d)&&(\\s|\\w|\\d)/xg"), "AND_OPERATOR",          counter);
        regexCheck(condition, Pattern.compile("/(\\s|\\w|\\d)\\|\\|(\\s|\\w|\\d)/xg"), "OR_OPERATOR",           counter);
        cur_depth += 1;
        super.visit(node, counter);
        cur_depth -= 1;
    }
    private void regexCheck(String haystack, Pattern pattern, String type, Collector counter) {
        Matcher matcher = pattern.matcher(haystack);
        while (matcher.find()) counter.addCC(methodname);
    }

    @Override
    public void visit(SwitchEntry node, Collector counter) {
        for (Statement st :node.getStatements()) counter.addCC(methodname);
        counter.addOthers(methodname, "NumberOfCond", node.getStatements().size());
        cur_depth += 1;
        super.visit(node, counter);
        cur_depth -= 1;
    }

    @Override
    public void visit(ThrowStmt node, Collector counter) {
        counter.addCC(methodname);
        super.visit(node, counter);
    }

    @Override
    public void visit(TryStmt node, Collector counter) {
        counter.addOthers(methodname, "NumberOfCond", 1);
        counter.addCC(methodname);
        cur_depth += 1;
        super.visit(node, counter);
        cur_depth -= 1;
    }

    @Override
    public void visit(CatchClause node, Collector counter) {
        counter.addOthers(methodname, "NumberOfCond", 1);
        counter.addCC(methodname);
        cur_depth += 1;
        super.visit(node, counter);
        cur_depth -= 1;
    }


    @Override
    public void visit(ConditionalExpr node, Collector counter) {
        counter.addOthers(methodname, "NumberOfCond", 1);
        super.visit(node, counter);
    }

    @Override
    public void visit(WhileStmt node, Collector counter) {
        counter.addCC(methodname);
        counter.addOthers(methodname, "NumberOfLoops", 1);
        cur_depth += 1;
        super.visit(node, counter);
        cur_depth -= 1;
    }

    @Override
    public void visit(DoStmt node, Collector counter) {
        counter.addCC(methodname);
        counter.addOthers(methodname, "NumberOfLoops", 1);
        cur_depth += 1;
        super.visit(node, counter);
        cur_depth -= 1;
    }

    @Override
    public void visit(BlockStmt node, Collector counter) {
        counter.addOthers(methodname, "NumberOfStmt", node.getStatements().size());
        max_depth = Math.max(max_depth, cur_depth);
        super.visit(node, counter);
        if(max_depth == cur_depth) sum_depth += cur_depth;
    }

    @Override
    public void visit(BinaryExpr node, Collector counter) {
        String op = node.getOperator().asString();
        counter.addOP(methodname, op);
        if(conditions.contains(op)) counter.addOthers(methodname, "NumberOfCond", 1);
        super.visit(node, counter);
    }

    @Override
    public void visit(UnaryExpr node, Collector counter) {
        String op = node.getOperator().asString();
        counter.addOP(methodname, op);
        if(node.isAssignExpr()) counter.addOthers(methodname, "NumberOfAssign", 1);
        if(conditions.contains(op)) counter.addOthers(methodname, "NumberOfCond", 1);
        super.visit(node, counter);
    }

    @Override
    public void visit(InstanceOfExpr node, Collector counter) {
        counter.addOthers(methodname, "NumberOfCond", 1);
        super.visit(node, counter);
    }

    @Override
    public void visit(AssignExpr node, Collector counter) {
        counter.addOP(methodname, node.getOperator().asString());
        counter.addOthers(methodname, "NumberOfAssign", 1);
        super.visit(node, counter);
    }

    @Override
    public void visit(VariableDeclarator node, Collector counter) {
        counter.addOthers(methodname, "NumberOfVarDecl", 1);
        if(node.getInitializer().isPresent()) counter.addOP(methodname, "=");
        super.visit(node, counter);
    }

    @Override
    public void visit(SimpleName node, Collector counter) {
        counter.addName(methodname, node.getIdentifier());
        super.visit(node, counter);
    }

    @Override
    public void visit(NullLiteralExpr node, Collector counter) {
        counter.addName(methodname, "null");
        super.visit(node, counter);
    }

    @Override
    public void visit(StringLiteralExpr node, Collector counter) {
        counter.addName(methodname, node.asString());
        super.visit(node, counter);
    }

    @Override
    public void visit(CharLiteralExpr node, Collector counter) {
        counter.addName(methodname, Character.toString(node.asChar()));
        super.visit(node, counter);
    }

    @Override
    public void visit(BooleanLiteralExpr node, Collector counter) {
        counter.addName(methodname, Boolean.toString(node.getValue()));
        super.visit(node, counter);
    }

    @Override
    public void visit(DoubleLiteralExpr node, Collector counter){
        counter.addName(methodname, String.valueOf(node.asDouble()));
        super.visit(node, counter);
    }


    @Override
    public void visit(IntegerLiteralExpr node, Collector counter){
        counter.addName(methodname, node.asNumber().toString());
        super.visit(node, counter);
    }

    @Override
    public void visit(LongLiteralExpr node, Collector counter){
        counter.addName(methodname, node.asNumber().toString());
        super.visit(node, counter);
    }
}
