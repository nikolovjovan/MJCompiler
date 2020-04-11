package rs.ac.bg.etf.pp1.loggers;

import rs.ac.bg.etf.pp1.symboltable.concepts.MJSymbol;

// TODO: Move more messages from SemanticAnalyzer here...

public class MJSemanticAnalyzerLogger extends MJLogger {

    private static final String invalidMessage = "Invalid semantic analyzer message!";

    public enum MessageType {
        /* DEBUG MESSAGES */
        NODE_VISIT,             // Params: String nodeName
        CUR_ACC_MOD,            // Params: String accessName
        /* INFO MESSAGES */
        DEF_SYM,                // Params: String kindName, MJSymbol symbol
        /* ERROR MESSAGES */
        INV_SYM_CHECK_REACHED,  // Params: String s1, String s1
        SYM_NOT_DEF,            // Params: String s1, String s2
        SYM_DEF_INV_KIND,       // Params: String s1, String s2, String s3
        SYM_IN_USE,             // Params: String symName
        INV_SYM,                // Params: String symName
        TYPE_NOT_BASIC,         // Params: String kindName
        INCOMPATIBLE_TYPES,     // Params: String type1Name, String type2Name
        MISPLACED_BREAK,        // Params:
        MISPLACED_CONTINUE,     // Params:
        MISPLACED_RETURN,       // Params:
        INV_COMPILER_OBJ,       // Params: String objectName, Object object
        ITERATOR_IN_USE,        // Params: String varName
        INV_ACT_PARAM,          // Params: Integer index
        UNDEF_OP,               // Params: String op, String type1Name, String type2Name
        UNIMPL_METHOD,          // Params: String className, String methodName
        INACCESSIBLE_SYM,       // Params: String symName
        /* ANY OTHER MESSAGE */
        OTHER                   // Params: String message
    }

    public MJSemanticAnalyzerLogger() {
        super("SEMANTIC ANALYZER: ");
    }

    @Override
    protected String generateMessage(Object... context) {
        MessageType type = getNextContextObject(MessageType.class, context);
        if (type == null) return invalidMessage;
        switch (type) {
            /* DEBUG MESSAGES */
            case NODE_VISIT: {
                String nodeName = getNextContextObject(String.class, context);
                return nodeName == null ? invalidMessage : "Visited node: '" + nodeName + "'.";
            }
            case CUR_ACC_MOD: {
                String accName = getNextContextObject(String.class, context);
                return accName == null ? invalidMessage : "Current access modifier: " + accName + '.';
            }
            /* INFO MESSAGES */
            case DEF_SYM: {
                String kindName = getNextContextObject(String.class, context);
                MJSymbol symbol = getNextContextObject(MJSymbol.class, context);
                return kindName == null || symbol == null ? invalidMessage : "Defined " + kindName + " '" + symbol.getName() + "'. Symbol node string: " + symbol;
            }
            /* ERROR MESSAGES */
            case INV_SYM_CHECK_REACHED: {
                String s1 = getNextContextObject(String.class, context);
                String s2 = getNextContextObject(String.class, context);
                return s1 == null || s2 == null ? invalidMessage : s1 + " not initialized but " + s2 + " reached!";
            }
            case SYM_NOT_DEF: {
                String s1 = getNextContextObject(String.class, context);
                String s2 = getNextContextObject(String.class, context);
                s1 = s1 == null ? "" : s1 + ' ';
                s2 = s2 == null ? "" : '\'' + s2 + "' ";
                return s1.isEmpty() && s2.isEmpty() ? invalidMessage : s1 + s2 + "is not defined!";
            }
            case SYM_DEF_INV_KIND: {
                String s1 = getNextContextObject(String.class, context);
                String s2 = getNextContextObject(String.class, context);
                s1 = s1 == null ? "" : s1 + ' ';
                s2 = s2 == null ? "" : '\'' + s2 + "' ";
                String s3 = getNextContextObject(String.class, context);
                return s1.isEmpty() && s2.isEmpty() || s3 == null ? invalidMessage : s1 + s2 + "is not " + s3 + '!';
            }
            case SYM_IN_USE: {
                String symName = getNextContextObject(String.class, context);
                return symName == null ? invalidMessage : "'" + symName + "' is already in use!";
            }
            case INV_SYM: {
                String symName = getNextContextObject(String.class, context);
                return symName == null ? invalidMessage : symName + " is invalid!";
            }
            case TYPE_NOT_BASIC: {
                String kindName = getNextContextObject(String.class, context);
                return kindName == null ? invalidMessage : kindName + " must be of basic type: int, char or bool!";
            }
            case INCOMPATIBLE_TYPES: {
                String type1Name = getNextContextObject(String.class, context);
                String type2Name = getNextContextObject(String.class, context);
                return type1Name == null || type2Name == null ? invalidMessage : type1Name + " incompatible with " + type2Name + 't';
            }
            case MISPLACED_BREAK: return "Break statement found outside of a loop!";
            case MISPLACED_CONTINUE: return "Continue statement found outside of a loop!";
            case MISPLACED_RETURN: return "Return statement found outside of a method!";
            case INV_COMPILER_OBJ: {
                String objectName = getNextContextObject(String.class, context);
                Object object = getNextContextObject(Object.class, context);
                return objectName == null || object == null ? invalidMessage : "Invalid " + objectName + ": " + object + '!';
            }
            case ITERATOR_IN_USE: {
                String varName = getNextContextObject(String.class, context);
                return varName == null ? invalidMessage : "Variable '" + varName + "' is already being used as a foreach iterator!";
            }
            case INV_ACT_PARAM: {
                Integer index = getNextContextObject(Integer.class, context);
                return index == null ? invalidMessage : "Actual parameter at position " + index + " is of wrong type!";
            }
            case UNDEF_OP: {
                String op = getNextContextObject(String.class, context);
                String type1Name = getNextContextObject(String.class, context);
                String type2Name = getNextContextObject(String.class, context);
                return op == null || type1Name == null || type2Name == null ? invalidMessage : "Operator '" + op + "' is undefined for arguments of types '" + type1Name + "' and '" + type2Name + "'!";
            }
            case UNIMPL_METHOD: {
                String className = getNextContextObject(String.class, context);
                String methodName = getNextContextObject(String.class, context);
                return className == null || methodName == null ? invalidMessage : "Non-abstract class '" + className + "' must implement abstract method '" + methodName + "'!";
            }
            case INACCESSIBLE_SYM: {
                String symName = getNextContextObject(String.class, context);
                return symName == null ? invalidMessage : "Symbol '" + symName + "' is not accessible in current scope!";
            }
            /* ANY OTHER MESSAGE */
            case OTHER: {
                String message = getNextContextObject(String.class, context);
                return message == null ? invalidMessage : message;
            }
            default: return "Unhandled semantic analyzer message type: '" + type.name() + "'.";
        }
    }
}