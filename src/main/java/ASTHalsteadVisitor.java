import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashMap;

public class ASTHalsteadVisitor extends VoidVisitorAdapter<Void> {
    public HashMap<String, Integer> names = new HashMap<>();
    public HashMap<String, Integer> oprt = new HashMap<>();

    @Override
    public void visit(BinaryExpr node, Void arg) {
        super.visit(node, arg);
        if (!this.oprt.containsKey(node.getOperator().toString()))
            this.oprt.put(node.getOperator().toString(), 1);
        else
            this.oprt.put(node.getOperator().toString(), this.oprt.get(node.getOperator().toString()) + 1);
    }

    @Override
    public void visit(UnaryExpr node, Void arg) {
        super.visit(node, arg);
        if (!this.oprt.containsKey(node.getOperator().toString()))
            this.oprt.put(node.getOperator().toString(), 1);
        else
            this.oprt.put(node.getOperator().toString(), this.oprt.get(node.getOperator().toString()) + 1);
    }

    @Override
    public void visit(AssignExpr node, Void arg)
    {
        super.visit(node, arg);
        if (!this.oprt.containsKey(node.getOperator().toString()))
            this.oprt.put(node.getOperator().toString(), 1);
        else
            this.oprt.put(node.getOperator().toString(), this.oprt.get(node.getOperator().toString())+1);
    }

    @Override
    public void visit(VariableDeclarator node, Void arg) {
        super.visit(node, arg);
        if(node.getInitializer().isPresent()) {
            if(!this.oprt.containsKey("=")) this.oprt.put("=", 1);
            else this.oprt.put("=", this.oprt.get("=")+1);
        }
    }
    @Override
    public void visit(SimpleName node, Void arg) {
        super.visit(node, arg);
        if (!this.names.containsKey(node.getIdentifier())) this.names.put(node.getIdentifier(),1);
        else this.names.put(node.getIdentifier(), this.names.get(node.getIdentifier()) + 1);
    }
    @Override
    public void visit(NullLiteralExpr node, Void arg) {
        super.visit(node, arg);
        if (!this.names.containsKey("null")) this.names.put("null", 1);
        else this.names.put("null", this.names.get("null")+1);
    }

    @Override
    public void visit(StringLiteralExpr node, Void arg) {
        super.visit(node, arg);
        if (!this.names.containsKey(node.asString())) this.names.put(node.asString(),1);
        else this.names.put(node.asString(), this.names.get(node.asString())+1);
    }

    @Override
    public void visit(CharLiteralExpr node, Void arg) {
        super.visit(node, arg);
        if (!this.names.containsKey(Character.toString(node.asChar())))
            this.names.put(Character.toString(node.asChar()), 1);
        else {
            this.names.put(Character.toString(node.asChar()),
                    this.names.get(Character.toString(node.asChar())) + 1);
        }
    }

    @Override
    public void visit(BooleanLiteralExpr node, Void arg) {
        super.visit(node, arg);
        if (!this.names.containsKey(Boolean.toString(node.getValue())))
            this.names.put(Boolean.toString(node.getValue()),1);
        else
            this.names.put(Boolean.toString(node.getValue()),
                    this.names.get(Boolean.toString(node.getValue()))+1);
    }

    @Override
    public void visit(DoubleLiteralExpr node, Void arg){
        super.visit(node, arg);
        String tk = String.valueOf(node.asDouble());
        if(!this.names.containsKey(tk)) this.names.put(tk, 1);
        else this.names.put(tk, this.names.get(tk) + 1);
    }

    @Override
    public void visit(IntegerLiteralExpr node, Void arg){
        super.visit(node, arg);
        String tk = String.valueOf(node.asNumber().toString());
        if(!this.names.containsKey(tk)) this.names.put(tk, 1);
        else this.names.put(tk, this.names.get(tk) + 1);
    }

    @Override
    public void visit(LongLiteralExpr node, Void arg){
        super.visit(node, arg);
        String tk = String.valueOf(node.asNumber().toString());
        if(!this.names.containsKey(tk)) this.names.put(tk, 1);
        else this.names.put(tk, this.names.get(tk) + 1);
    }


}
