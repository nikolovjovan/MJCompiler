package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.helpers.ActualParametersStack;
import rs.ac.bg.etf.pp1.helpers.MJConstants;
import rs.ac.bg.etf.pp1.loggers.MJSemanticAnalyzerLogger;
import rs.ac.bg.etf.pp1.loggers.MJSemanticAnalyzerLogger.MessageType;
import rs.ac.bg.etf.pp1.symboltable.*;
import rs.ac.bg.etf.pp1.symboltable.concepts.*;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJScope.ScopeID;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJSymbol.Access;
import rs.ac.bg.etf.pp1.util.MJUtils;

import java.util.List;

public class SemanticAnalyzer extends VisitorAdaptor {

    private MJSemanticAnalyzerLogger logger = new MJSemanticAnalyzerLogger();

    private int errorCount = 0;
    private int constCount = 0;
    private int varCount = 0;
    private int classCount = 0;
    private int methodCount = 0;

    private int currentLocalVarCount = 0;
    private int currentFormalParamCount = 0;
    private int currentClassFieldCount = 0;

    private MJSymbol currentTypeSym = MJTable.noSym;

    private MJSymbol currentClassSym = MJTable.noSym;
    private Access currentAccess = Access.PUBLIC;

    private MJSymbol currentMethodSym = MJTable.noSym;

    private String currentDesignatorName = "";

    private int ifDepth = 0;
    private int forDepth = 0;

    private ActualParametersStack actualParametersStack = new ActualParametersStack();

    /******************** Error / debug methods ***********************************************************************/

    private void logDebug(SyntaxNode node, Object... context) {
        logger.debug(MJUtils.getLineNumber(node), -1, context);
    }

    private void logDebugNodeVisit(SyntaxNode node) {
        logger.debug(MJUtils.getLineNumber(node), -1, MessageType.NODE_VISIT, node.getClass().getSimpleName());
    }

    private void logInfo(SyntaxNode node, Object... context) {
        logger.info(MJUtils.getLineNumber(node), -1, context);
    }

    private void logInfoSymUsage(SyntaxNode node, String kindName, MJSymbol symbol) {
        int line = MJUtils.getLineNumber(node);
        logger.info(line, -1, MessageType.SYM_USAGE, kindName, symbol);
        System.out.println("Found " + kindName + " '" + symbol.getName() + "' at line " + line + '!' +
                (MJUtils.isSymbolValid(symbol) ? " Symbol node: " + symbol : ""));
    }

    private void logWarn(SyntaxNode node, Object... context) {
        logger.warn(MJUtils.getLineNumber(node), -1, context);
    }

    private void logError(SyntaxNode node, Object... context) {
        errorCount++;
        logger.error(MJUtils.getLineNumber(node), -1, context);
    }

    private void logErrorValueNotAssignableToSymbol(SyntaxNode node, String kindName, String symName) {
        logError(node, MessageType.SYM_DEF_INV_KIND, kindName, symName,
                "a variable, an array element or a class field");
    }

    private void logFatal(SyntaxNode node, Object... context) {
        errorCount++;
        logger.fatal(MJUtils.getLineNumber(node), -1, context);
    }

    private boolean assertInvSym(SyntaxNode node, MJSymbol sym, String symName) {
        if (!MJUtils.isSymbolValid(sym)) {
            logError(node, MessageType.INV_SYM, symName);
            return true;
        }
        return false;
    }

    private boolean assertSymInAnyScope(SyntaxNode node, String symName) {
        if (MJUtils.isSymbolValid(MJTable.findSymbolInAnyScope(symName))) {
            logError(node, MessageType.SYM_IN_USE, symName);
            return true;
        }
        return false;
    }

    private boolean assertSymInCurrentScope(SyntaxNode node, String symName) {
        if (MJUtils.isSymbolValid(MJTable.findSymbolInCurrentScope(symName))) {
            logError(node, MessageType.SYM_IN_USE, symName);
            return true;
        }
        return false;
    }

    private boolean assertTypeNotBasic(SyntaxNode node, MJType type, String kindName) {
        if (!MJUtils.isTypeBasic(type)) {
            logError(node, MessageType.TYPE_NOT_BASIC, kindName);
            return true;
        }
        return false;
    }

    private boolean assertValueNotAssignableToSymbol(SyntaxNode node, MJSymbol sym) {
        if (!MJUtils.isValueAssignableToSymbol(sym)) {
            logErrorValueNotAssignableToSymbol(node, null, sym.getName());
            return true;
        }
        return false;
    }

    private boolean assertDesignatorParentInvalid(SyntaxNode node, MJSymbol parentSym) {
        if (!MJUtils.isSymbolValid(parentSym)) return true;
        if (!MJUtils.isValueAssignableToSymbol(parentSym)) {
            logErrorValueNotAssignableToSymbol(node, "Designator", currentDesignatorName);
            return true;
        }
        return false;
    }

    /******************** Helper methods ******************************************************************************/

    private void processVariable(SyntaxNode node, String name, boolean array) {
        logDebugNodeVisit(node);
        // If type not found exit
        if (!MJUtils.isSymbolValid(currentTypeSym)) return;
        // Check if name is already defined in current scope
        if (assertSymInCurrentScope(node, name)) return;
        // Check if name is reserved
        if (MJUtils.isReservedName(name)) {
            logWarn(node, MessageType.RES_SYM_HIDDEN, "Variable", name);
        }
        // Insert symbol into symbol table
        MJSymbol sym = MJTable.insert(MJTable.getCurrentScope().getId() == ScopeID.CLASS ? MJSymbol.Fld : MJSymbol.Var,
                name, array ? new MJType(MJType.Array, currentTypeSym.getType()) : currentTypeSym.getType());
        sym.setLevel(MJTable.getCurrentLevel());
        if (MJTable.getCurrentScope().getId() == ScopeID.CLASS) {
            // Set class field properties
            sym.setAccess(currentAccess);
            sym.setParent(currentClassSym);
            // Increment class field count
            currentClassFieldCount++;
        } else if (MJTable.getCurrentScope().getId() == ScopeID.PROGRAM) {
            // Increment global variable count
            varCount++;
        } else {
            // Increment local variable count
            currentLocalVarCount++;
        }
        // Log symbol definition
        logInfo(node, MessageType.DEF_SYM, MJType.getTypeName(sym.getType()) + " variable", sym);
    }

    private void processAccessModifier(SyntaxNode node) {
        logDebugNodeVisit(node);
        // Set current access modifier
        if (node instanceof PublicAccessModifier) {
            currentAccess = Access.PUBLIC;
        } else if (node instanceof ProtectedAccessModifier) {
            currentAccess = Access.PROTECTED;
        } else {
            currentAccess = Access.PRIVATE;
        }
        // Log current access modifier
        logDebug(node, MessageType.CUR_ACC_MOD, currentAccess.toString());
    }

