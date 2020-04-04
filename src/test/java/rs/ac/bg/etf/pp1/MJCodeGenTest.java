package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.symboltable.MJTab;
import rs.etf.pp1.mj.runtime.Code;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Reader;

public class MJCodeGenTest extends MJTest {

    public static MJCodeGenTest INSTANCE = new MJCodeGenTest();

    private MJCodeGenTest() {}

    @Override
    protected String testName() { return "codegen"; }

    @Override
    protected void processTestFile(Reader r) throws Exception {
        MJLexer lexer = new MJLexer(r);
        MJParser parser = new MJParser(lexer);
        Program prog = (Program) parser.parse().value;
        log.info("Abstract syntax tree: " + prog.toString(""));
        if (parser.getErrorCount() == 0) {
            System.out.println("Syntax analysis completed without errors!");
        } else {
            System.err.println("Syntax analysis completed with " + parser.getErrorCount() + " errors!");
        }
        System.out.println("Starting semantic analysis...");
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        prog.traverseBottomUp(analyzer);
        MJTab.dump();
        if (analyzer.getErrorCount() == 0) {
            System.out.println("Semantic analysis completed without errors!");
            File objFile = new File("test\\program.obj");
            log.info("Generating bytecode file: " + objFile.getAbsolutePath());
            if (objFile.exists()) {
                objFile.delete();
            }
            CodeGenerator generator = new CodeGenerator();
            prog.traverseBottomUp(generator);
            Code.dataSize = analyzer.getVarCount();
            Code.mainPc = generator.getMainPC();
            Code.write(new FileOutputStream(objFile));
            log.info("Compilation finished!");
        } else  {
            System.err.println("Semantic analysis completed with " + analyzer.getErrorCount() + " errors!");
        }
    }
}