package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;
import org.apache.log4j.Logger;

import java.io.*;

public class MJLexerTest extends MJTest {

    public static MJLexerTest INSTANCE = new MJLexerTest();

    private MJLexerTest() {}

    @Override
    protected String testName() { return "lexer"; }

    @Override
    protected void processTestFile(String fileName, Reader r, Logger log) throws Exception {
        MJLexer lexer = new MJLexer(r, fileName);
        Symbol currToken;
        while ((currToken = lexer.next_token()).sym != sym.EOF) {
            if (currToken != null) {
                log.info(currToken.toString() + (currToken.value != null ? " " + currToken.value.toString() : ""));
            }
        }
    }
}