package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.symboltable.MJTable;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;

import java.io.*;

public class Compiler {

    private static boolean debugMode;
    private static String inputFileName, outputFileName;

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        Compiler.debugMode = debugMode;
    }

    public static String getInputFileName() {
        return inputFileName;
    }

    public static void setInputFileName(String inputFileName) {
        Compiler.inputFileName = inputFileName;
    }

    public static String getOutputFileName() {
        return outputFileName;
    }

    public static void setOutputFileName(String outputFileName) {
        Compiler.outputFileName = outputFileName;
    }

    public static void tsdump() {
        MJTable.dump();
    }

    public static void main(String[] args) {
        DOMConfigurator.configure(Log4JUtils.INSTANCE.findLoggerConfigFile());
        if (args.length < 1) {
            System.err.println("Not enough arguments! Usage: MJParser <source-file>");
            return;
        }
        File inputFile = new File(args[0]);
        if (!inputFile.exists()) {
            System.err.println("Source file '" + inputFile.getAbsolutePath() + "' not found!");
            return;
        }
        // TODO: Add debug mode toggle
        debugMode = true;
        inputFileName = args[0];
        // TODO: Add output file name parsing from program argument
        outputFileName = inputFileName.concat(".out");
        Log4JUtils.INSTANCE.prepareLogFile(Logger.getRootLogger());
        Logger log = Logger.getLogger(Compiler.class);
        log.info("Compiling source file: " + inputFile.getAbsolutePath());
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            MJLexer lexer = new MJLexer(br);
            MJParser parser = new MJParser(lexer);
            Program prog = (Program) parser.parse().value;
            log.info("Abstract syntax tree: " + prog.toString(""));
            if (parser.getErrorCount() == 0) {
                System.out.println("Syntax analysis completed without errors!");
            } else {
                System.err.println("Syntax analysis completed with " + parser.getErrorCount() + " errors!");
                System.err.println("Compilation failed!");
            }
            System.out.println("Starting semantic analysis...");
            SemanticAnalyzer analyzer = new SemanticAnalyzer();
            prog.traverseBottomUp(analyzer);
            tsdump();
            if (analyzer.getErrorCount() == 0) {
                System.out.println("Semantic analysis completed without errors!");
                File outputFile = new File(outputFileName);
                log.info("Generating bytecode file: " + outputFile.getAbsolutePath());
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                CodeGenerator generator = new CodeGenerator();
                prog.traverseBottomUp(generator);
                Code.dataSize = analyzer.getVarCount();
                Code.mainPc = generator.getMainPC();
                Code.write(new FileOutputStream(outputFile));
                log.info("Compilation finished!");
            } else {
                System.err.println("Semantic analysis completed with " + analyzer.getErrorCount() + " errors!");
                System.err.println("Compilation failed!");
            }
        } catch (Exception e) {
            log.error("Failed to compile source file: '" + inputFile.getAbsolutePath() + "'!", e);
            e.printStackTrace(System.err);
            System.err.println("Compilation failed!");
        }
    }
}