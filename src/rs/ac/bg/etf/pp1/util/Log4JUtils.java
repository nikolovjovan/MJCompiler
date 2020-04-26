package rs.ac.bg.etf.pp1.util;

import java.io.File;
import java.text.SimpleDateFormat;

import org.apache.log4j.*;
import rs.ac.bg.etf.pp1.Compiler;

public class Log4JUtils {

    public static Log4JUtils INSTANCE = new Log4JUtils();

    public String getLoggerConfigFileName() {
        return "config/log4j.xml";
    }

    public void prepareLogFile(Logger root) {
        Appender appender = root.getAppender("file");
        if (!(appender instanceof FileAppender)) return;
        FileAppender fAppender = (FileAppender) appender;
        String logFileName = fAppender.getFile();
        String inputFileName = Compiler.getInputFileName();
        if (inputFileName != null && !inputFileName.isEmpty()) {
            logFileName = new File(logFileName).getParentFile().getAbsolutePath() + '\\' + new File(inputFileName).getName() + ".log";
        } else {
            logFileName = logFileName.substring(0, logFileName.lastIndexOf('.')) + "-test.log";
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
    }
}