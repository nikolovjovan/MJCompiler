package rs.ac.bg.etf.pp1.loggers;

public class MJParserLogger extends MJLogger {

    private static final String invalidMessage = "Invalid parser message!";

    public enum MessageType {
        /* ERROR MESSAGES */
        SYNTAX_ERROR,           // Params:
        INV_CLS_BASE_TYPE,      // Params:
        INV_ABS_CLS_METH_DECL,  // Params:
        INV_FORMAL_PARAM,       // Params:
        INV_LOCAL_VAR_DECL,     // Params:
        INV_ASSIGN_EXPR,        // Params:
        INV_FOR_STMT_COND,      // Params:
        /* FATAL MESSAGES */
        IRR_SYNTAX_ERROR,       // Params:
        /* ANY OTHER MESSAGE */
        OTHER                   // Params: String message
    }

    public MJParserLogger() {
        super("PARSER: ");
    }

    @Override
    protected String generate_message(Object... context) {
        MessageType type = get_next_context_object(MessageType.class, context);
        if (type == null) return invalidMessage;
        switch (type) {
            /* ERROR MESSAGES */
            case SYNTAX_ERROR:          return "Syntax error!";
            case INV_CLS_BASE_TYPE:     return "Invalid class base type declaration! Parsing continued...";
            case INV_ABS_CLS_METH_DECL: return "Invalid abstract class method declaration! Parsing continued...";
            case INV_FORMAL_PARAM:      return "Invalid formal parameter! Parsing continued...";
            case INV_LOCAL_VAR_DECL:    return "Invalid local variable declaration! Parsing continued...";
            case INV_ASSIGN_EXPR:       return "Invalid assignment expression! Parsing continued...";
            case INV_FOR_STMT_COND:     return "Invalid for statement condition! Parsing continued...";
            /* FATAL MESSAGES */
            case IRR_SYNTAX_ERROR:      return "Irrecoverable syntax error! Parsing aborted!";
            /* ANY OTHER MESSAGE */
            case OTHER: {
                String message = get_next_context_object(String.class, context);
                return message == null ? invalidMessage : message;
            }
            default: return "Unhandled parser message type: '" + type.name() + "'.";
        }
    }
}