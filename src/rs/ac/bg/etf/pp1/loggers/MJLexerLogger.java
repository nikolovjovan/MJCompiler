package rs.ac.bg.etf.pp1.loggers;

import java_cup.runtime.Symbol;

public class MJLexerLogger extends MJLogger {

    public enum MessageType {
        /* DEBUG MESSAGES */
        SYMBOL_PRINT,           // Params: Symbol sym
        /* ERROR MESSAGES */
        INT_PARSE_FAIL,         // Params: String sym
        INV_SYMBOL,             // Params: String sym
        /* ANY OTHER MESSAGE */
        OTHER                   // Params: String message
    }

    public MJLexerLogger() {
        super("LEXER: ");
    }

    @Override
    protected String generateMessage(Object... context) {
        MessageType type = getNextContextObject(MessageType.class, context);
        if (type == null) return generateInvalidMessage(null);
        switch (type) {
            /* DEBUG MESSAGES */
            case SYMBOL_PRINT: {
                Symbol sym = getNextContextObject(Symbol.class, context);
                if (sym == null) break;
                return "Found symbol: '" + sym.toString() + "'" +
                        (sym.value != null ? " with value: '" + sym.value + '\'' : "");
            }
            /* ERROR MESSAGES */
            case INT_PARSE_FAIL: {
                String sym = getNextContextObject(String.class, context);
                if (sym == null) break;
                return "Failed to parse '" + sym + "' as integer!";
            }
            case INV_SYMBOL: {
                String sym = getNextContextObject(String.class, context);
                if (sym == null) break;
                return "Invalid symbol '" + sym + "'!";
            }
            /* ANY OTHER MESSAGE */
            case OTHER: {
                String message = getNextContextObject(String.class, context);
                if (message == null) break;
                return message;
            }
        }
        return generateInvalidMessage(type.name());
    }
}