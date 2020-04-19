package rs.ac.bg.etf.pp1.test;

import rs.ac.bg.etf.pp1.SemanticAnalyzer;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.symboltable.MJTable;

import java.io.Reader;

public class MJSemanticTest extends MJTest {

    public static MJSemanticTest INSTANCE = new MJSemanticTest();

    private MJSemanticTest() {}

    @Override
    protected String testName() { return "semantic"; }

    @Override
    protected void processTestFile(Reader r) throws Exception {
        Program prog = MJParserTest.INSTANCE.parseTestFile(r);
        System.out.println("Starting semantic analysis...");
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        prog.traverseBottomUp(analyzer);
        if (analyzer.getErrorCount() == 0) {
            System.out.println("Semantic analysis completed without errors.");
        } else {
            System.out.println("Semantic analysis completed with " + analyzer.getErrorCount() + " error(s)!");
        }
        MJTable.dump();
    }
}