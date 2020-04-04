package rs.ac.bg.etf.pp1.loggers;

public class MJCodeGeneratorLogger extends MJLogger {

    private static final String invalidMessage = "Invalid code generator message!";

    public enum MessageType {
        /* DEBUG MESSAGES */
        NODE_VISIT,             // Params: String nodeName
        /* ERROR MESSAGES */
        INV_PROG_SIZE,          // Params: Integer actualSize, Integer maxSize
        /* ANY OTHER MESSAGE */
        OTHER                   // Params: String message
    }

    public MJCodeGeneratorLogger() {
        super("CODE GENERATOR: ");
    }

    @Override
    protected String generate_message(Object... context) {
        MessageType type = get_next_context_object(MessageType.class, context);
        if (type == null) return invalidMessage;
        switch (type) {
            /* DEBUG MESSAGES */
            case NODE_VISIT: {
                String nodeName = get_next_context_object(String.class, context);
                return nodeName == null ? invalidMessage : "Visited node: '" + nodeName + "'.";
            }
            /* ERROR MESSAGES */
            case INV_PROG_SIZE: {
                Integer actualSize = get_next_context_object(Integer.class, context);
                Integer maxSize = get_next_context_object(Integer.class, context);
                return actualSize == null || maxSize == null ? invalidMessage : "Program is too big! Size: " + actualSize + ", max size: " + maxSize + '!';
            }
            /* ANY OTHER MESSAGE */
            case OTHER: {
                String message = get_next_context_object(String.class, context);
                return message == null ? invalidMessage : message;
            }
            default: return "Unhandled semantic analyzer message type: '" + type.name() + "'.";
        }
    }
}