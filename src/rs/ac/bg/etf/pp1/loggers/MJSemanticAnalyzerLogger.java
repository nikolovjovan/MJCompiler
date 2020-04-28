package rs.ac.bg.etf.pp1.loggers;

import rs.ac.bg.etf.pp1.symboltable.concepts.MJSymbol;
import rs.ac.bg.etf.pp1.util.MJUtils;

public class MJSemanticAnalyzerLogger extends MJLogger {

    public enum MessageType {
        /* DEBUG MESSAGES */
        NODE_VISIT,             // Params: String nodeName
        CUR_ACC_MOD,            // Params: String accessName
        /* INFO MESSAGES */
        DEF_SYM,                // Params: String kindName, MJSymbol symbol
        SYM_USAGE,              // Params: String kindName, MJSymbol symbol
        /* WARN MESSAGES */
        RES_SYM_HIDDEN,         // Params: String s1, String s2
        /* ERROR MESSAGES */
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
        VAR_READ_ONLY,          // Params: String varName
        INV_ACT_PARAM,          // Params: Integer index
        UNDEF_OP,               // Params: String op, String type1Name, String type2Name
        UNIMPL_METHOD,          // Params: String className, String methodName
        INACCESSIBLE_SYM,       // Params: String symName
        MISPLACED_ABS_METH,     // Params:
        PRIVATE_ABS_METH,       // Params:
        INV_ACC_MOD_OVRD,       // Params: String oldAccModName, String newAccModName
        INV_METH_OVRD_FP_CNT,   // Params: String methodName, Integer fpCount
        MEMBER_NOT_FOUND,       // Params: String designatorName, String memberName
        INV_METH_ARG_CNT,       // Params:
        /* FATAL MESSAGES */
        MAX_COUNT_EXCEEDED,     // Params: String kindName, Integer count, Integer maxCount
        /* ANY OTHER MESSAGE */
        OTHER                   // Params: String message
    }

    public MJSemanticAnalyzerLogger() {
        super("SEMANTIC ANALYZER: ");
    }

