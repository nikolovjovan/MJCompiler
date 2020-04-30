package rs.ac.bg.etf.pp1.test;

import rs.ac.bg.etf.pp1.Compiler;
import rs.etf.pp1.mj.runtime.Run;

import java.io.*;

public class MJGeneratorTest extends MJTest {

    public static MJGeneratorTest INSTANCE = new MJGeneratorTest();

    private File programInFile;
    private File expectedProgramOutFile, expectedProgramErrFile;
    private File actualProgramOutFile, actualProgramErrFile;

    private MJGeneratorTest() {}

    @Override
    protected void initializeFiles() {
        super.initializeFiles();
        programInFile = new File(expectedFileBasePath.concat(".vm.in"));
        expectedProgramOutFile = new File(expectedFileBasePath.concat(".vm.out"));
        expectedProgramErrFile = new File(expectedFileBasePath.concat(".vm.err"));
        actualProgramOutFile = new File(actualFileBasePath.concat(".vm.out"));
        actualProgramErrFile = new File(actualFileBasePath.concat(".vm.err"));
    }

    @Override
    protected boolean checkTestPassed() {
        boolean passed = super.checkTestPassed();
        // If program is not being run return inherited result.
        if (!programInFile.exists()) return passed;
        // Otherwise, ignore inherited result and check program output and error output. Errors will still be displayed
        // if compiler outputs do not match, but test will pass if the compiled program generates the same result for
        // the provided inputs. This way compiler can be changed but resulting program behavior must be the same.
        passed = checkTestOutputFile(expectedProgramOutFile, actualProgramOutFile, "uJVM output");
        passed &= checkTestOutputFile(expectedProgramErrFile, actualProgramErrFile, "uJVM error output");
        return passed;
    }

    @Override
    protected String testName() { return "generator"; }

    @Override
    protected void processTestInputFile(Reader r) throws Exception {
        try {
            Compiler.compile(r);
        } catch (Exception e) {
            if (e.getMessage() != null) {
                System.err.println(e.getMessage());
            } else {
                e.printStackTrace(System.err);
            }
            System.err.println("Failed to compile source file: '" + Compiler.getInputFile().getAbsolutePath() + "'!");
        }
        if (programInFile.exists()) {
            if (!Compiler.getOutputFile().exists()) {
                System.err.println("Object file not found but program test expected!");
            } else {
                // Backup only in stream because out and err are backed up in MJTest.test method.
                InputStream oldIn = System.in;
                PrintStream oldOut = System.out, oldErr = System.err;
                System.setIn(new FileInputStream(programInFile));
                System.setOut(new PrintStream(actualProgramOutFile));
                System.setErr(new PrintStream(actualProgramErrFile));
                // Run previously compiled object file
                Run.main(new String[] { Compiler.getOutputFile().getAbsolutePath() });
                // Restore in stream
                System.in.close();
                System.out.close();
                System.err.close();
                System.setIn(oldIn);
                System.setOut(oldOut);
                System.setErr(oldErr);
            }
        }
    }
}