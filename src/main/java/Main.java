import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;
import soot.PackManager;
import soot.Transform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {

    static MetricRecorder recorder = null;
    static Collector collector = null;

    static void work(ParseResult<CompilationUnit> result) {
        Optional<CompilationUnit> unit = result.getResult();
        ASTVisitor visitor;
        if(unit.isPresent()){
            CompilationUnit cu = unit.get();
            visitor = new ASTVisitor();
            visitor.visit(cu, collector);
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
    public static List<ClassNode> load(File file) {
        try {
            JarFile jar = new JarFile(file);
            List<ClassNode> list = new ArrayList<>();
            Enumeration<JarEntry> enumeration = jar.entries();
            while(enumeration.hasMoreElements()) {
                JarEntry next = enumeration.nextElement();
                if(next.getName().endsWith(".class")) {
                    ClassReader reader = new ClassReader(jar.getInputStream(next));
                    ClassNode node = new ClassNode();
                    reader.accept(node, ClassReader.SKIP_DEBUG);
                    list.add(node);
                }
            }
            jar.close();
            return list;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void binary_metrics(String jarName) {
        List<ClassNode> classes = load(new File(jarName));
        for(ClassNode classNode : classes) {
            for(MethodNode methodNode : classNode.methods){
                String methodname = methodNode.name;
                for(AbstractInsnNode inst : methodNode.instructions.toArray()){
                    if(inst instanceof LdcInsnNode) {
                        collector.addOthers(methodname, "NumberOfLdcInsn", 1);
                    }
                    if(inst instanceof JumpInsnNode) {
                        collector.addOthers(methodname, "NumberOfJumpInsn", 1);
                    }
                    if(inst instanceof FieldInsnNode){
                        collector.addOthers(methodname, "NumberOfFieldInsn", 1);
                    }
                    if(inst instanceof IincInsnNode){
                        collector.addOthers(methodname, "NumberOfIincInsn", 1);
                    }
                    if(inst instanceof MethodInsnNode){
                        collector.addOthers(methodname, "NumberOfMethodInsn", 1);
                    }
                    if(inst instanceof VarInsnNode){
                        collector.addOthers(methodname, "NumberOfVarInsn", 1);
                    }
                    if(inst instanceof InvokeDynamicInsnNode){
                        collector.addOthers(methodname, "NumberOfInvokeDInsn", 1);
                    }
                    

                }
            }
        }
    }

    public static void main(String[] args) {
        String type = args[0], fName= args[1], save_f = args[2];
        recorder = new MetricRecorder(save_f);
        collector = new Collector();
        if(type.equals("source")){
            src_metrics(fName);
            collector.cyclomaticComplexity(recorder);
            collector.HalsteadMetrics(recorder);
            collector.ABC(recorder);
        } else if(type.equals("binary")) {
            binary_metrics(fName);
        }
        collector.otherMetrics(recorder);
        try {
            recorder.export();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
