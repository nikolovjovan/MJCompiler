package rs.ac.bg.etf.pp1.test;

import rs.ac.bg.etf.pp1.Compiler;
import rs.ac.bg.etf.pp1.exceptions.MJCodeGeneratorException;
import rs.ac.bg.etf.pp1.exceptions.MJSemanticAnalyzerException;

import java.io.Reader;

public class MJCodeGenTest extends MJTest {

    public static MJCodeGenTest INSTANCE = new MJCodeGenTest();

    private MJCodeGenTest() {}

    @Override
    protected String testName() { return "codegen"; }

    @Override
    protected void processTestFile(Reader r) throws Exception {
        try {
            Compiler.compile(r);
        } catch (MJSemanticAnalyzerException | MJCodeGeneratorException e) {
            logger.error("Test FAILED!");
            System.err.println("Compilation failed!");
            e.printStackTrace(System.err);
        }
    }
}