    private void processClassHeader(SyntaxNode node, boolean abs, String name, OptClassBaseType optBaseType) {
        logDebugNodeVisit(node);
        // Check base type
        MJType baseType = MJTable.noType;
        if (optBaseType instanceof ClassBaseType) {
            MJSymbol typeSym = ((ClassBaseType) optBaseType).getType().mjsymbol;
            if (typeSym.getType().getKind() != MJType.Class) {
                logError(node, MessageType.SYM_DEF_INV_KIND, null, typeSym.getName(), "a class");
                // Continue with no base type (already set above)
            } else {
                baseType = typeSym.getType();
            }
        }
        // If symbol with same name is already defined, make a new obj outside symbol table to continue analysis
        if (assertSymInAnyScope(node, name)) {
            currentClassSym = new MJSymbol(MJSymbol.Type, name, new MJType(MJType.Class, baseType));
        } else {
            currentClassSym = MJTable.insert(MJSymbol.Type, name, new MJType(MJType.Class, baseType));
        }
        currentClassSym.setAbstract(abs);
        // Increment class count
        classCount++;
        MJTable.openScope(ScopeID.CLASS);
        // Reset class field count
        currentClassFieldCount = 0;
        if (MJUtils.isTypeValid(baseType)) {
            // Insert a copy of base type members into current scope
            for (MJSymbol sym : baseType.getMembersList()) {
                if (sym.getKind() == MJSymbol.Fld) {
                    // Increment class field count
                    currentClassFieldCount++;
                }
                MJTable.getCurrentScope().addToLocals(new MJSymbol(sym));
            }
        } else {
            // Insert VMT_POINTER as first field
            MJTable.insert(MJSymbol.Fld, MJConstants.VMT_POINTER, MJTable.intType);
            // Increment class field count
            currentClassFieldCount++;
        }
        // Set reference in syntax node
        if (node instanceof ClassHeader) {
            ((ClassHeader) node).mjsymbol = currentClassSym;
        } else {
            ((AbstractClassHeader) node).mjsymbol = currentClassSym;
        }
    }

    private void processClassDeclaration(SyntaxNode node) {
        logDebugNodeVisit(node);
        // Set members and close scope
        MJTable.chainLocalSymbols(currentClassSym.getType());
        MJTable.closeScope();
        // Non-abstract class must implement all abstract methods
        if (!currentClassSym.isAbstract()) {
            for (MJSymbol sym : currentClassSym.getType().getMembersList()) {
                if (sym.getKind() == MJSymbol.Meth && sym.isAbstract()) {
                    logError(node, MessageType.UNIMPL_METHOD, currentClassSym.getName(), sym.getName());
                }
            }
        }
        // Check class field count
        if (currentClassFieldCount > MJConstants.MAX_CLASS_FIELD_COUNT) {
            logFatal(node, MessageType.MAX_COUNT_EXCEEDED, "class fields",
                    currentClassFieldCount, MJConstants.MAX_CLASS_FIELD_COUNT);
        }
        // Log symbol definition
        logInfo(node, MessageType.DEF_SYM, "class", currentClassSym);
        currentClassSym = MJTable.noSym;
    }

    private void processMethodHeader(SyntaxNode node, boolean abs, String name, RetType retType) {
        logDebugNodeVisit(node);
        // Return type check
        MJType returnType = MJTable.voidType;
        if (MJUtils.isSymbolValid(retType.mjsymbol)) {
            returnType = retType.mjsymbol.getType();
        } // Else continue with void type, error already reported
        // Insert or modify existing symbol (class method overriding)
        MJSymbol methodSym = MJTable.findSymbolInAnyScope(name);
        if (MJUtils.isSymbolValid(methodSym)) {
            // Inherited class method overriding (cannot override method defined in this class)
            if (MJTable.getCurrentScope().getId() == ScopeID.CLASS && methodSym.getKind() == MJSymbol.Meth &&
                    methodSym.getParent() != currentClassSym) {
                // Check if return type is assignable to base method return type
                if (!returnType.assignableTo(methodSym.getType())) {
                    logError(node, MessageType.INCOMPATIBLE_TYPES,
                            MJType.getTypeName(returnType), MJType.getTypeName(methodSym.getType()));
                    // To continue analysis instantiate a new symbol outside symbol table
                    currentMethodSym = new MJSymbol(MJSymbol.Meth, name, returnType);
                } else {
                    // Return type is good, use the same method symbol
                    currentMethodSym = methodSym;
                }
            } else {
                // Symbol with same name is already defined, log error message
                logError(node, MessageType.SYM_IN_USE, name);
                // To continue analysis instantiate a new symbol outside symbol table
                currentMethodSym = new MJSymbol(MJSymbol.Meth, name, returnType);
            }
        } else {
            // Symbol does not already exist insert it into symbol table
            currentMethodSym = MJTable.insert(MJSymbol.Meth, name, returnType);
        }
        // Abstract method checks
        if (abs) {
            if (!MJUtils.isSymbolValid(currentClassSym) || !currentClassSym.isAbstract()) {
                logError(node, MessageType.MISPLACED_ABS_METH);
            } else if (currentAccess == Access.PRIVATE) {
                logError(node, MessageType.PRIVATE_ABS_METH);
            }
        }
        currentMethodSym.setAbstract(abs);
        // Open a new method scope
        MJTable.openScope(MJTable.getCurrentScope().getId() == ScopeID.PROGRAM ?
                ScopeID.GLOBAL_METHOD :
                ScopeID.CLASS_METHOD);
        // Reset local variable count and formal parameter count
        currentLocalVarCount = 0;
        currentFormalParamCount = 0;
        if (MJTable.getCurrentScope().getId() == ScopeID.CLASS_METHOD) {
            // Insert 'this' as implicit first formal parameter
            // Regardless of whether this method is being overridden, symbol this must be current class instance
            MJTable.insert(MJSymbol.Var, MJConstants.THIS, currentClassSym.getType());
            // Increment local variable count and formal parameter count
            currentLocalVarCount++;
            currentFormalParamCount++;
            if (currentMethodSym == methodSym) { // Method override
                // Change access modifier
                if (currentAccess.ordinal() < methodSym.getAccess().ordinal()) {
                    logError(node, MessageType.INV_ACC_MOD_OVRD,
                            methodSym.getAccess().toString().toLowerCase(), currentAccess.toString().toLowerCase());
                } else {
                    currentMethodSym.setAccess(currentAccess);
                }
            } else {
                // Set access modifier and parent for class method inheritance check
                currentMethodSym.setAccess(currentAccess);
                currentMethodSym.setParent(currentClassSym);
            }
        } else {
            // Increment global method count
            methodCount++;
        }
        // Set reference in syntax node
        if (node instanceof MethodHeader) {
            ((MethodHeader) node).mjsymbol = currentMethodSym;
        } else {
            ((AbstractMethodHeader) node).mjsymbol = currentMethodSym;
        }
    }

