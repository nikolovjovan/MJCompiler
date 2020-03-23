package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.util.Log4JUtils;

import java.io.*;

public abstract class MJTest {

    private static final String testBaseDir = "test/";

    protected Logger log = Logger.getLogger(getClass());

    protected abstract String testName();

    protected abstract void processTestFile(Reader r) throws Exception;

    public void runTest(String fileName) {
        String testDir = testBaseDir + testName();
        File inputFile = new File(testDir + "/" + fileName);

        if (!inputFile.exists()) {
            System.err.println("Input test file: '" + fileName + "' not found in '" + testDir + "'!");
            return;
        }

        Compiler.setDebugMode(true);
        Compiler.setInputFileName(fileName);
        Compiler.setOutputFileName(fileName.concat(".out"));

        Log4JUtils.INSTANCE.prepareLogFile(Logger.getRootLogger());

        Reader br = null;
        try {
            log.info("Testing " + testName() + " with input file '" + testDir + "/" + inputFile.getName() + "'.");
            br = new BufferedReader(new FileReader(inputFile));
            processTestFile(br);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
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
                Compiler.setInputFileName(inputFile.getName());
                Compiler.setOutputFileName(inputFile.getName().concat(".out"));
                Log4JUtils.INSTANCE.prepareLogFile(Logger.getRootLogger());
                log.info("Testing " + testName() + " with input file '" + testDir + "/" + inputFile.getName() + "'.");
                br = new BufferedReader(new FileReader(inputFile));
                processTestFile(br);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}