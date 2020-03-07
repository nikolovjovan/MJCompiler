package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.util.Log4JUtils;

import java.io.*;

public class Compiler {

    static {
        DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
        Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
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
            log.info(prog.toString());
        } catch (Exception e) {
            log.error("Failed to compile source file: '" + inputFile.getAbsolutePath() + "'!", e);
        }
    }

}