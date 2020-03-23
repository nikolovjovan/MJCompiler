package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;

import java.io.*;

public class MJLexerTest extends MJTest {

    public static MJLexerTest INSTANCE = new MJLexerTest();

    private MJLexerTest() {}

    @Override
    protected String testName() { return "lexer"; }

    @Override
    protected void processTestFile(Reader r) throws Exception {
        MJLexer lexer = new MJLexer(r);
        Symbol currToken = lexer.next_token();
        while (currToken.sym != sym.EOF) currToken = lexer.next_token();
    }
}