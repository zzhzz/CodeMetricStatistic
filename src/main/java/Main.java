import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.serialization.JavaParserJsonSerializer;
import soot.jimple.parser.Parse;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    static ASTHalsteadVisitor work(ParseResult<CompilationUnit> result){
        Optional<CompilationUnit> unit = result.getResult();
        ASTHalsteadVisitor visitor = null;
        if(unit.isPresent()){
            CompilationUnit cu = unit.get();
            visitor = new ASTHalsteadVisitor();
            visitor.visit(cu, null);
        }
        return visitor;
    }

    static ParseResult<CompilationUnit> getAST(String fname, JavaParser parser) throws FileNotFoundException {
        File file = new File(fname);
        ParseResult<CompilationUnit> result = parser.parse(file);
        return result;
    }

    static ParseResult<CompilationUnit> getAST(String fname) throws FileNotFoundException {
        ParserConfiguration configuration = new ParserConfiguration();
        configuration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_8);
        JavaParser parser_8 = new JavaParser(configuration);
        ParseResult<CompilationUnit> result = getAST(fname, parser_8);
        if (!result.isSuccessful()) {
            configuration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_1_4);
            JavaParser parser_4 = new JavaParser(configuration);
            result = getAST(fname, parser_4);
            if (!result.isSuccessful()) {
                System.err.println(result.getProblems());
                System.err.println("Parser:Parsing failed for: " + fname);
                System.exit(1);
            }
        }
        return result;
    }

    public static List<String> retrieveFiles(String directory) {
        List<String> Files = new ArrayList<>();
        File dir = new File(directory);
        if (!dir.isDirectory()) {
            System.out.println("The provided path is not a valid directory");
            System.exit(1);
        }
        for (File file : dir.listFiles()) {
            if(file.isDirectory()) Files.addAll(retrieveFiles(file.getAbsolutePath()));
            if (file.getName().endsWith((".java"))) Files.add(file.getAbsolutePath());
        }
        return Files;
    }

    public static void main(String[] args) {
        String dirName= args[1];
        System.out.println("Directory Name is: " + dirName);
        List<String> files =retrieveFiles(dirName);
        int DistinctOperators=0, DistinctOperands=0, TotalOperators=0, TotalOperands=0, OperatorCount=0, OperandCount=0;
        try {
            for (String fname : files) {
                ParseResult<CompilationUnit> result = getAST(fname);
                ASTHalsteadVisitor visitor = work(result);
                DistinctOperators += visitor.oprt.size();
                DistinctOperands+=visitor.names.size();
                OperatorCount=0;
                for (int f : visitor.oprt.values()) OperatorCount+= f;
                TotalOperators+=OperatorCount;
                OperandCount=0;
                for (int f : visitor.names.values()) OperandCount += f;
                TotalOperands+=OperandCount;
            }
            HalsteadMetrics hal = new HalsteadMetrics();
            hal.setParameters(DistinctOperators, DistinctOperands, TotalOperators, TotalOperands);
            hal.getVocabulary();
            hal.getProglen();
            hal.getCalcProgLen();
            hal.getVolume();
            hal.getDifficulty();
            hal.getEffort();
            hal.getTimeReqProg();
            hal.getTimeDelBugs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
