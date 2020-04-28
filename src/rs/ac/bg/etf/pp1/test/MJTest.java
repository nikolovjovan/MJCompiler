package rs.ac.bg.etf.pp1.test;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.Compiler;
import rs.ac.bg.etf.pp1.util.Log4JUtils;

import java.io.*;

public abstract class MJTest {

    private static final String testBaseDir = "test/";

    protected Logger logger = Logger.getLogger(getClass());

    protected File expectedLogFile, expectedOutFile, expectedErrFile, actualLogFile, actualOutFile, actualErrFile;

    protected abstract String testName();

    protected void initializeFiles() {
        String inputFilePath = Compiler.getInputFile().getAbsolutePath();
        expectedLogFile = new File(inputFilePath.concat(".log"));
        expectedOutFile = new File(inputFilePath.concat(".out"));
        expectedErrFile = new File(inputFilePath.concat(".err"));
        String actualFileBasePath = Compiler.getLogFile().getAbsolutePath();
        actualFileBasePath = actualFileBasePath.substring(0, actualFileBasePath.length() - 4);
        actualLogFile = Compiler.getLogFile();
        actualOutFile = new File(actualFileBasePath.concat(".out"));
        actualErrFile = new File(actualFileBasePath.concat(".err"));
    }

    protected abstract void processTestFile(Reader r) throws Exception;

    protected boolean checkTestPassed() {
        return filesIdentical(expectedLogFile, actualLogFile) &&
                filesIdentical(expectedOutFile, actualOutFile) &&
                filesIdentical(expectedErrFile, actualErrFile);
    }

    protected boolean filesIdentical(File expectedFile, File actualFile) {
        try (BufferedReader ebr = new BufferedReader(new FileReader(expectedFile));
             BufferedReader abr = new BufferedReader(new FileReader(actualFile))) {
            String expectedLine, actualLine;
            while ((expectedLine = ebr.readLine()) != null && (actualLine = abr.readLine()) != null) {
                if (!actualLine.equals(expectedLine)) return false;
            }
            return ebr.readLine() == null && abr.readLine() == null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public void test(String inputFileName, Reader br) throws Exception {
        System.out.println("Testing " + testName() + " with input file '" + inputFileName + "'.");
        PrintStream oldOut = System.out, oldErr = System.err;
        System.setOut(new PrintStream(actualOutFile));
        System.setErr(new PrintStream(actualErrFile));
        processTestFile(br);
        System.setOut(oldOut);
        System.setErr(oldErr);
        System.out.println("Test " + (checkTestPassed() ? "PASSED" : "FAILED"));
    }

    public void runTest(String fileName) {
        String testDir = testBaseDir + testName();
        File inputFile = new File(testDir + '/' + fileName);

        Compiler.setDebugMode(true);
        Compiler.setInputFile(inputFile);
        Compiler.setOutputFile(new File(testBaseDir + "/program.obj"));
        Compiler.setLogFile(Log4JUtils.prepareLogFile(Logger.getRootLogger(), true));

        if (!inputFile.exists()) {
            System.err.println("Input test file: '" + fileName + "' not found in '" + testDir + "'!");
            return;
        }

        Reader br = null;
        try {
            initializeFiles();
            br = new BufferedReader(new FileReader(inputFile));
            test(testDir + '/' + inputFile.getName(), br);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.err.println("Test failed!");
        } finally {
            if (br != null) {
                try {
                    br.close();
                    System.out.println("Test finished!");
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    System.err.println("Test failed!");
                }
            }
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
        Reader br = null;
        try {
            for (File inputFile : testInputs) {
                Compiler.setInputFile(inputFile);
                Compiler.setOutputFile(new File(inputFile.getAbsolutePath().substring(0, inputFile.getAbsolutePath().length() - 3).concat(".obj")));
                Compiler.setLogFile(Log4JUtils.prepareLogFile(Logger.getRootLogger(), true));
                initializeFiles();
                br = new BufferedReader(new FileReader(inputFile));
                test(testDir + '/' + inputFile.getName(), br);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.err.println("Test failed!");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    System.err.println("Test failed!");
                }
            }
        }
    }
}