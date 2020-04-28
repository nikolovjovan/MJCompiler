package rs.ac.bg.etf.pp1.loggers;

public class MJCodeGeneratorLogger extends MJLogger {

    public enum MessageType {
        /* DEBUG MESSAGES */
        NODE_VISIT,             // Params: String nodeName
        /* ERROR MESSAGES */
        INV_LOAD_PARAM,         // Params: String kindName
        INV_STORE_PARAM,        // Params: String symName
        INV_CODE_SIZE,          // Params: Integer actualSize, Integer maxSize
        /* ANY OTHER MESSAGE */
        OTHER                   // Params: String message
    }

    public MJCodeGeneratorLogger() {
        super("CODE GENERATOR: ");
    }

    @Override
    protected String generateMessage(Object... context) {
        MessageType type = getNextContextObject(MessageType.class, context);
        if (type == null) return generateInvalidMessage(null);
        switch (type) {
            /* DEBUG MESSAGES */
            case NODE_VISIT: {
                String nodeName = getNextContextObject(String.class, context);
                if (nodeName == null) break;
                return "Visited node: '" + nodeName + "'.";
            }
            /* ERROR MESSAGES */
            case INV_LOAD_PARAM: {
                String kindName = getNextContextObject(String.class, context);
                if (kindName == null) break;
                return "Invalid MJCode.load() operand kind: " + kindName + '!';
            }
            case INV_STORE_PARAM: {
                String symName = getNextContextObject(String.class, context);
                if (symName == null) break;
                return '\'' + symName + "' is not a variable, an array element or a class field!";
            }
            case INV_CODE_SIZE: {
                Integer actualSize = getNextContextObject(Integer.class, context);
                Integer maxSize = getNextContextObject(Integer.class, context);
                if (actualSize == null || maxSize == null) break;
                return "Program is too big! Size: " + actualSize + ", max size: " + maxSize + '!';
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