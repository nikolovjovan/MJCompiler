package rs.ac.bg.etf.pp1.loggers;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.Compiler;

public abstract class MJLogger {

    protected final Logger logger = Logger.getLogger(getClass());
    protected String messageHeader;
    protected int contextIndex = 0;

    protected MJLogger(String messageHeader) {
        this.messageHeader = messageHeader;
    }

    protected abstract String generateMessage(Object... context);

    protected <TObject> TObject getContextObject(int index, Class<TObject> type, Object... context) {
        if (context.length <= index || context[index] == null || !(context[index].getClass().equals(type))) return null;
        return type.cast(context[index]);
    }

    protected <TObject> TObject getNextContextObject(Class<TObject> type, Object... context) {
        return getContextObject(contextIndex++, type, context);
    }

    protected String formatMessage(String message, int line, int column) {
        StringBuilder messageBuilder = new StringBuilder();
        if (line <= 0) return messageBuilder.append(messageHeader).append(message).toString();
        if (column <= 0) return messageBuilder.append(Compiler.getInputFileName()).append(':').append(line).append(": ").append(messageHeader).append(message).toString();
        return messageBuilder.append(Compiler.getInputFileName()).append(':').append(line).append(':').append(column).append(": ").append(messageHeader).append(message).toString();
    }

    public final void log(Level level, int line, int column, Object... context) {
        if (level == Level.DEBUG && !Compiler.isDebugMode()) return;
        logger.log(level, formatMessage(generateMessage(context), line, column));
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