    private void processMethodDeclaration(SyntaxNode node) {
        logDebugNodeVisit(node);
        // Set locals and close scope
        MJTable.chainLocalSymbols(currentMethodSym);
        MJTable.closeScope();
        // Set formal parameter count
        currentMethodSym.setLevel(currentFormalParamCount);
        // Set parent class here to allow method overriding
        currentMethodSym.setParent(currentClassSym);
        // Check local variable count
        if (currentLocalVarCount > MJConstants.MAX_LOCAL_VARIABLE_COUNT) {
            logFatal(node, MessageType.MAX_COUNT_EXCEEDED, "local variables",
                    currentLocalVarCount, MJConstants.MAX_LOCAL_VARIABLE_COUNT);
        }
        // Log symbol definition
        logInfo(node, MessageType.DEF_SYM, "method", currentMethodSym);
        currentMethodSym = null;
    }

    private void processIncOrDec(SyntaxNode node, MJSymbol designatorSym) {
        logDebugNodeVisit(node);
        // If designator symbol is invalid just exit, error already reported
        if (!MJUtils.isSymbolValid(designatorSym)) return;
        // Check if value can be assigned to designator
        if (assertValueNotAssignableToSymbol(node, designatorSym)) return;
        // Type checks
        if (designatorSym.getType() != MJTable.intType) {
            logError(node, MessageType.INCOMPATIBLE_TYPES,
                    MJType.getTypeName(designatorSym.getType()), MJType.getTypeName(MJTable.intType));
        }
    }

    /******************** Public methods / constructors ***************************************************************/

    public SemanticAnalyzer() {
        // Initialize symbol table before starting semantic analysis
        MJTable.init();
    }

    public int getErrorCount() { return errorCount; }
    public int getConstCount() { return constCount; }
    public int getVarCount() { return varCount; }
    public int getClassCount() { return classCount; }
    public int getMethodCount() { return methodCount; }

    /******************** Program *************************************************************************************/

    @Override
    public void visit(ProgramHeader programHeader) {
        logDebugNodeVisit(programHeader);
        String name = programHeader.getName();
        if (MJUtils.isSymbolValid(MJTable.findSymbolInCurrentScope(name))) {
            logError(programHeader, MessageType.SYM_DEF_INV_KIND, null, name, "a valid program name");
            // Create a new object outside symbol table to continue analysis
            programHeader.mjsymbol = new MJSymbol(MJSymbol.Prog, name, MJTable.noType);
        } else {
            // Insert object into symbol table
            programHeader.mjsymbol = MJTable.insert(MJSymbol.Prog, name, MJTable.noType);
        }
        // Open program scope
        MJTable.openScope(ScopeID.PROGRAM);
    }

    @Override
    public void visit(Program program) {
        logDebugNodeVisit(program);
        // Add local symbols and close scope
        MJSymbol programSym = program.getProgramHeader().mjsymbol;
        MJTable.chainLocalSymbols(programSym);
        MJTable.closeScope();
        // Check main method is defined and valid type
        MJSymbol mainSym = MJUtils.findLocalSymbol(programSym, MJConstants.MAIN);
        if (!MJUtils.isSymbolValid(mainSym)) {
            logError(program, MessageType.SYM_NOT_DEF, "Main method");
        } else if (mainSym.getKind() != MJSymbol.Meth) {
            logError(program, MessageType.SYM_DEF_INV_KIND, null, MJConstants.MAIN, "a method");
        } else if (mainSym.getType() != MJTable.voidType || mainSym.getLevel() != 0) {
            logError(program, MessageType.SYM_DEF_INV_KIND,
                    "Main method defined but", "a void method with zero parameters");
        }
        // Check global variable count
        if (varCount > MJConstants.MAX_GLOBAL_VARIABLE_COUNT) {
            logFatal(program, MessageType.MAX_COUNT_EXCEEDED, "global variables",
                    varCount, MJConstants.MAX_GLOBAL_VARIABLE_COUNT);
        }
        // Log symbol definition
        logInfo(program, MessageType.DEF_SYM, "program", programSym);
    }

    /******************** Common **************************************************************************************/

    @Override
    public void visit(Type type) {
        logDebugNodeVisit(type);
        String name = type.getName();
        // Search for defined type
        type.mjsymbol = MJTable.findSymbolInAnyScope(name);
        if (!MJUtils.isSymbolValid(type.mjsymbol)) {
            logError(type, MessageType.SYM_NOT_DEF, name, MJSymbol.Type);
        } else if (type.mjsymbol.getKind() != MJSymbol.Type) {
            logError(type, MessageType.SYM_DEF_INV_KIND, null, name, "a type");
            type.mjsymbol = MJTable.noSym;
        }
        currentTypeSym = type.mjsymbol;
        if (currentTypeSym.getType().getKind() == MJType.Class && currentTypeSym.isAbstract()) {
            logInfoSymUsage(type, "abstract class usage", currentTypeSym);
        }
    }

    /******************** Const ***************************************************************************************/

    @Override
    public void visit(ConstAssignment constAssignment) {
        logDebugNodeVisit(constAssignment);
        String name = constAssignment.getName();
        Const value = constAssignment.getConst();
        // Check if name is in use
        if (assertSymInAnyScope(constAssignment, name)) return;
        // Check if name is reserved
        if (MJUtils.isReservedName(name)) {
            logWarn(constAssignment, MessageType.RES_SYM_HIDDEN, "Constant", name);
        }
        // Type checking
        if (assertInvSym(constAssignment, currentTypeSym, "Constant type")) return;
        if (assertTypeNotBasic(constAssignment, currentTypeSym.getType(), "Constant")) return;
        // Get assigned value
        MJType assignedType;
        int assignedValue;
        if (value instanceof IntConst) {
            assignedType = MJTable.intType;
            assignedValue = ((IntConst) value).getValue();
            if (((IntConst) value).getOptSign() instanceof MinusSign) assignedValue *= -1;
        } else if (value instanceof CharConst) {
            assignedType = MJTable.charType;
            assignedValue = ((CharConst) value).getValue();
        } else {
            assignedType = MJTable.boolType;
            assignedValue = ((BoolConst) value).getValue() ? 1 : 0;
        }
        // Check if types match
        if (!assignedType.equals(currentTypeSym.getType())) {
            logError(constAssignment, MessageType.INCOMPATIBLE_TYPES,
                    "Constant type", "type of initial value");
            return;
        }
        // Insert obj into symbol table
        MJSymbol sym = MJTable.insert(MJSymbol.Con, name, assignedType);
        sym.setAdr(assignedValue); // Set const value
        // Increment constant count
        constCount++;
        // Log symbol definition
        logInfo(constAssignment, MessageType.DEF_SYM, MJType.getTypeName(assignedType) + " constant", sym);
    }

