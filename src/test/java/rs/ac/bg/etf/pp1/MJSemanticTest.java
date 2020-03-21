package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.symboltable.MyTab;

import java.io.Reader;

public class MJSemanticTest extends MJTest {

    public static MJSemanticTest INSTANCE = new MJSemanticTest();

    private MJSemanticTest() {}

    @Override
    protected String testName() { return "semantic"; }

    @Override
    protected void processTestFile(String fileName, Reader r, Logger log) throws Exception {
        MJLexer lexer = new MJLexer(r, fileName);
        MJParser parser = new MJParser(lexer);
        Program prog = (Program) parser.parse().value;
        log.info("Abstract syntax tree: " + prog.toString(""));
        if (parser.getErrorCount() == 0) {
            System.out.println("Syntax analysis completed without errors!");
            System.out.println("Starting semantic analysis...");
            SemanticAnalyzer analyzer = new SemanticAnalyzer(fileName);
            prog.traverseBottomUp(analyzer);
            if (analyzer.getErrorCount() == 0) {
                System.out.println("Semantic analysis completed without errors!");
            } else {
                System.err.println("Semantic analysis completed with " + analyzer.getErrorCount() + " errors!");
            }
            MyTab.dump();
        } else {
            System.err.println("Syntax analysis completed with " + parser.getErrorCount() + " errors!");
        }
    }
}