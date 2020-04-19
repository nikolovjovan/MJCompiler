package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.exceptions.MJCodeGeneratorException;
import rs.ac.bg.etf.pp1.exceptions.MJSemanticAnalyzerException;
import rs.ac.bg.etf.pp1.mj.runtime.MJCode;
import rs.ac.bg.etf.pp1.symboltable.MJTable;
import rs.ac.bg.etf.pp1.util.CLIUtils;
import rs.ac.bg.etf.pp1.util.Log4JUtils;

import java.io.*;

public class Compiler {

    private static Logger logger = Logger.getLogger(Compiler.class);

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

    public static void compile(Reader r) throws Exception {
        MJLexer lexer = new MJLexer(r);
        MJParser parser = new MJParser(lexer);

        Program prog = (Program) parser.parse().value;
        logger.info("Abstract syntax tree: " + prog.toString(""));

        if (parser.getErrorCount() == 0) {
            System.out.println("Syntax analysis completed without errors!");
        } else {
            System.err.println("Syntax analysis completed with " + parser.getErrorCount() + " errors!");
        }

        System.out.println("Starting semantic analysis...");
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        prog.traverseBottomUp(analyzer);

        if (analyzer.getErrorCount() == 0) {
            System.out.println("Semantic analysis completed without errors!");

            tsdump();

            File outputFile = new File(outputFileName);
            logger.info("Generating bytecode file: " + outputFile.getAbsolutePath());
            if (outputFile.exists()) {
                outputFile.delete();
            }

            CodeGenerator generator = new CodeGenerator();
            prog.traverseBottomUp(generator);

            if (generator.getErrorCount() == 0) {
                MJCode.dataSize = analyzer.getVarCount();
                MJCode.mainPc = generator.getMainPC();
                MJCode.write(new FileOutputStream(outputFile));
                if (MJCode.greska) {
                    throw new MJCodeGeneratorException("Failed to write bytecode to output file: " + outputFile.getAbsolutePath() + "!");
                }
            } else {
                throw new MJCodeGeneratorException("Code generation failed with " + generator.getErrorCount() + " error(s)!");
            }

            logger.info("Compilation finished!");
        } else {
            throw new MJSemanticAnalyzerException("Semantic analysis failed with " + analyzer.getErrorCount() + " error(s)!");
        }
    }

    public static void main(String[] args) {
        DOMConfigurator.configure(Log4JUtils.INSTANCE.getLoggerConfigFileName());

        if (!CLIUtils.parseCLIArgs(args)) return;
        File inputFile = new File(inputFileName);

        Log4JUtils.INSTANCE.prepareLogFile(Logger.getRootLogger());

        logger.info("Compiling source file: " + inputFile.getAbsolutePath());
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            compile(br);
        } catch (Exception e) {
            logger.error("Failed to compile source file: '" + inputFile.getAbsolutePath() + "'!", e);
            System.err.println("Compilation failed!");
            e.printStackTrace(System.err);
        }
    }
}