    /******************** Global variables ****************************************************************************/

    @Override
    public void visit(Variable variable) {
        processVariable(variable, variable.getName(), variable.getOptArrayBrackets() instanceof ArrayBrackets);
    }

    /******************** Class variables *****************************************************************************/

    @Override
    public void visit(ClassVariable classVariable) {
        processVariable(classVariable, classVariable.getName(), classVariable.getOptArrayBrackets() instanceof ArrayBrackets);
    }

    /******************** Local variables *****************************************************************************/

    @Override
    public void visit(LocalVariable localVariable) {
        processVariable(localVariable, localVariable.getName(), localVariable.getOptArrayBrackets() instanceof ArrayBrackets);
    }

    /******************** Class common ********************************************************************************/

    @Override
    public void visit(PublicAccessModifier publicAccessModifier) {
        processAccessModifier(publicAccessModifier);
    }

    @Override
    public void visit(ProtectedAccessModifier protectedAccessModifier) {
        processAccessModifier(protectedAccessModifier);
    }

    @Override
    public void visit(PrivateAccessModifier privateAccessModifier) {
        processAccessModifier(privateAccessModifier);
    }

    /******************** Class ***************************************************************************************/

    @Override
    public void visit(ClassHeader classHeader) {
        processClassHeader(classHeader, false, classHeader.getName(), classHeader.getOptClassBaseType());
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        processClassDeclaration(classDeclaration);
    }

    /******************** Abstract class ******************************************************************************/

    @Override
    public void visit(AbstractClassHeader abstractClassHeader) {
        processClassHeader(abstractClassHeader, true, abstractClassHeader.getName(), abstractClassHeader.getOptClassBaseType());
    }

    @Override
    public void visit(AbstractClassDeclaration abstractClassDeclaration) {
        processClassDeclaration(abstractClassDeclaration);
    }

    /******************** Common method *******************************************************************************/

    @Override
    public void visit(ReturnType returnType) {
        logDebugNodeVisit(returnType);
        returnType.mjsymbol = returnType.getType().mjsymbol;
    }

    @Override
    public void visit(ReturnVoid returnVoid) {
        logDebugNodeVisit(returnVoid);
        returnVoid.mjsymbol = MJTable.voidTypeSym;
    }

    @Override
    public void visit(FormalParameter formalParameter) {
        logDebugNodeVisit(formalParameter);
        String name = formalParameter.getName();
        // If type not found exit
        if (!MJUtils.isSymbolValid(currentTypeSym)) return;
        // Check if name is already defined in current scope
        if (assertSymInCurrentScope(formalParameter, name)) return;
        // Check if name is reserved
        if (MJUtils.isReservedName(name)) {
            logWarn(formalParameter, MessageType.RES_SYM_HIDDEN, "Formal parameter", name);
        }
        // Check if method is being overridden (formal parameter type must be assignable to inherited type)
        if (MJTable.getCurrentScope().getId() == ScopeID.CLASS_METHOD &&
                currentMethodSym.getParent() != currentClassSym) { // Method override
            if (currentFormalParamCount > currentMethodSym.getLevel()) {
                logError(formalParameter, MessageType.INV_METH_OVRD_FP_CNT,
                        currentMethodSym.getName(), currentMethodSym.getLevel());
                return;
            }
            MJSymbol inheritedSym = currentMethodSym.getLocalSymbolsList().get(currentFormalParamCount);
            if (!currentTypeSym.getType().assignableTo(inheritedSym.getType())) {
                logError(formalParameter, MessageType.INCOMPATIBLE_TYPES,
                        MJType.getTypeName(currentTypeSym.getType()), MJType.getTypeName(inheritedSym.getType()));
                return;
            }
        }
        // Insert obj into symbol table
        MJSymbol sym = MJTable.insert(MJSymbol.Var, name,
                formalParameter.getOptArrayBrackets() instanceof ArrayBrackets ?
                        new MJType(MJType.Array, currentTypeSym.getType()) :
                        currentTypeSym.getType());
        sym.setFpPos(currentFormalParamCount++);
        // Log symbol definition
        logInfo(formalParameter, MessageType.DEF_SYM,
                MJType.getTypeName(sym.getType()) + " formal parameter", sym);
    }

    /******************** Method **************************************************************************************/

    @Override
    public void visit(MethodHeader methodHeader) {
        processMethodHeader(methodHeader, false, methodHeader.getName(), methodHeader.getRetType());
    }

    @Override
    public void visit(MethodStatementListStart methodStatementListStart) {
        logDebugNodeVisit(methodStatementListStart);
        // Add formal parameters and local variables before statements to allow recursion...
        MJTable.chainLocalSymbols(currentMethodSym);
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        processMethodDeclaration(methodDeclaration);
    }

    /******************** Abstract method *****************************************************************************/

    @Override
    public void visit(AbstractMethodHeader abstractMethodHeader) {
        processMethodHeader(abstractMethodHeader, true, abstractMethodHeader.getName(), abstractMethodHeader.getRetType());
    }

    @Override
    public void visit(AbstractMethodDeclaration abstractMethodDeclaration) {
        processMethodDeclaration(abstractMethodDeclaration);
    }

    /******************** Designator **********************************************************************************/

    //------------------- Designator ---------------------------------------------------------------------------------//