    private String generateMessageHeader(Object... context) {
        String s1 = getNextContextObject(String.class, context);
        String s2 = getNextContextObject(String.class, context);
        s1 = s1 == null ? "" : s1 + ' ';
        s2 = s2 == null ? "" : '\'' + s2 + "' ";
        return s1.isEmpty() && s2.isEmpty() ? "" : s1 + s2;
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
            case CUR_ACC_MOD: {
                String accName = getNextContextObject(String.class, context);
                if (accName == null) break;
                return "Current access modifier: " + accName + '.';
            }
            /* INFO MESSAGES */
            case DEF_SYM: {
                String kindName = getNextContextObject(String.class, context);
                MJSymbol symbol = getNextContextObject(MJSymbol.class, context);
                if (kindName == null || symbol == null) break;
                return "Defined " + kindName + " '" + symbol.getName() + "'. Symbol node: " + symbol;
            }
            case SYM_USAGE: {
                String kindName = getNextContextObject(String.class, context);
                MJSymbol symbol = getNextContextObject(MJSymbol.class, context);
                if (kindName == null || symbol == null) break;
                return "Found " + kindName + " '" + symbol.getName() + "'!" +
                        (MJUtils.isSymbolValid(symbol) ? " Symbol node: " + symbol : "");
            }
            /* WARN MESSAGES */
            case RES_SYM_HIDDEN: {
                String header = generateMessageHeader(context);
                if (header.isEmpty()) break;
                return header + " hides a reserved symbol!";
            }
            /* ERROR MESSAGES */
            case SYM_NOT_DEF: {
                String header = generateMessageHeader(context);
                if (header.isEmpty()) break;
                return header + "is not defined!";
            }
            case SYM_DEF_INV_KIND: {
                String header = generateMessageHeader(context);
                String s3 = getNextContextObject(String.class, context);
                if (header.isEmpty() || s3 == null) break;
                return header + "is not " + s3 + '!';
            }
            case SYM_IN_USE: {
                String symName = getNextContextObject(String.class, context);
                if (symName == null) break;
                return "'" + symName + "' is already in use!";
            }
            case INV_SYM: {
                String symName = getNextContextObject(String.class, context);
                if (symName == null) break;
                return symName + " is invalid!";
            }
            case TYPE_NOT_BASIC: {
                String kindName = getNextContextObject(String.class, context);
                if (kindName == null) break;
                return kindName + " must be of basic type: int, char or bool!";
            }
            case INCOMPATIBLE_TYPES: {
                String type1Name = getNextContextObject(String.class, context);
                String type2Name = getNextContextObject(String.class, context);
                if (type1Name == null || type2Name == null) break;
                return type1Name + " incompatible with " + type2Name + '!';
            }
            case MISPLACED_BREAK: return "Break statement found outside of a loop!";
            case MISPLACED_CONTINUE: return "Continue statement found outside of a loop!";
            case MISPLACED_RETURN: return "Return statement found outside of a method!";
            case INV_COMPILER_OBJ: {
                String objectName = getNextContextObject(String.class, context);
                Object object = getNextContextObject(Object.class, context);
                if (objectName == null || object == null) break;
                return "Invalid " + objectName + ": " + object + '!';
            }
            case ITERATOR_IN_USE: {
                String varName = getNextContextObject(String.class, context);
                if (varName == null) break;
                return "Variable '" + varName + "' is already being used as a foreach iterator!";
            }
            case VAR_READ_ONLY: {
                String varName = getNextContextObject(String.class, context);
                if (varName == null) break;
                return "Variable '" + varName + "' is read-only inside the foreach loop!";
            }
            case INV_ACT_PARAM: {
                Integer index = getNextContextObject(Integer.class, context);
                if (index == null) break;
                return "Actual parameter at position " + index + " is of wrong type!";
            }
            case UNDEF_OP: {
                String op = getNextContextObject(String.class, context);
                String type1Name = getNextContextObject(String.class, context);
                String type2Name = getNextContextObject(String.class, context);
                if (op == null || type1Name == null || type2Name == null) break;
                return "Operator '" + op + "' is undefined for arguments of types '" +
                        type1Name + "' and '" + type2Name + "'!";
            }
            case UNIMPL_METHOD: {
                String className = getNextContextObject(String.class, context);
                String methodName = getNextContextObject(String.class, context);
                if (className == null || methodName == null) break;
                return "Non-abstract class '" + className + "' must implement abstract method '" + methodName + "'!";
            }
            case INACCESSIBLE_SYM: {
                String symName = getNextContextObject(String.class, context);
                if (symName == null) break;
                return "Symbol '" + symName + "' is not accessible in current scope!";
            }
            case MISPLACED_ABS_METH: return "Abstract method can only be defined inside an abstract class!";
            case PRIVATE_ABS_METH: return "Abstract method cannot be declared private!";
            case INV_ACC_MOD_OVRD: {
                String oldAccModName = getNextContextObject(String.class, context);
                String newAccModName = getNextContextObject(String.class, context);
                if (oldAccModName == null || newAccModName == null) break;
                return "Cannot override '" + oldAccModName + "' method access modifier with '" +
                        newAccModName + "' access modifier!";
            }
            case INV_METH_OVRD_FP_CNT: {
                String methodName = getNextContextObject(String.class, context);
                Integer fpCount = getNextContextObject(Integer.class, context);
                if (methodName == null || fpCount == null) break;
                return "Overridden method '" + methodName + "' must have exactly " + fpCount + " formal parameter(s)!";
            }
            case MEMBER_NOT_FOUND: {
                String designatorName = getNextContextObject(String.class, context);
                String memberName = getNextContextObject(String.class, context);
                if (designatorName == null || memberName == null) break;
                return "Designator '" + designatorName + "' has no member named '" + memberName + "'!";
            }
            case INV_METH_ARG_CNT: return "Wrong number of arguments!";
            /* FATAL MESSAGES */
            case MAX_COUNT_EXCEEDED: {
                String kindName = getNextContextObject(String.class, context);
                Integer count = getNextContextObject(Integer.class, context);
                Integer maxCount = getNextContextObject(Integer.class, context);
                if (kindName == null || count == null || maxCount == null) break;
                return "Number of " + kindName + ": " + count + " exceeds the maximum allowed number of " +
                        kindName + ": " + maxCount + '!';
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