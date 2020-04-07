package rs.ac.bg.etf.pp1.test;

import org.apache.log4j.xml.DOMConfigurator;
import rs.ac.bg.etf.pp1.util.Log4JUtils;

public class MJTestRunner {

    public static void main(String[] args) {
        DOMConfigurator.configure(Log4JUtils.INSTANCE.getLoggerConfigFileName());
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
                case "semantic": {
                    if (args.length > 1) {
                        MJSemanticTest.INSTANCE.runTest(args[1]);
                    } else {
                        MJSemanticTest.INSTANCE.runAll();
                    }
                    break;
                }
                case "codegen": {
                    if (args.length > 1) {
                        MJCodeGenTest.INSTANCE.runTest(args[1]);
                    } else {
                        MJCodeGenTest.INSTANCE.runAll();
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
            MJSemanticTest.INSTANCE.runAll();
            MJCodeGenTest.INSTANCE.runAll();
        }
    }

}