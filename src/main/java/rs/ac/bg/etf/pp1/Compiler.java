package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.symboltable.MyTab;
import rs.ac.bg.etf.pp1.util.Log4JUtils;

import java.io.*;

public class Compiler {

    static {
        DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
        Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
    }

    public static void tsdump() {
        MyTab.dump();
    }

    public static void main(String[] args) {
        Logger log = Logger.getLogger(Compiler.class);
        if (args.length < 1) {
            System.err.println("Not enough arguments! Usage: MJParser <source-file>");
            return;
        }
        File inputFile = new File(args[0]);
        if (!inputFile.exists()) {
            System.err.println("Source file '" + inputFile.getAbsolutePath() + "' not found!");
            return;
        }
        log.info("Compiling source file: " + inputFile.getAbsolutePath());
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            MJLexer lexer = new MJLexer(br, inputFile.getName());
            MJParser parser = new MJParser(lexer);
            Program prog = (Program) parser.parse().value;
            log.info("Abstract syntax tree: " + prog.toString(""));
            if (parser.getErrorCount() == 0) {
                System.out.println("Syntax analysis completed without errors!");
                System.out.println("Starting semantic analysis...");
                SemanticAnalyzer analyzer = new SemanticAnalyzer(inputFile.getName());
                prog.traverseBottomUp(analyzer);
                if (analyzer.getErrorCount() == 0) {
                    System.out.println("Semantic analysis completed without errors!");
                    // TODO: Start code generation here
                } else {
                    System.err.println("Semantic analysis completed with " + analyzer.getErrorCount() + " errors!");
                    System.err.println("Compilation failed!");
                }
                tsdump();
            } else {
                System.err.println("Syntax analysis completed with " + parser.getErrorCount() + " errors!");
                System.err.println("Compilation failed!");
            }
        } catch (Exception e) {
            log.error("Failed to compile source file: '" + inputFile.getAbsolutePath() + "'!", e);
            e.printStackTrace(System.err);
            System.err.println("Compilation failed!");
        }
    }
}