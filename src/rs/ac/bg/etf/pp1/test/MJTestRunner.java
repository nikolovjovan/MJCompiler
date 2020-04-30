package rs.ac.bg.etf.pp1.test;

public class MJTestRunner {

    public static void main(String[] args) {
        if (args.length > 2) {
            System.err.println("Invalid number of arguments: " + args.length + "!");
            return;
        }
        if (args.length > 0) { // run specific test
            switch (args[0]) {
                case "lexer": {
                    if (args.length > 1) {
                        MJLexerTest.INSTANCE.runSingle(args[1]);
                    } else {
                        MJLexerTest.INSTANCE.runAll();
                    }
                    break;
                }
                case "parser": {
                    if (args.length > 1) {
                        MJParserTest.INSTANCE.runSingle(args[1]);
                    } else {
                        MJParserTest.INSTANCE.runAll();
                    }
                    break;
                }
                case "analyzer": {
                    if (args.length > 1) {
                        MJAnalyzerTest.INSTANCE.runSingle(args[1]);
                    } else {
                        MJAnalyzerTest.INSTANCE.runAll();
                    }
                    break;
                }
                case "generator": {
                    if (args.length > 1) {
                        MJGeneratorTest.INSTANCE.runSingle(args[1]);
                    } else {
                        MJGeneratorTest.INSTANCE.runAll();
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
            MJAnalyzerTest.INSTANCE.runAll();
            MJGeneratorTest.INSTANCE.runAll();
        }
    }
}