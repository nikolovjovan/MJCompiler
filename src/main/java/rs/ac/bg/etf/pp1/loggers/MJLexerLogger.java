package rs.ac.bg.etf.pp1.loggers;

import java_cup.runtime.Symbol;

public class MJLexerLogger extends MJLogger {

    private static final String invalidMessage = "Invalid lexer message!";

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
    protected String generate_message(Object... context) {
        MessageType type = get_next_context_object(MessageType.class, context);
        if (type == null) return invalidMessage;
        switch (type) {
            /* DEBUG MESSAGES */
            case SYMBOL_PRINT: {
                Symbol sym = get_next_context_object(Symbol.class, context);
                return sym == null ? invalidMessage : "Found symbol: '" + sym.toString() + "'" + (sym.value != null ? " with value: '" + sym.value + '\'' : "");
            }
            /* ERROR MESSAGES */
            case INT_PARSE_FAIL: {
                String sym = get_next_context_object(String.class, context);
                return sym == null ? invalidMessage : "Failed to parse '" + sym + "' as integer!";
            }
            case INV_SYMBOL: {
                String sym = get_next_context_object(String.class, context);
                return sym == null ? invalidMessage : "Invalid symbol '" + sym + "'!";
            }
            /* ANY OTHER MESSAGE */
            case OTHER: {
                String message = get_next_context_object(String.class, context);
                return message == null ? invalidMessage : message;
            }
            default: return "Unhandled lexer message type: '" + type.name() + "'.";
        }
    }
}