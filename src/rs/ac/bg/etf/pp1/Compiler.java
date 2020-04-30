package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.exceptions.MJCodeGeneratorException;
import rs.ac.bg.etf.pp1.exceptions.MJCompilerException;
import rs.ac.bg.etf.pp1.exceptions.MJSemanticAnalyzerException;
import rs.ac.bg.etf.pp1.mj.runtime.MJCode;
import rs.ac.bg.etf.pp1.symboltable.MJTable;
import rs.ac.bg.etf.pp1.util.CLIUtils;
import rs.ac.bg.etf.pp1.util.Log4JUtils;

import java.io.*;

public class Compiler {

    private static Logger logger = Logger.getLogger(Compiler.class);

    private static boolean debugMode;
    private static File inputFile, outputFile, logFile;

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        Compiler.debugMode = debugMode;
    }

    public static File getInputFile() {
        return inputFile;
    }

    public static void setInputFile(File inputFileName) {
        Compiler.inputFile = inputFileName;
    }

    public static File getOutputFile() {
        return outputFile;
    }

    public static void setOutputFile(File outputFile) {
        Compiler.outputFile = outputFile;
    }

    public static File getLogFile() {
        return logFile;
    }

    public static void setLogFile(File logFile) {
        Compiler.logFile = logFile;
    }

    public static void tsdump() {
        MJTable.dump();
    }

    public static void compile(Reader r) throws Exception {
        System.out.println("Compiling source file: " + inputFile.getAbsolutePath());

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

            System.out.println("====================================================================================================");
            System.out.printf("%5d symbolic constant(s) defined\n", analyzer.getConstCount());
            System.out.printf("%5d global variable(s) defined\n", analyzer.getVarCount());
            System.out.printf("%5d class(es) defined\n", analyzer.getClassCount());
            System.out.printf("%5d global method(s) defined\n", analyzer.getMethodCount());

            tsdump();

            System.out.println("Generating bytecode file: " + outputFile.getAbsolutePath());
            if (outputFile.exists()) {
                outputFile.delete();
            }

            CodeGenerator generator = new CodeGenerator();
            prog.traverseBottomUp(generator);

            if (generator.getErrorCount() == 0) {
                MJCode.write(new FileOutputStream(outputFile));
                if (!MJCode.greska) {
                    System.out.println("Compilation finished!");
                } else {
                    throw new MJCodeGeneratorException("Failed to write bytecode to output file: " + outputFile.getAbsolutePath() + "!");
                }
            } else {
                throw new MJCodeGeneratorException("Code generation failed with " + generator.getErrorCount() + " error(s)!");
            }
        } else {
            throw new MJSemanticAnalyzerException("Semantic analysis failed with " + analyzer.getErrorCount() + " error(s)!");
        }
    }

    public static void main(String[] args) {
        if (!CLIUtils.parseCLIArgs(args)) return;
        Log4JUtils.prepareLogFile(false);
        boolean failure = true;
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            compile(br);
            failure = false;
        } catch (MJCompilerException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        if (failure) {
            System.err.println("Failed to compile source file: '" + inputFile.getAbsolutePath() + "'!");
        }
    }
}