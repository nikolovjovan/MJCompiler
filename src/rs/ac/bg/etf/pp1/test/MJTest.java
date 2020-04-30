package rs.ac.bg.etf.pp1.test;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.Compiler;
import rs.ac.bg.etf.pp1.util.Log4JUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class MJTest {

    private static final String testBaseDir = "test/";

    protected Logger logger = Logger.getLogger(getClass());

    protected String expectedFileBasePath, actualFileBasePath;

    protected File expectedLogFile, expectedOutFile, expectedErrFile;
    protected File actualLogFile, actualOutFile, actualErrFile;

    protected List<File> filesToDelete = new ArrayList<>();

    protected abstract String testName();

    protected void initializeFiles() {
        expectedFileBasePath = Compiler.getInputFile().getAbsolutePath();
        expectedLogFile = new File(expectedFileBasePath.concat(".log"));
        expectedOutFile = new File(expectedFileBasePath.concat(".out"));
        expectedErrFile = new File(expectedFileBasePath.concat(".err"));
        actualFileBasePath = Compiler.getLogFile().getAbsolutePath();
        actualFileBasePath = actualFileBasePath.substring(0, actualFileBasePath.length() - 4); // remove '.log'
        actualLogFile = Compiler.getLogFile();
        actualOutFile = new File(actualFileBasePath.concat(".out"));
        actualErrFile = new File(actualFileBasePath.concat(".err"));
    }

    protected abstract void processTestInputFile(Reader r) throws Exception;

    protected boolean checkTestOutputFile(File expectedFile, File actualFile, String desc) {
        if (!expectedFile.exists()) return true;
        if (!actualFile.exists()) {
            System.err.println(desc + " file not found!");
            return false;
        }
        try (BufferedReader ebr = new BufferedReader(new FileReader(expectedFile));
             BufferedReader abr = new BufferedReader(new FileReader(actualFile))) {
            String expectedLine, actualLine;
            boolean identical = true;
            while ((expectedLine = ebr.readLine()) != null && (actualLine = abr.readLine()) != null) {
                if (!actualLine.equals(expectedLine)) {
                    String regex = "^Completion took \\d+ ms$";
                    if (ebr.readLine() != null || abr.readLine() != null ||
                            !expectedLine.matches(regex) || !actualLine.matches(regex)) {
                        identical = false;
                    }
                    break;
                }
            }
            if (identical && ebr.readLine() == null && abr.readLine() == null) {
                filesToDelete.add(actualFile);
                return true;
            }
        } catch (Exception ignored) {}
        System.err.println(desc + " files not identical!");
        return false;
    }

    protected boolean checkTestPassed() {
        boolean passed = checkTestOutputFile(expectedLogFile, actualLogFile, "Compiler log");
        passed &= checkTestOutputFile(expectedOutFile, actualOutFile, "Compiler output");
        passed &= checkTestOutputFile(expectedErrFile, actualErrFile, "Compiler error output");
        return passed;
    }

    protected void deleteFiles() {
        for (File file : filesToDelete) {
            file.delete();
        }
        filesToDelete.clear();
    }

    protected void test(String inputFileName) throws Exception {
        initializeFiles();
        System.out.println("Testing " + testName() + " with input file '" + inputFileName + "'.");
        PrintStream oldOut = System.out, oldErr = System.err;
        System.setOut(new PrintStream(actualOutFile));
        System.setErr(new PrintStream(actualErrFile));
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            processTestInputFile(br);
        }
        Log4JUtils.reset();
        System.out.close();
        System.err.close();
        System.setOut(oldOut);
        System.setErr(oldErr);
        System.out.println("Test " + (checkTestPassed() ? "PASSED" : "FAILED"));
        deleteFiles();
    }

    public void runSingle(String fileName) {
        String testDir = testBaseDir + testName();
        File inputFile = new File(testDir + '/' + fileName);

        Compiler.setDebugMode(true);
        Compiler.setInputFile(inputFile);
        Compiler.setOutputFile(new File(testBaseDir + "/program.obj"));
        Compiler.setLogFile(Log4JUtils.prepareLogFile(true));

        if (!inputFile.exists()) {
            System.err.println("Input test file: '" + fileName + "' not found in '" + testDir + "'!");
            return;
        }

        try {
            test(testDir + '/' + inputFile.getName());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.err.println("Test failed to complete!");
        }
    }

    public void runAll() {
        String testDir = testBaseDir + testName();
        File directory = new File(testDir);
        File[] testInputs = null;
        boolean testsFound = true;

        if (directory.exists()) {
            testInputs = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".mj"));
            if (testInputs == null || testInputs.length == 0) testsFound = false;
        } else testsFound = false;

        if (!testsFound) {
            System.err.println("No " + testName() + " tests found in '" + testDir + "'!");
            return;
        }

        Compiler.setDebugMode(true);
        try {
            for (File inputFile : testInputs) {
                Compiler.setInputFile(inputFile);
                Compiler.setOutputFile(new File(testBaseDir.concat(
                        inputFile.getName().substring(0, inputFile.getName().length() - 3)).concat(".obj")));
                Compiler.setLogFile(Log4JUtils.prepareLogFile(true));
                test(testDir + '/' + inputFile.getName());
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.err.println("Tests failed to complete!");
        }
    }
}