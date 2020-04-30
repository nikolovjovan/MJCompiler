package rs.ac.bg.etf.pp1.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.log4j.*;
import rs.ac.bg.etf.pp1.Compiler;

public class Log4JUtils {

    public static void reset() {
        Logger.getRootLogger().removeAllAppenders();
    }

    public static File prepareLogFile(boolean test) {
        String logFileName = "logs/";
        if (Compiler.getInputFile() != null && Compiler.getInputFile().exists()) {
            logFileName += Compiler.getInputFile().getName() + ".log";
        } else {
            logFileName += "invalid.log";
        }

        File logFile = new File(logFileName);
        if (logFile.exists()) {
            String renamedFileName = logFile.getAbsolutePath();
            renamedFileName = renamedFileName.substring(0, renamedFileName.lastIndexOf('.')) +
                    new SimpleDateFormat("_yyyy-MM-dd'T'HH-mm-ss.SSS'.log'").format(logFile.lastModified());
            if (!logFile.renameTo(new File(renamedFileName))) {
                System.err.println("Could not rename log file!");
            }
        }

        Logger rootLogger = Logger.getRootLogger();
        rootLogger.removeAllAppenders();
        rootLogger.setLevel(Level.DEBUG);
        try {
            RollingFileAppender fAppender = new RollingFileAppender(
                    new PatternLayout(test ? "%-5p - %m%n" : "%-5p %d{ABSOLUTE} - %m%n"), logFile.getAbsolutePath(),
                    false);
            rootLogger.addAppender(fAppender);
        } catch (IOException e) {
            System.err.println("Failed to add a file appender!");
        }

        return logFile;
    }
}