    @Override
    public void visit(IdentifierDesignator simpleDesignator) {
        logDebugNodeVisit(simpleDesignator);
        simpleDesignator.mjsymbol = MJTable.noSym;
        currentDesignatorName = "";
        String name = simpleDesignator.getName();
        MJSymbol sym = MJTable.findSymbolInAnyScope(name);
        if (!MJUtils.isSymbolValid(sym)) {
            logError(simpleDesignator, MessageType.SYM_NOT_DEF, null, name);
        } else if ((MJTable.getCurrentScope().getId() == ScopeID.GLOBAL_METHOD &&
                !MJUtils.isSymbolAccessibleInGlobalMethod(sym)) ||
                (MJTable.getCurrentScope().getId() == ScopeID.CLASS_METHOD &&
                        !MJUtils.isSymbolAccessibleInClassMethod(currentClassSym, sym))) {
            logError(simpleDesignator, MessageType.INACCESSIBLE_SYM, name);
            return;
        }
        currentDesignatorName = name;
        simpleDesignator.mjsymbol = sym;
        switch (sym.getKind()) {
            case MJSymbol.Con: {
                logInfoSymUsage(simpleDesignator, "symbolic constant usage", sym);
                break;
            }
            case MJSymbol.Var: {
                if (sym.getLevel() == 0) {
                    logInfoSymUsage(simpleDesignator, "global variable usage", sym);
                } else if (sym.getFpPos() == -1) {
                    logInfoSymUsage(simpleDesignator, "local variable usage", sym);
                } else {
                    logInfoSymUsage(simpleDesignator, "formal parameter usage", sym);
                }
                break;
            }
            case MJSymbol.Fld: {
                logInfoSymUsage(simpleDesignator, "class '" + sym.getParent().getName() + "' field usage", sym);
                break;
            }
            case MJSymbol.Meth: {
                if (MJUtils.isSymbolValid(sym.getParent())) {
                    logInfoSymUsage(simpleDesignator, "class '" + sym.getParent().getName() + "' method call", sym);
                } else {
                    logInfoSymUsage(simpleDesignator, "global method call", sym);
                }
                break;
            }
        }
    }

    @Override
    public void visit(MemberAccessDesignator memberAccessDesignator) {
        logDebugNodeVisit(memberAccessDesignator);
        memberAccessDesignator.mjsymbol = MJTable.noSym;
        MJSymbol parentSym = memberAccessDesignator.getDesignator().mjsymbol;
        if (assertDesignatorParentInvalid(memberAccessDesignator, parentSym)) return;
        if (parentSym.getType().getKind() != MJType.Class) {
            logError(memberAccessDesignator, MessageType.SYM_DEF_INV_KIND,
                    "Designator", currentDesignatorName, "a class instance");
            return;
        }
        String name = memberAccessDesignator.getName();
        MJSymbol memberSym = (MJSymbol) parentSym.getType().getMembersTable().searchKey(name);
        if (!MJUtils.isSymbolValid(memberSym) && MJUtils.isSymbolValid(currentClassSym) &&
                parentSym.getType().equals(currentClassSym.getType())) {
            memberSym = MJTable.getCurrentScope().getOuter().findSymbol(name);
        }
        if (!MJUtils.isSymbolValid(memberSym)) {
            logError(memberAccessDesignator, MessageType.MEMBER_NOT_FOUND, currentDesignatorName, name);
            return;
        } else if ((MJTable.getCurrentScope().getId() == ScopeID.GLOBAL_METHOD &&
                !MJUtils.isSymbolAccessibleInGlobalMethod(memberSym)) ||
                (MJTable.getCurrentScope().getId() == ScopeID.CLASS_METHOD &&
                        !MJUtils.isSymbolAccessibleInClassMethod(currentClassSym, memberSym))) {
            logError(memberAccessDesignator, MessageType.INACCESSIBLE_SYM, currentDesignatorName + '.' + name);
            return;
        }
        currentDesignatorName += '.' + name;
        memberAccessDesignator.mjsymbol = memberSym;
        logInfoSymUsage(memberAccessDesignator,
                "class '" + memberSym.getParent().getName() + "' field usage", memberSym);
    }

    @Override
    public void visit(ElementAccessDesignator elementAccessDesignator) {
        logDebugNodeVisit(elementAccessDesignator);
        elementAccessDesignator.mjsymbol = MJTable.noSym;
        MJSymbol parentSym = elementAccessDesignator.getDesignator().mjsymbol;
        if (assertDesignatorParentInvalid(elementAccessDesignator, parentSym)) {
            return;
        }
        if (parentSym.getType().getKind() != MJType.Array) {
            logError(elementAccessDesignator, MessageType.SYM_DEF_INV_KIND,
                    "Designator", currentDesignatorName, "an array");
            return;
        }
        MJSymbol expressionSym = elementAccessDesignator.getArrayIndexer().getExpr().mjsymbol;
        if (expressionSym.getType() != MJTable.intType) {
            logError(elementAccessDesignator, MessageType.SYM_DEF_INV_KIND,
                    "Array index expression", expressionSym.getName(), "int type");
            return;
        }
        currentDesignatorName += '[' + elementAccessDesignator.getArrayIndexer().getExpr().mjsymbol.getName() + ']';
        elementAccessDesignator.mjsymbol = new MJSymbol(MJSymbol.Elem, currentDesignatorName,
                parentSym.getType().getElemType());
        logInfoSymUsage(elementAccessDesignator, "array element access", elementAccessDesignator.mjsymbol);
    }

    //------------------- DesignatorStatement ------------------------------------------------------------------------//

    @Override
    public void visit(AssignmentDesignatorStatement assignmentDesignatorStatement) {
        logDebugNodeVisit(assignmentDesignatorStatement);
        AssignmentHeader header = (AssignmentHeader) assignmentDesignatorStatement.getAssignHeader();
        AssignmentFooter footer = (AssignmentFooter) assignmentDesignatorStatement.getAssignFooter();
        MJSymbol designatorSym = header.getDesignator().mjsymbol;
        MJSymbol expressionSym = footer.getExpr().mjsymbol;
        // Don't report any more errors if designator or expression is invalid since it is already reported
        if (!MJUtils.isSymbolValid(designatorSym) || !MJUtils.isSymbolValid(expressionSym)) return;
        // Check if value can be assigned to designator
        if (assertValueNotAssignableToSymbol(assignmentDesignatorStatement, designatorSym)) return;
        // Check if designator is read-only (foreach loop iterator)
        if (designatorSym.isReadOnly()) {
            logError(assignmentDesignatorStatement, MessageType.VAR_READ_ONLY, designatorSym.getName());
        }
        // Type checks
        if (!expressionSym.getType().assignableTo(designatorSym.getType())) {
            logError(assignmentDesignatorStatement, MessageType.INCOMPATIBLE_TYPES,
                    MJType.getTypeName(expressionSym.getType()), MJType.getTypeName(designatorSym.getType()));
        } else {
            if (!(footer.getAssignop() instanceof AssignOperator)) {
                // Check if expression and designator are integers to allow these operators
                if (designatorSym.getType() != MJTable.intType) {
                    logError(assignmentDesignatorStatement, MessageType.SYM_DEF_INV_KIND,
                            "Designator", designatorSym.getName(), "int type");
                } else if (expressionSym.getType() != MJTable.intType) {
                    logError(assignmentDesignatorStatement, MessageType.SYM_DEF_INV_KIND,
                            "Expression", expressionSym.getName(), "int type");
                }
            }
        }
    }

