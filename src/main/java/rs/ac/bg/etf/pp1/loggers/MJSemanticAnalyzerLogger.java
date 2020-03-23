package rs.ac.bg.etf.pp1.loggers;

import rs.ac.bg.etf.pp1.symboltable.concepts.MJObj;

// TODO: Move more messages from SemanticAnalyzer here...

public class MJSemanticAnalyzerLogger extends MJLogger {

    private static final String invalidMessage = "Invalid semantic analyzer message!";

    public enum MessageType {
        /* DEBUG MESSAGES */
        NODE_VISIT,             // Params: String nodeName
        CUR_ACC_MOD,            // Params: String accessName
        /* INFO MESSAGES */
        DEF_OBJ,                // Params: String kindName, MJObj obj
        /* ERROR MESSAGES */
        INV_OBJ_CHECK_REACHED,  // Params: String s1, String s1
        SYM_NOT_DEF,            // Params: String s1, String s2
        SYM_DEF_INV_KIND,       // Params: String s1, String s2, String s3
        SYM_IN_USE,             // Params: String symName
        INV_OBJ,                // Params: String objName
        TYPE_NOT_BASIC,         // Params: String kindName
        INCOMPATIBLE_TYPES,     // Params: String type1Name, String type2Name
        MISPLACED_BREAK,        // Params:
        MISPLACED_CONTINUE,     // Params:
        MISPLACED_RETURN,       // Params:
        INV_COMPILER_OBJ,       // Params: String objectName, Object object
        ITERATOR_IN_USE,        // Params: String varName
        /* ANY OTHER MESSAGE */
        OTHER                   // Params: String message
    }

    public MJSemanticAnalyzerLogger() {
        super("SEMANTIC ANALYZER: ");
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
            case CUR_ACC_MOD: {
                String accName = get_next_context_object(String.class, context);
                return accName == null ? invalidMessage : "Current access modifier: " + accName + '.';
            }
            /* INFO MESSAGES */
            case DEF_OBJ: {
                String kindName = get_next_context_object(String.class, context);
                MJObj obj = get_next_context_object(MJObj.class, context);
                return kindName == null || obj == null ? invalidMessage : "Defined " + kindName + " '" + obj.getName() + "'. Object string: " + obj;
            }
            /* ERROR MESSAGES */
            case INV_OBJ_CHECK_REACHED: {
                String s1 = get_next_context_object(String.class, context);
                String s2 = get_next_context_object(String.class, context);
                return s1 == null || s2 == null ? invalidMessage : s1 + " not initialized but " + s2 + " reached!";
            }
            case SYM_NOT_DEF: {
                String s1 = get_next_context_object(String.class, context);
                String s2 = get_next_context_object(String.class, context);
                s1 = s1 == null ? "" : s1 + ' ';
                s2 = s2 == null ? "" : '\'' + s2 + "' ";
                return s1.isEmpty() && s2.isEmpty() ? invalidMessage : s1 + s2 + "is not defined!";
            }
            case SYM_DEF_INV_KIND: {
                String s1 = get_next_context_object(String.class, context);
                String s2 = get_next_context_object(String.class, context);
                s1 = s1 == null ? "" : s1 + ' ';
                s2 = s2 == null ? "" : '\'' + s2 + "' ";
                String s3 = get_next_context_object(String.class, context);
                return s1.isEmpty() && s2.isEmpty() || s3 == null ? invalidMessage : s1 + s2 + "is not a " + s3 + '!';
            }
            case SYM_IN_USE: {
                String symName = get_next_context_object(String.class, context);
                return symName == null ? invalidMessage : "'" + symName + "' is already in use!";
            }
            case INV_OBJ: {
                String objName = get_next_context_object(String.class, context);
                return objName == null ? invalidMessage : objName + " is invalid!";
            }
            case TYPE_NOT_BASIC: {
                String kindName = get_next_context_object(String.class, context);
                return kindName == null ? invalidMessage : kindName + " must be of basic type: int, char or bool!";
            }
            case INCOMPATIBLE_TYPES: {
                String type1Name = get_next_context_object(String.class, context);
                String type2Name = get_next_context_object(String.class, context);
                return type1Name == null || type2Name == null ? invalidMessage : type1Name + " incompatible with " + type2Name + 't';
            }
            case MISPLACED_BREAK: return "Break statement found outside of a loop!";
            case MISPLACED_CONTINUE: return "Continue statement found outside of a loop!";
            case MISPLACED_RETURN: return "Return statement found outside of a method!";
            case INV_COMPILER_OBJ: {
                String objectName = get_next_context_object(String.class, context);
                Object object = get_next_context_object(Object.class, context);
                return objectName == null || object == null ? invalidMessage : "Invalid " + objectName + ": " + object + '!';
            }
            case ITERATOR_IN_USE: {
                String varName = get_next_context_object(String.class, context);
                return varName == null ? invalidMessage : "Variable '" + varName + "' is already being used as a foreach iterator!";
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