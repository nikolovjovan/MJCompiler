package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import rs.ac.bg.etf.pp1.util.Log4JUtils;

public class MJTestRunner {

    static {
        DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
        Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
    }

    public static void main(String[] args) {
        if (args.length > 2) {
            System.err.println("Invalid number of arguments: " + args.length + "!");
            return;
        }
        if (args.length > 0) { // run specific test
            switch (args[0]) {
                case "lexer": {
                    if (args.length > 1) {
                        MJLexerTest.INSTANCE.runTest(args[1]);
                    } else {
                        MJLexerTest.INSTANCE.runAll();
                    }
                    break;
                }
                case "parser": {
                    if (args.length > 1) {
                        MJParserTest.INSTANCE.runTest(args[1]);
                    } else {
                        MJParserTest.INSTANCE.runAll();
                    }
                    break;
                }
                default: {
                    System.err.println("Invalid option '" + args[0] + "'!");
                    break;
                }
            }
        } else { // run all tests
            MJLexerTest.INSTANCE.runAll();
            MJParserTest.INSTANCE.runAll();
        }
    }

}