    @Override
    public void visit(IncrementDesignatorStatement incrementDesignatorStatement) {
        processIncOrDec(incrementDesignatorStatement, incrementDesignatorStatement.getDesignator().mjsymbol);
    }

    @Override
    public void visit(DecrementDesignatorStatement decrementDesignatorStatement) {
        processIncOrDec(decrementDesignatorStatement, decrementDesignatorStatement.getDesignator().mjsymbol);
    }

    /******************** Statement ***********************************************************************************/

    @Override
    public void visit(BreakStatement breakStatement) {
        logDebugNodeVisit(breakStatement);
        if (forDepth == 0) {
            logError(breakStatement, MessageType.MISPLACED_BREAK);
        }
    }

    @Override
    public void visit(ContinueStatement continueStatement) {
        logDebugNodeVisit(continueStatement);
        if (forDepth == 0) {
            logError(continueStatement, MessageType.MISPLACED_CONTINUE);
        }
    }

    @Override
    public void visit(ReadStatement readStatement) {
        logDebugNodeVisit(readStatement);
        MJSymbol designatorSym = readStatement.getDesignator().mjsymbol;
        // If symbol is invalid continue, error already reported
        if (!MJUtils.isSymbolValid(designatorSym)) return;
        // Check if value is assignable to symbol
        if (!MJUtils.isValueAssignableToSymbol(designatorSym)) {
            logErrorValueNotAssignableToSymbol(readStatement, "Read statement parameter", designatorSym.getName());
        } else {
            // Check if type is basic (read statement can only load basic types)
            assertTypeNotBasic(readStatement, designatorSym.getType(), "Read statement parameter");
        }
    }

    @Override
    public void visit(PrintStatement printStatement) {
        logDebugNodeVisit(printStatement);
        // Parameter semantic check
        assertTypeNotBasic(printStatement, printStatement.getExpr().mjsymbol.getType(), "Print statement parameter");
    }

    @Override
    public void visit(ReturnStatement returnStatement) {
        logDebugNodeVisit(returnStatement);
        if (MJUtils.isSymbolValid(currentMethodSym)) {
            MJType retType = MJTable.voidType;
            if (returnStatement.getOptRetValue() instanceof ReturnValue) {
                retType = ((ReturnValue) returnStatement.getOptRetValue()).getExpr().mjsymbol.getType();
            }
            if (!retType.assignableTo(currentMethodSym.getType())) {
                logError(returnStatement, MessageType.INCOMPATIBLE_TYPES, "Return expression type", "method type");
            }
        } else {
            logError(returnStatement, MessageType.MISPLACED_RETURN);
        }
    }

    @Override
    public void visit(IfStatementHeader ifStatementHeader) {
        logDebugNodeVisit(ifStatementHeader);
        ifDepth++;
    }

    @Override
    public void visit(IfOptElseStatement ifOptElseStatement) {
        logDebugNodeVisit(ifOptElseStatement);
        ifDepth--;
        if (ifDepth < 0) {
            logError(ifOptElseStatement, MessageType.INV_COMPILER_OBJ, "if depth", ifDepth);
        }
    }

    @Override
    public void visit(ForStatementHeader forStatementHeader) {
        logDebugNodeVisit(forStatementHeader);
        forDepth++;
        logInfoSymUsage(forStatementHeader, "for loop", MJTable.noSym);
    }

    @Override
    public void visit(ForStatement forStatement) {
        logDebugNodeVisit(forStatement);
        forDepth--;
        if (forDepth < 0) {
            logError(forStatement, MessageType.INV_COMPILER_OBJ, "for depth", forDepth);
        }
    }

    @Override
    public void visit(ForEachStatementHeader forEachStatementHeader) {
        logDebugNodeVisit(forEachStatementHeader);
        forEachStatementHeader.mjsymbol = MJTable.noSym;
        String name = forEachStatementHeader.getName();
        MJSymbol designatorSym = forEachStatementHeader.getDesignator().mjsymbol;
        // Iterator checks
        MJSymbol iteratorSym = MJTable.findSymbolInAnyScope(name);
        boolean iteratorSymValid = MJUtils.isSymbolValid(iteratorSym);
        if (!iteratorSymValid) {
            logError(forEachStatementHeader, MessageType.SYM_NOT_DEF, name, MJSymbol.Var);
        } else if (iteratorSym.getKind() != MJSymbol.Var) {
            logError(forEachStatementHeader, MessageType.SYM_DEF_INV_KIND, null, name, "a local or global variable");
        } else if (iteratorSym.isReadOnly()) {
            logError(forEachStatementHeader, MessageType.ITERATOR_IN_USE, name);
        }
        // Array checks
        boolean designatorSymValid = MJUtils.isSymbolValid(designatorSym);
        if (designatorSymValid && designatorSym.getType().getKind() != MJType.Array) {
            logError(forEachStatementHeader, MessageType.SYM_DEF_INV_KIND, "Designator", designatorSym.getName(), "an array");
            designatorSymValid = false;
        }
        // Type checks
        if (iteratorSymValid && designatorSymValid && !iteratorSym.getType().equals(designatorSym.getType().getElemType())) {
            logError(forEachStatementHeader, MessageType.INCOMPATIBLE_TYPES, "Iterator type", "type of array element");
        }
        if (iteratorSymValid) {
            // Set iterator symbol to read-only inside foreach statement
            iteratorSym.setReadOnly(true);
            // Set node symbol to iterator variable symbol for use in code generation
            forEachStatementHeader.mjsymbol = iteratorSym;
        }
        forDepth++;
    }

    @Override
    public void visit(ForEachStatement forEachStatement) {
        logDebugNodeVisit(forEachStatement);
        forDepth--;
        if (forDepth < 0) logError(forEachStatement, MessageType.INV_COMPILER_OBJ, "for depth", forDepth);
        MJSymbol iteratorSym = forEachStatement.getForEachStatementHeader().mjsymbol;
        if (MJUtils.isSymbolValid(iteratorSym)) {
            // Set iterator symbol back to read-write as we exited foreach statement
            iteratorSym.setReadOnly(false);
        }
    }

    /******************** Method call *********************************************************************************/

