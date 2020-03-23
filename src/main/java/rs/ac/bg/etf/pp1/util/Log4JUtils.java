package rs.ac.bg.etf.pp1.util;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.*;
import rs.ac.bg.etf.pp1.Compiler;

public class Log4JUtils {

    public static Log4JUtils INSTANCE = new Log4JUtils();

    public URL findLoggerConfigFile() {
        return Thread.currentThread().getContextClassLoader().getResource("log4j.xml");
    }

    public void prepareLogFile(Logger root) {
        Appender appender = root.getAppender("file");
        if (!(appender instanceof FileAppender)) return;
        FileAppender fAppender = (FileAppender) appender;
        String logFileName = fAppender.getFile();
        String inputFileName = Compiler.getInputFileName();
        if (inputFileName != null && !inputFileName.isEmpty()) {
            int slashIdx = logFileName.lastIndexOf('/') + 1;
            if (slashIdx == 0) slashIdx = logFileName.lastIndexOf('\\') + 1;
            logFileName = logFileName.substring(0, slashIdx) + inputFileName + ".log";
        } else {
            logFileName = logFileName.substring(0, logFileName.lastIndexOf('.')) + "-test.log";
        }
        File logFile = new File(logFileName);
        String renamedFileName = logFile.getAbsolutePath();
        renamedFileName = renamedFileName.substring(0, renamedFileName.lastIndexOf('.'));
        renamedFileName += new SimpleDateFormat("_yyyy-MM-dd'T'HH-mm-ss.SSS'.log'").format(new Date());
        File renamedFile = new File(renamedFileName);
        if (logFile.exists() && !logFile.renameTo(renamedFile)) System.err.println("Could not rename log file!");
        fAppender.setFile(logFile.getAbsolutePath());
        fAppender.activateOptions();
    }
}