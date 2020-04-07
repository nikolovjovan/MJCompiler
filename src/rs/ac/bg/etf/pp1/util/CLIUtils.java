package rs.ac.bg.etf.pp1.util;

import rs.ac.bg.etf.pp1.Compiler;

import java.io.File;
import java.io.PrintStream;

public class CLIUtils {

    public static void printUsage(PrintStream stream) {
        stream.println("usage: mjc [options] input_file");
        stream.println("  options:");
        stream.println("    -d, --debug                 Enables logging of debug messages.");
        stream.println("    -h, --help                  Prints this message.");
        stream.println("    -o, --output output_file    Specifies output file name.");
    }

    private static void printError(String message) {
        System.err.println("Error! " + message);
        printUsage(System.err);
    }

    public static boolean parseCLIArgs(String[] args) {
        if (args.length < 1) {
            printError("Not enough arguments!");
            return false;
        }
        if (args[args.length - 1].charAt(0) == '-') {
            if (args[args.length - 1].equals("-h") || args[args.length - 1].equals("--help")) {
                printUsage(System.out);
            } else {
                printError("Illegal input file name: " + args[args.length - 1]);
            }
            return false;
        } else {
            File inputFile = new File(args[args.length - 1]);
            if (!inputFile.exists()) {
                printError("Input file '" + inputFile.getAbsolutePath() + "' does not exist!");
                return false;
            }
            Compiler.setInputFileName(args[args.length - 1]);
        }
        for (int i = 0; i < args.length - 1; ++i) {
            switch (args[i]) {
                case "-d": case "--debug": {
                    Compiler.setDebugMode(true);
                    break;
                }
                case "-h": case "--help": {
                    printUsage(System.out);
                    // Used to prevent program from continuing and just print usage info
                    return false;
                }
                case "-o": case "--output": {
                    if (i < args.length - 2) {
                        if (args[i + 1].charAt(0) == '-') {
                            printError("Illegal output file name: " + args[i + 1]);
                            return false;
                        } else {
                            Compiler.setOutputFileName(args[++i]);
                        }
                    } else {
                        printError("Output file name missing!");
                        return false;
                    }
                    break;
                }
                default: {
                    printError("Invalid option: " + args[i]);
                    return false;
                }
            }
        }
        if (Compiler.getOutputFileName() == null) {
            Compiler.setOutputFileName(Compiler.getInputFileName().concat(".obj"));
        }
        return true;
    }
}