package rs.ac.bg.etf.pp1.util;

import java.io.File;
import java.text.SimpleDateFormat;

import org.apache.log4j.*;
import rs.ac.bg.etf.pp1.Compiler;

public class Log4JUtils {

    public static String getLoggerConfigFileName() {
        return "config/log4j.xml";
    }

    public static File prepareLogFile(Logger root, boolean test) {
        Appender appender = root.getAppender(test ? "test.file" : "file");
        if (!(appender instanceof FileAppender)) return null;
        FileAppender fAppender = (FileAppender) appender;
        String logFileName = fAppender.getFile();
        if (Compiler.getInputFile() != null && Compiler.getInputFile().exists()) {
            logFileName = new File(logFileName).getParentFile().getAbsolutePath() + '\\' + Compiler.getInputFile().getName() + ".log";
        } else {
            logFileName = logFileName.substring(0, logFileName.lastIndexOf('.')) + "-invalid.log";
        }
        File logFile = new File(logFileName);
        if (logFile.exists()) {
            String renamedFileName = logFile.getAbsolutePath();
            renamedFileName = renamedFileName.substring(0, renamedFileName.lastIndexOf('.')) +
                    new SimpleDateFormat("_yyyy-MM-dd'T'HH-mm-ss.SSS'.log'").format(logFile.lastModified());
            if (!logFile.renameTo(new File(renamedFileName))) System.err.println("Could not rename log file!");
        }
        fAppender.setFile(logFile.getAbsolutePath());
        fAppender.activateOptions();
        return logFile;
    }

    public static void prepareLogFile(Logger root) {
        prepareLogFile(root, false);
    }
}