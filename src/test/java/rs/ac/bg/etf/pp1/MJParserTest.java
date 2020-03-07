package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.Program;

import java.io.*;

public class MJParserTest extends MJTest {

    public static MJParserTest INSTANCE = new MJParserTest();

    private MJParserTest() {}

    @Override
    protected String testName() { return "parser"; }

    @Override
    protected void processTestFile(String fileName, Reader r, Logger log) throws Exception {
        MJLexer lexer = new MJLexer(r, fileName);
        MJParser parser = new MJParser(lexer);
        Program prog = (Program) parser.parse().value;
        log.info("Abstract syntax tree: " + prog.toString(""));
    }
}