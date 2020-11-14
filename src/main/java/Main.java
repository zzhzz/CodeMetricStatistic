import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {

    static MetricRecorder recorder = null;

    static void work(ParseResult<CompilationUnit> result) throws IOException {
        Optional<CompilationUnit> unit = result.getResult();
        ASTVisitor visitor = null;
        if(unit.isPresent()){
            CompilationUnit cu = unit.get();
            Collector collector = new Collector();
            visitor = new ASTVisitor();
            visitor.visit(cu, collector);
            collector.cyclomaticComplexity(recorder);
            collector.HalsteadMetrics(recorder);
            collector.ABC(recorder);
            collector.otherMetrics(recorder);
            recorder.export();
        }
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

    static void src_metrics(String dirName){
        System.out.println("Apply source code metric static analyze.");
        System.out.println("Directory Name is: " + dirName);
        List<String> files =retrieveFiles(dirName);
        try {
            for (String fname : files) work(getAST(fname));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void binary_metrics(String dirName) {

    }

    public static void main(String[] args) {
        String type = args[0], dirName= args[1], save_f = args[2];
        recorder = new MetricRecorder(save_f);
        if(type.equals("source")){
            src_metrics(dirName);
        } else if(type.equals("binary")) {
            binary_metrics(dirName);
        }
    }
}
