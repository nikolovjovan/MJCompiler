package rs.ac.bg.etf.pp1.mj.runtime;

import rs.ac.bg.etf.pp1.CodeGenerator;
import rs.ac.bg.etf.pp1.ast.SyntaxNode;
import rs.ac.bg.etf.pp1.loggers.MJCodeGeneratorLogger.MessageType;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJSymbol;
import rs.ac.bg.etf.pp1.util.MJUtils;
import rs.etf.pp1.mj.runtime.Code;

public class MJCode extends Code {

    public static int jeq = 43;
    public static int jne = 44;
    public static int jlt = 45;
    public static int jle = 46;
    public static int jgt = 47;
    public static int jge = 48;

    private static CodeGenerator generator = null;
    private static SyntaxNode currentNode = null;

    public static void setGenerator(CodeGenerator codeGenerator) {
        generator = codeGenerator;
    }

    public static void setCurrentNode(SyntaxNode node) {
        currentNode = node;
    }

    // Hide base class method to log error with MJCodeGeneratorLogger
    public static void load(MJSymbol sym) {
        // Call base class method
        Code.load(sym);
        // Log error if needed
        if (generator != null && sym.getKind() != MJSymbol.Con && !MJUtils.isValueAssignableToSymbol(sym)) {
            generator.logError(currentNode, MessageType.OTHER, "Invalid MJCode.load() operand kind: " + sym.getKindName() + "!");
        }
    }

    // Hide base class method to log error with MJCodeGeneratorLogger
    public static void store(MJSymbol sym) {
        // Call base class method
        Code.store(sym);
        // Log error if needed
        if (generator != null && !MJUtils.isValueAssignableToSymbol(sym)) {
            generator.logError(currentNode, MessageType.OTHER, '\'' + sym.getName() + "' is not a variable, a class field or an array element!");
        }
    }

    public static void putCall(int address) {
        put(call);
        put2(address - pc + 1);
    }

    public static void putTrueJump(int condition, int address) {
        put(jcc + condition);
        put2(address - pc + 1);
    }
}