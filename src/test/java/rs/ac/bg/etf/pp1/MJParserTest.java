package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;
import org.apache.log4j.Logger;

import java.io.*;

public class MJParserTest {

    private static String testDir = "test/parser";

    public static void testParser(Logger log) {
        File directory = new File(testDir);
        File[] testInputs = null;

        boolean testsFound = true;

        if (directory.exists()) {
            testInputs = directory.listFiles(((dir, name) -> name.toLowerCase().endsWith(".mj")));
            if (testInputs == null || testInputs.length == 0) testsFound = false;
        } else testsFound = false;

        if (!testsFound) {
            log.warn("No parser tests found in '" + testDir + "'!");
            return;
        }

        Reader br = null;
        try {
            for (File inputFile : testInputs) {
                log.info("Testing parser with input file '" + testDir + "/" + inputFile.getName() + "'.");
                br = new BufferedReader(new FileReader(inputFile));
                MJLexer lexer = new MJLexer(br, inputFile.getName());
                Symbol currToken;
                while ((currToken = lexer.next_token()).sym != sym.EOF) {
                    if (currToken != null) {
                        log.info(currToken.toString() + (currToken.value != null ? " " + currToken.value.toString() : ""));
                    }
                }
            }
        } catch (IOException e) {
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