    @Override
    public void visit(MethodCallHeader methodCallHeader) {
        logDebugNodeVisit(methodCallHeader);
        MJSymbol methodSym = methodCallHeader.mjsymbol = methodCallHeader.getDesignator().mjsymbol;
        // If symbol is invalid continue, error already reported
        if (!MJUtils.isSymbolValid(methodSym)) return;
        // Check if symbol is a method
        if (methodSym.getKind() != MJSymbol.Meth) {
            logError(methodCallHeader, MessageType.SYM_DEF_INV_KIND, null, methodSym.getName(), "a method");
        } else {
            // Initialize actual parameters list
            actualParametersStack.createParameters();
        }
    }

    @Override
    public void visit(MethodCall methodCall) {
        logDebugNodeVisit(methodCall);
        MJSymbol methodSym = methodCall.mjsymbol = methodCall.getMethodCallHeader().mjsymbol;
        // If symbol is invalid continue, error already reported
        if (!MJUtils.isSymbolValid(methodSym)) return;
        List<MJSymbol> actualParametersList = actualParametersStack.getParameters();
        // Class methods have an implicit argument which is not specified on method call.
        // To make it work an offset is added if method is a class method (parent symbol is valid)
        int implicitThis = MJUtils.isSymbolValid(methodSym.getParent()) ? 1 : 0;
        if (implicitThis + actualParametersList.size() != methodSym.getLevel()) {
            logError(methodCall, MessageType.INV_METH_ARG_CNT);
            return;
        }
        // Check argument types
        List<MJSymbol> locals = methodSym.getLocalSymbolsList();
        for (int i = 0; i < methodSym.getLevel() - implicitThis; i++) {
            if (!actualParametersList.get(i).getType().assignableTo(locals.get(i + implicitThis).getType())) {
                logError(methodCall, MJSemanticAnalyzerLogger.MessageType.INV_ACT_PARAM, i + 1);
            }
        }
    }

    @Override
    public void visit(ActualParameter actualParameter) {
        logDebugNodeVisit(actualParameter);
        MJSymbol expressionSym = actualParameter.getExpr().mjsymbol;
        // If symbol is invalid continue, error already reported
        if (!MJUtils.isSymbolValid(expressionSym)) return;
        // Insert actual parameter
        actualParametersStack.insertActualParameter(expressionSym);
    }

    /******************** Expression **********************************************************************************/

    //------------------- Expression ---------------------------------------------------------------------------------//

    @Override
    public void visit(AssignmentExpression assignmentExpression) {
        logDebugNodeVisit(assignmentExpression);
        MJSymbol leftSym = assignmentExpression.getLeftExpr().mjsymbol;
        MJSymbol rightSym = assignmentExpression.getExpr().mjsymbol;
        assignmentExpression.mjsymbol = MJTable.noSym;
        // Don't report any more errors if left or right expression is invalid since it is already reported
        if (!MJUtils.isSymbolValid(leftSym) || !MJUtils.isSymbolValid(rightSym)) return;
        if (leftSym.getType() != MJTable.intType) {
            logError(assignmentExpression, MessageType.SYM_DEF_INV_KIND,
                    "Expression", leftSym.getName(), "int type");
        } else if (rightSym.getType() != MJTable.intType) {
            logError(assignmentExpression, MessageType.SYM_DEF_INV_KIND,
                    "Expression", rightSym.getName(), "int type");
        } else if (leftSym.isReadOnly()) {
            logError(assignmentExpression, MessageType.VAR_READ_ONLY, leftSym.getName());
        } else if (!assertValueNotAssignableToSymbol(assignmentExpression, leftSym)) {
            String opName;
            Rightop op = assignmentExpression.getRightop();
            if (op instanceof RightAddOperator) {
                RightAddop addOp = ((RightAddOperator) op).getRightAddop();
                if (addOp instanceof AddAssignOperator) {
                    opName = " += ";
                } else {
                    opName = " -= ";
                }
            } else {
                RightMulop mulOp = ((RightMulOperator) op).getRightMulop();
                if (mulOp instanceof MulAssignOperator) {
                    opName = " *= ";
                } else if (mulOp instanceof DivAssignOperator) {
                    opName = " /= ";
                } else {
                    opName = " %= ";
                }
            }
            assignmentExpression.mjsymbol = new MJSymbol(MJSymbol.Con,
                    leftSym.getName() + opName + rightSym.getName(), MJTable.intType);
        }
    }

    @Override
    public void visit(LeftExpression leftExpression) {
        logDebugNodeVisit(leftExpression);
        // Propagate inner symbol
        leftExpression.mjsymbol = leftExpression.getLeftExpr().mjsymbol;
    }

    @Override
    public void visit(MultipleTermsExpression multipleTermsExpression) {
        logDebugNodeVisit(multipleTermsExpression);
        MJSymbol leftSym = multipleTermsExpression.getLeftExpr().mjsymbol;
        MJSymbol rightSym = multipleTermsExpression.getTerm().mjsymbol;
        multipleTermsExpression.mjsymbol = MJTable.noSym;
        if (assertInvSym(multipleTermsExpression, leftSym, "Left expression")) return;
        if (assertInvSym(multipleTermsExpression, rightSym, "Right term")) return;
        if (leftSym.getType() != MJTable.intType) {
            logError(multipleTermsExpression, MessageType.SYM_DEF_INV_KIND,
                    "Expression", leftSym.getName(), "int type");
        } else if (rightSym.getType() != MJTable.intType) {
            logError(multipleTermsExpression, MessageType.SYM_DEF_INV_KIND,
                    "Term", rightSym.getName(), "int type");
        } else {
            String opName;
            LeftAddop op = multipleTermsExpression.getLeftAddop();
            if (op instanceof AddOperator) {
                opName = " + ";
            } else {
                opName = " - ";
            }
            multipleTermsExpression.mjsymbol = new MJSymbol(MJSymbol.Con,
                    leftSym.getName() + opName + rightSym.getName(), MJTable.intType);
        }
    }

    @Override
    public void visit(SingleTermExpression singleTermExpression) {
        logDebugNodeVisit(singleTermExpression);
        OptSign sign = singleTermExpression.getOptSign();
        if (sign instanceof MinusSign) {
            // Cannot assign a value to negative symbol, so change symbol kind to MJSymbol.Con
            singleTermExpression.mjsymbol = new MJSymbol(singleTermExpression.getTerm().mjsymbol, MJSymbol.Con);
        } else {
            // Propagate inner symbol
            singleTermExpression.mjsymbol = singleTermExpression.getTerm().mjsymbol;
        }
    }

    //------------------- Term ---------------------------------------------------------------------------------------//

