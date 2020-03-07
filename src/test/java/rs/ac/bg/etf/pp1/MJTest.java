package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;

import java.io.*;

public abstract class MJTest {

    private static final String testBaseDir = "test/";

    protected abstract String testName();

    protected abstract void processTestFile(String fileName, Reader r, Logger log) throws Exception;

    public void runTest(String fileName) {
        Logger log = Logger.getLogger(getClass());

        String testDir = testBaseDir + testName();
        File inputFile = new File(testDir + "/" + fileName);

        if (!inputFile.exists()) {
            log.error("Input test file: '" + fileName + "' not found in '" + testDir + "'!");
            return;
        }

        Reader br = null;
        try {
            log.info("Testing " + testName() + " with input file '" + testDir + "/" + inputFile.getName() + "'.");
            br = new BufferedReader(new FileReader(inputFile));
            processTestFile(fileName, br, log);
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
        Logger log = Logger.getLogger(getClass());

        String testDir = testBaseDir + testName();
        File directory = new File(testDir);
        File[] testInputs = null;
        boolean testsFound = true;

        if (directory.exists()) {
            testInputs = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".mj"));
            if (testInputs == null || testInputs.length == 0) testsFound = false;
        } else testsFound = false;

        if (!testsFound) {
            log.warn("No " + testName() + " tests found in '" + testDir + "'!");
            return;
        }

        log.info("Starting " + testName() + " testing...");
        log.info("===========================================================================");

        Reader br = null;
        try {
            for (File inputFile : testInputs) {
                log.info("Testing " + testName() + " with input file '" + testDir + "/" + inputFile.getName() + "'.");
                br = new BufferedReader(new FileReader(inputFile));
                processTestFile(inputFile.getName(), br, log);
                log.info("===========================================================================");
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