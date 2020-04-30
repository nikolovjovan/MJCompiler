package rs.ac.bg.etf.pp1.test;

import rs.ac.bg.etf.pp1.Compiler;
import rs.ac.bg.etf.pp1.SemanticAnalyzer;
import rs.ac.bg.etf.pp1.ast.Program;

import java.io.Reader;

public class MJAnalyzerTest extends MJTest {

    public static MJAnalyzerTest INSTANCE = new MJAnalyzerTest();

    private MJAnalyzerTest() {}

    @Override
    protected String testName() { return "analyzer"; }

    @Override
    protected void processTestInputFile(Reader r) throws Exception {
        Program prog = MJParserTest.INSTANCE.parseTestFile(r);
        System.out.println("Starting semantic analysis...");
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        prog.traverseBottomUp(analyzer);
        if (analyzer.getErrorCount() == 0) {
            System.out.println("Semantic analysis completed without errors.");
        } else {
            System.err.println("Semantic analysis completed with " + analyzer.getErrorCount() + " error(s)!");
        }
        Compiler.tsdump();
    }
}