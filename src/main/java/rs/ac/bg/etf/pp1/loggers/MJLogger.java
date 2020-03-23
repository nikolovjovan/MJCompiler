package rs.ac.bg.etf.pp1.loggers;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.Compiler;

public abstract class MJLogger {

    public enum Level {
        DEBUG, INFO, WARN, ERROR, FATAL
    }

    protected final Logger log = Logger.getLogger(getClass());
    protected String messageHeader;
    protected int contextIndex = 0;

    protected MJLogger(String messageHeader) {
        this.messageHeader = messageHeader;
    }

    protected abstract String generate_message(Object... context);

    protected <TObject> TObject get_context_object(int index, Class<TObject> type, Object... context) {
        if (context.length <= index || !(context[index].getClass().equals(type))) return null;
        return type.cast(context[index]);
    }

    protected <TObject> TObject get_next_context_object(Class<TObject> type, Object... context) {
        return get_context_object(contextIndex++, type, context);
    }

    protected String format_message(String message, int line, int column) {
        StringBuilder messageBuilder = new StringBuilder();
        if (line <= 0) return messageBuilder.append(messageHeader).append(message).toString();
        if (column <= 0) return messageBuilder.append(Compiler.getInputFileName()).append(':').append(line).append(": ").append(messageHeader).append(message).toString();
        return messageBuilder.append(Compiler.getInputFileName()).append(':').append(line).append(':').append(column).append(": ").append(messageHeader).append(message).toString();
    }

    public final void log(Level level, String message) {
        switch (level) {
            case DEBUG: {
                if (!Compiler.isDebugMode()) return;
                log.debug(message);
                System.out.println("DEBUG: ".concat(message));
                return;
            }
            case INFO: {
                log.info(message);
                System.out.println("INFO: ".concat(message));
                return;
            }
            case WARN: {
                log.warn(message);
                System.out.println("WARN: ".concat(message));
                return;
            }
            case ERROR: {
                log.error(message);
                System.err.println("ERROR: ".concat(message));
                return;
            }
            case FATAL: {
                log.fatal(message);
                System.err.println("FATAL: ".concat(message));
            }
        }
    }

    public final void debug(String message) {
        log(Level.DEBUG, message);
    }

    public final void info(String message) {
        log(Level.INFO, message);
    }

    public final void warn(String message) {
        log(Level.WARN, message);
    }

    public final void error(String message) {
        log(Level.ERROR, message);
    }

    public final void fatal(String message) {
        log(Level.FATAL, message);
    }

    public final void log(Level level, int line, int column, Object... context) {
        log(level, format_message(generate_message(context), line, column));
        contextIndex = 0; // Reset context index for next generate_message call
    }

    public final void debug(int line, int column, Object... context) {
        log(Level.DEBUG, line, column, context);
    }

    public final void info(int line, int column, Object... context) {
        log(Level.INFO, line, column, context);
    }

    public final void warn(int line, int column, Object... context) {
        log(Level.WARN, line, column, context);
    }

    public final void error(int line, int column, Object... context) {
        log(Level.ERROR, line, column, context);
    }

    public final void fatal(int line, int column, Object... context) {
        log(Level.FATAL, line, column, context);
    }
}