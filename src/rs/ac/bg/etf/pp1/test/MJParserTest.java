package rs.ac.bg.etf.pp1.test;

import rs.ac.bg.etf.pp1.MJLexer;
import rs.ac.bg.etf.pp1.MJParser;
import rs.ac.bg.etf.pp1.ast.Program;

import java.io.Reader;

public class MJParserTest extends MJTest {

    public static MJParserTest INSTANCE = new MJParserTest();

    private MJParserTest() {}

    Program parseTestFile(Reader r) throws Exception {
        MJLexer lexer = new MJLexer(r);
        MJParser parser = new MJParser(lexer);
        Program prog = (Program) parser.parse().value;
        logger.info("Abstract syntax tree:\n" + prog.toString(""));
        if (parser.getErrorCount() == 0) {
            System.out.println("Syntax analysis completed without errors.");
        } else {
            System.err.println("Syntax analysis completed with " + parser.getErrorCount() + " error(s)!");
        }
        return prog;
    }

    @Override
    protected String testName() { return "parser"; }

    @Override
    protected void processTestInputFile(Reader r) throws Exception {
        parseTestFile(r);
    }
}