    @Override
    public void visit(MultipleFactorsTerm multipleFactorsTerm) {
        logDebugNodeVisit(multipleFactorsTerm);
        MJSymbol leftSym = multipleFactorsTerm.getTerm().mjsymbol;
        MJSymbol rightSym = multipleFactorsTerm.getFactor().mjsymbol;
        multipleFactorsTerm.mjsymbol = MJTable.noSym;
        if (assertInvSym(multipleFactorsTerm, leftSym, "Left term")) return;
        if (assertInvSym(multipleFactorsTerm, rightSym, "Right factor")) return;
        if (leftSym.getType() != MJTable.intType) {
            logError(multipleFactorsTerm, MessageType.SYM_DEF_INV_KIND,
                    "Expression", leftSym.getName(), "int type");
        } else if (rightSym.getType() != MJTable.intType) {
            logError(multipleFactorsTerm, MessageType.SYM_DEF_INV_KIND,
                    "Factor", rightSym.getName(), "int type");
        } else {
            String opName;
            LeftMulop op = multipleFactorsTerm.getLeftMulop();
            if (op instanceof MulOperator) {
                opName = " * ";
            } else if (op instanceof DivOperator) {
                opName = " / ";
            } else {
                opName = " % ";
            }
            multipleFactorsTerm.mjsymbol = new MJSymbol(MJSymbol.Con,
                    leftSym.getName() + opName + rightSym.getName(), MJTable.intType);
        }
    }

    @Override
    public void visit(SingleFactorTerm singleFactorTerm) {
        logDebugNodeVisit(singleFactorTerm);
        // Propagate inner symbol
        singleFactorTerm.mjsymbol = singleFactorTerm.getFactor().mjsymbol;
    }

    //------------------- Factor -------------------------------------------------------------------------------------//

    @Override
    public void visit(DesignatorFactor designatorFactor) {
        logDebugNodeVisit(designatorFactor);
        designatorFactor.mjsymbol = designatorFactor.getDesignator().mjsymbol;
    }

    @Override
    public void visit(MethodCallFactor methodCallFactor) {
        logDebugNodeVisit(methodCallFactor);
        // Make a copy of method return type symbol with kind = MJSymbol.Con to prevent assignment
        methodCallFactor.mjsymbol = new MJSymbol(methodCallFactor.getMethodCall().mjsymbol, MJSymbol.Con);
    }

    @Override
    public void visit(ConstantFactor constantFactor) {
        logDebugNodeVisit(constantFactor);
        String name;
        MJType type;
        int adr;
        ConstFactor cf = constantFactor.getConstFactor();
        if (cf instanceof IntConstantFactor) {
            Integer value = ((IntConstantFactor) cf).getValue();
            name = String.valueOf(value);
            type = MJTable.intType;
            adr = value;
        } else if (cf instanceof CharConstantFactor) {
            Character value = ((CharConstantFactor) cf).getValue();
            name = String.valueOf(value);
            type = MJTable.charType;
            adr = value;
        } else {
            Boolean value = ((BoolConstantFactor) cf).getValue();
            name = String.valueOf(value);
            type = MJTable.boolType;
            adr = value ? 1 : 0;
        }
        // Make a new symbol with actual value as it will be used by code generator
        constantFactor.mjsymbol = new MJSymbol(MJSymbol.Con, name, type, adr, 0);
    }

    @Override
    public void visit(AllocatorFactor allocatorFactor) {
        logDebugNodeVisit(allocatorFactor);
        allocatorFactor.mjsymbol = MJTable.noSym;
        MJSymbol typeSym = allocatorFactor.getType().mjsymbol;
        OptArrayIndexer arraySize = allocatorFactor.getOptArrayIndexer();
        if (arraySize instanceof SingleArrayIndexer) { // array allocator
            MJSymbol expressionSym = ((SingleArrayIndexer) arraySize).getArrayIndexer().getExpr().mjsymbol;
            if (expressionSym.getType() != MJTable.intType) {
                logError(allocatorFactor, MessageType.SYM_DEF_INV_KIND,
                        "Array size expression", expressionSym.getName(), "int type");
            } else {
                allocatorFactor.mjsymbol = new MJSymbol(MJSymbol.Con, "",
                        new MJType(MJType.Array, typeSym.getType()));
            }
        } else { // object allocator
            if (typeSym.getType().getKind() != MJType.Class || typeSym.isAbstract()) {
                logError(allocatorFactor, MessageType.SYM_DEF_INV_KIND,
                        "Object type", "a non-abstract class type");
            } else {
                allocatorFactor.mjsymbol = new MJSymbol(MJSymbol.Con, "", typeSym.getType());
            }
        }
    }

    @Override
    public void visit(InnerExpressionFactor innerExpressionFactor) {
        logDebugNodeVisit(innerExpressionFactor);
        innerExpressionFactor.mjsymbol = innerExpressionFactor.getExpr().mjsymbol;
    }

    /******************** Condition ***********************************************************************************/

    @Override
    public void visit(ComplexConditionFact complexConditionFact) {
        logDebugNodeVisit(complexConditionFact);
        MJType tl = complexConditionFact.getExpr().mjsymbol.getType();
        MJType tr = complexConditionFact.getExpr().mjsymbol.getType();
        // Check if types are compatible
        if (!tl.compatibleWith(tr)) {
            logError(complexConditionFact, MessageType.INCOMPATIBLE_TYPES,
                    MJType.getTypeName(tl), MJType.getTypeName(tr));
        } else if (tl.getKind() == MJType.Array || tl.getKind() == MJType.Class) {
            Relop relop = complexConditionFact.getRelop();
            // Convert relop to string
            String op;
            if (relop instanceof EqOperator) {
                op = "==";
            } else if (relop instanceof NeqOperator) {
                op = "!=";
            } else if (relop instanceof GrtOperator) {
                op = ">";
            } else if (relop instanceof GeqOperator) {
                op = ">=";
            } else if (relop instanceof LssOperator) {
                op = "<";
            } else {
                op = "<=";
            }
            // Reference types (arrays and class objects) can only use '!=' or '==' relational operators
            if (!(relop instanceof EqOperator) && !(relop instanceof NeqOperator)) {
                logError(complexConditionFact, MessageType.UNDEF_OP, op, MJType.getTypeName(tl), MJType.getTypeName(tr));
            }
        }
    }

    @Override
    public void visit(SimpleConditionFact simpleConditionFact) {
        logDebugNodeVisit(simpleConditionFact);
        MJSymbol expressionSym = simpleConditionFact.getExpr().mjsymbol;
        // Check if type is boolean
        if (!expressionSym.getType().equals(MJTable.boolType)) {
            logError(simpleConditionFact, MessageType.INCOMPATIBLE_TYPES,
                    MJType.getTypeName(simpleConditionFact.mjsymbol.getType()), MJType.getTypeName(MJTable.boolType));
        }
    }
}