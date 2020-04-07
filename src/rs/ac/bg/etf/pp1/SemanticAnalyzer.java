package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.helpers.ActualParametersStack;
import rs.ac.bg.etf.pp1.loggers.MJSemanticAnalyzerLogger;
import rs.ac.bg.etf.pp1.loggers.MJSemanticAnalyzerLogger.MessageType;
import rs.ac.bg.etf.pp1.symboltable.*;
import rs.ac.bg.etf.pp1.symboltable.concepts.*;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJScope.ScopeID;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJSymbol.Access;
import rs.ac.bg.etf.pp1.util.MJUtils;

import java.util.List;

public class SemanticAnalyzer extends VisitorAdaptor {

    // TODO: Add symbol usage detection...

    // TODO: Check for any inconsistencies for using MJTable.noSym! Make sure every production's mjsymbol field
    //       has value of MJTable.noSym if it is invalid!

    // TODO: Add visit methods for all error productions to allow analysis to continue even though there may be
    //       syntax errors!

    // TODO: Add member count checks using MJCode constants...

    private static final String MAIN = "main";
    private static final String THIS = "this";
    private static final String VMT_POINTER = "$vmt_pointer";

    private MJSemanticAnalyzerLogger logger = new MJSemanticAnalyzerLogger();

    private int errorCount = 0;
    private int constCount = 0;
    private int varCount = 0;
    private int classCount = 0;
    private int methodCount = 0;

    private int currentFormalParamCount = 0;
    private int currentLocalVarCount = 0;
    private int currentClassFieldCount = 0;

    private MJSymbol currentTypeSym = MJTable.noSym;

    private MJSymbol currentClassSym = MJTable.noSym;
    private Access currentAccess = Access.PUBLIC;

    private MJSymbol currentMethodSym = MJTable.noSym;
    private boolean returnFound = false;

    private int ifDepth = 0;
    private int forDepth = 0;

    private ActualParametersStack actualParametersStack = new ActualParametersStack();

    /******************** Error / debug methods *******************************************************/

    private void logDebug(SyntaxNode info, Object... context) {
        logger.debug(MJUtils.getLineNumber(info), -1, context);
    }

    private void logDebugNodeVisit(SyntaxNode info) {
        logger.debug(MJUtils.getLineNumber(info), -1, MessageType.NODE_VISIT, info.getClass().getSimpleName());
    }

    private void logInfo(SyntaxNode info, Object... context) {
        logger.info(MJUtils.getLineNumber(info), -1, context);
    }

    private void logWarn(SyntaxNode info, Object... context) {
        logger.warn(MJUtils.getLineNumber(info), -1, context);
    }

    private void logError(SyntaxNode info, Object... context) {
        errorCount++;
        logger.error(MJUtils.getLineNumber(info), -1, context);
    }

    private boolean assertInvSym(SyntaxNode info, MJSymbol sym, String symName) {
        if (!MJUtils.isSymbolValid(sym)) {
            logError(info, MessageType.INV_SYM, symName);
            return true;
        }
        return false;
    }

    private boolean assertSymInUse(SyntaxNode info, String symName) {
        if (MJTable.findSymbolInAnyScope(symName) != MJTable.noSym) {
            logError(info, MessageType.SYM_IN_USE, symName);
            return true;
        }
        return false;
    }

    private boolean assertSymInCurrentScope(SyntaxNode info, String symName) {
        if (MJTable.findSymbolInCurrentScope(symName) != MJTable.noSym) {
            logError(info, MessageType.SYM_IN_USE, symName);
            return true;
        }
        return false;
    }

    private boolean assertTypeNotBasic(SyntaxNode info, MJType type, String kindName) {
        if (!MJUtils.isTypeBasic(type)) {
            logError(info, MessageType.TYPE_NOT_BASIC, kindName);
            return true;
        }
        return false;
    }

    private boolean assertValueNotAssignableToSymbol(SyntaxNode info, MJSymbol sym) {
        if (!MJUtils.isValueAssignableToSymbol(sym)) {
            logError(info, MessageType.SYM_DEF_INV_KIND, null, sym.getName(), "variable, a class field or an array element");
            return true;
        }
        return false;
    }

    /******************** Helper methods **************************************************************/

    private void processAccessModifier(SyntaxNode info) {
        logDebugNodeVisit(info);
        // Set current access modifier
        if (info instanceof PublicAccess) {
            currentAccess = Access.PUBLIC;
        } else if (info instanceof ProtectedAccess) {
            currentAccess = Access.PROTECTED;
        } else {
            currentAccess = Access.PRIVATE;
        }
        // Log current access modifier
        logDebug(info, MessageType.CUR_ACC_MOD, currentAccess.toString());
    }

    private void processVariable(SyntaxNode info, String name, boolean array) {
        logDebugNodeVisit(info);
        // Type checking
        if (assertInvSym(info, currentTypeSym, "Variable type")) return;
        // Check if name is in use
        if (assertSymInCurrentScope(info, name)) return;
        MJSymbol sym = MJTable.insert(MJTable.getCurrentScope().getId() == ScopeID.CLASS ? MJSymbol.Fld : MJSymbol.Var, name, array ?
                new MJType(MJType.Array, currentTypeSym.getType()) : currentTypeSym.getType());
        sym.setLevel(MJTable.getCurrentLevel());
        if (MJTable.getCurrentScope().getId() == ScopeID.CLASS) {
            sym.setAccess(currentAccess);
            sym.setParent(currentClassSym);
        }
        if (MJTable.getCurrentScope().getId() == ScopeID.PROGRAM) varCount++;
        // Log object definition
        logInfo(info, MessageType.DEF_SYM, MJType.getTypeName(sym.getType()) + " variable", sym);
    }

    private void processClassHeader(SyntaxNode info, boolean abs, String name, OptClassBaseType optBaseType) {
        logDebugNodeVisit(info);
        // Check base type
        MJType baseType = MJTable.noType;
        if (optBaseType instanceof ClassBaseType) {
            MJSymbol typeSym = ((ClassBaseType) optBaseType).getType().mjsymbol;
            if (typeSym.getType().getKind() != MJType.Class) {
                logError(info, MessageType.SYM_DEF_INV_KIND, null, typeSym.getName(), "class type");
            } else {
                baseType = typeSym.getType();
            }
        }
        // If symbol with same name is already defined, make a new obj outside symbol table to continue analysis
        if (assertSymInCurrentScope(info, name)) {
            currentClassSym = new MJSymbol(MJSymbol.Type, name, new MJType(MJType.Class, baseType));
        } else {
            currentClassSym = MJTable.insert(MJSymbol.Type, name, new MJType(MJType.Class, baseType));
        }
        currentClassSym.setAbstract(abs);
        classCount++;
        MJTable.openScope(ScopeID.CLASS);
        if (baseType != MJTable.noType) {
            // Copy base type members to current scope
            for (MJSymbol sym : baseType.getMembersList()) {
                MJTable.getCurrentScope().addToLocals(new MJSymbol(sym));
            }
        } else {
            // Insert VMT_POINTER as first field
            MJTable.insert(MJSymbol.Fld, VMT_POINTER, MJTable.intType);
        }
    }

    private void processClassDeclaration(SyntaxNode info) {
        logDebugNodeVisit(info);
        // Add local symbols and close scope
        MJTable.chainLocalSymbols(currentClassSym.getType()); // set members
        MJTable.closeScope();
        // Non-abstract class must implement all abstract methods
        if (!currentClassSym.isAbstract()) {
            for (MJSymbol sym : currentClassSym.getType().getMembersList()) {
                if (sym.getKind() == MJSymbol.Meth && sym.isAbstract()) {
                    logError(info, MessageType.UNIMPL_METHOD, currentClassSym.getName(), sym.getName());
                }
            }
        }
        // Log object definition
        logInfo(info, MessageType.DEF_SYM, "class", currentClassSym);
        currentClassSym = MJTable.noSym;
    }

    private void processMethodHeader(SyntaxNode info, boolean abs, String name, RetType retType) {
        logDebugNodeVisit(info);
        MJType returnType = MJTable.voidType;
        // Check return type
        if (!assertInvSym(info, retType.mjsymbol, "return type")) returnType = retType.mjsymbol.getType();
        // TODO: Figure out inheritance and method overriding...
        // If symbol with same name is already defined, make a new obj outside symbol table to continue analysis
        if (assertSymInCurrentScope(info, name)) {
            currentMethodSym = new MJSymbol(MJSymbol.Meth, name, returnType);
        } else {
            currentMethodSym = MJTable.insert(MJSymbol.Meth, name, returnType);
        }
        if (abs) {
            if (!MJUtils.isSymbolValid(currentClassSym)) logError(info, MessageType.OTHER, "Abstract method definition outside abstract class!");
            else if (!currentClassSym.isAbstract()) logError(info, MessageType.OTHER, "Abstract method definition inside a concrete class!");
            currentMethodSym.setAbstract(true);
        }
        if (MJTable.getCurrentScope().getId() == ScopeID.CLASS) {
            currentMethodSym.setAccess(currentAccess);
            currentMethodSym.setParent(currentClassSym);
        } else {
            methodCount++; // global method count
        }
        MJTable.openScope(MJTable.getCurrentScope().getId() == ScopeID.PROGRAM ? ScopeID.GLOBAL_METHOD : ScopeID.CLASS_METHOD);
        returnFound = false;
        currentFormalParamCount = 0;
        if (MJTable.getCurrentScope().getId() == ScopeID.CLASS_METHOD) {
            // Insert 'this' as implicit first formal parameter
            MJTable.insert(MJSymbol.Var, THIS, currentClassSym.getType());
            currentFormalParamCount++;
        }
        if (info instanceof MethodHeader) {
            ((MethodHeader) info).mjsymbol = currentMethodSym;
        } else {
            ((AbstractMethodHeader) info).mjsymbol = currentMethodSym;
        }
    }

    private void processMethodDeclaration(SyntaxNode info) {
        logDebugNodeVisit(info);
        if (currentMethodSym.getType() != MJTable.voidType && !returnFound) {
            // TODO: Make this throw a runtime exception...
            //       Instruction: 57: trap b (where b should be 1 for runtime error probably...)
            logWarn(info, MessageType.OTHER, "Method return type is not 'void' but method does not contain a return statement!");
        }
        MJTable.closeScope();
        currentMethodSym.setLevel(currentFormalParamCount);
        // Log object definition
        logInfo(info, MessageType.DEF_SYM, "method", currentMethodSym);
        currentMethodSym = null;
    }

    private void processVariableIncDec(SyntaxNode info, Designator designator) {
        logDebugNodeVisit(info);
        // Check if value can be assigned to designator
        if (assertValueNotAssignableToSymbol(info, designator.mjsymbol)) return;
        // Type checks
        if (designator.mjsymbol.getType() != MJTable.intType) {
            logError(info, MessageType.INCOMPATIBLE_TYPES, MJType.getTypeName(designator.mjsymbol.getType()), MJType.getTypeName(MJTable.intType));
        }
    }

    /******************** Public methods / constructors ***********************************************/

    public SemanticAnalyzer() {
        MJTable.init();
    }

    public int getErrorCount() { return errorCount; }
    public int getConstCount() { return constCount; }
    public int getVarCount() { return varCount; }
    public int getClassCount() { return classCount; }
    public int getMethodCount() { return methodCount; }

    /******************** Program *********************************************************************/

    @Override
    public void visit(ProgramName programName) {
        logDebugNodeVisit(programName);
        // Production variables
        String name = programName.getName();
        // Insert object into symbol table
        if (MJTable.findSymbolInCurrentScope(name) == MJTable.noSym) {
            programName.mjsymbol = MJTable.insert(MJSymbol.Prog, name, MJTable.noType);
        } else {
            logError(programName, MessageType.SYM_DEF_INV_KIND, null, name, "valid program name");
            // Create a new object outside symbol table to continue analysis
            programName.mjsymbol = new MJSymbol(MJSymbol.Prog, name, MJTable.noType);
        }
        // Open program scope
        MJTable.openScope(ScopeID.PROGRAM);
    }

    @Override
    public void visit(Program program) {
        logDebugNodeVisit(program);
        // Add local symbols and close scope
        MJSymbol programSym = program.getProgramName().mjsymbol;
        MJTable.chainLocalSymbols(programSym);
        // Check main method is defined and valid type
        MJSymbol mainSym = MJTable.findSymbolInCurrentScope(MAIN);
        if (mainSym == MJTable.noSym) {
            logError(program, MessageType.SYM_NOT_DEF, "Main method");
        } else if (mainSym.getKind() != MJSymbol.Meth) {
            logError(program, MessageType.SYM_DEF_INV_KIND, null, MAIN, "method");
        } else if (mainSym.getType() != MJTable.voidType || mainSym.getLevel() != 0) {
            logError(program, MessageType.SYM_DEF_INV_KIND, "Main method defined but", "void method with zero arguments");
        } else { // Log object definition
            logInfo(program, MessageType.DEF_SYM, "program", programSym);
        }
        // Close scope at the end in order to be able to find main
        MJTable.closeScope();
    }

    /******************** Type ************************************************************************/

    @Override
    public void visit(Type type) {
        logDebugNodeVisit(type);
        // Production variables
        String name = type.getName();
        // Search for defined type
        type.mjsymbol = MJTable.findSymbolInAnyScope(name);
        if (type.mjsymbol == MJTable.noSym) {
            logError(type, MessageType.SYM_NOT_DEF, name, MJSymbol.Type);
        } else if (type.mjsymbol.getKind() != MJSymbol.Type) {
            logError(type, MessageType.SYM_DEF_INV_KIND, null, name, "type");
            type.mjsymbol = MJTable.noSym;
        }
        currentTypeSym = type.mjsymbol;
    }

    /******************** Const ***********************************************************************/

    @Override
    public void visit(ConstAssignment constAssignment) {
        logDebugNodeVisit(constAssignment);
        // Production variables
        String name = constAssignment.getName();
        Const value = constAssignment.getConst();
        // Check if name is in use
        if (assertSymInUse(constAssignment, name)) return;
        // Type checking
        if (assertInvSym(constAssignment, currentTypeSym, "Constant type")) return;
        if (assertTypeNotBasic(constAssignment, currentTypeSym.getType(), "Constant")) return;
        // Get assigned value
        MJType assignedType;
        int assignedValue;
        if (value instanceof ConstInt) {
            assignedType = MJTable.intType;
            assignedValue = ((ConstInt) value).getValue();
            if (((ConstInt) value).getOptSign() instanceof Negative) assignedValue *= -1;
        } else if (value instanceof ConstChar) {
            assignedType = MJTable.charType;
            assignedValue = ((ConstChar) value).getValue();
        } else {
            assignedType = MJTable.boolType;
            assignedValue = ((ConstBool) value).getValue() ? 1 : 0;
        }
        // Check if types match
        if (!assignedType.equals(currentTypeSym.getType())) { logError(constAssignment, MessageType.INCOMPATIBLE_TYPES, "Constant type", "type of initial value"); return; }
        // Insert obj into symbol table
        MJSymbol sym = MJTable.insert(MJSymbol.Con, name, assignedType);
        sym.setAdr(assignedValue); // Set const value
        constCount++;
        // Log object definition
        logInfo(constAssignment, MessageType.DEF_SYM, MJType.getTypeName(assignedType) + " constant", sym);
    }

    /******************** Access modifier *************************************************************/

    @Override
    public void visit(PublicAccess publicAccess) {
        processAccessModifier(publicAccess);
    }

    @Override
    public void visit(ProtectedAccess protectedAccess) {
        processAccessModifier(protectedAccess);
    }

    @Override
    public void visit(PrivateAccess privateAccess) {
        processAccessModifier(privateAccess);
    }

    /******************** Global variables ************************************************************/

    @Override
    public void visit(Variable variable) {
        processVariable(variable, variable.getName(), variable.getOptArrayBrackets() instanceof ArrayBrackets);
    }

    /******************** Class variables *************************************************************/

    @Override
    public void visit(ClassVariable classVariable) {
        processVariable(classVariable, classVariable.getName(), classVariable.getOptArrayBrackets() instanceof ArrayBrackets);
    }

    /******************** Local variables *************************************************************/

    @Override
    public void visit(LocalVariable localVariable) {
        processVariable(localVariable, localVariable.getName(), localVariable.getOptArrayBrackets() instanceof ArrayBrackets);
    }

    /******************** Class ***********************************************************************/

    @Override
    public void visit(ClassHeader classHeader) {
        processClassHeader(classHeader, false, classHeader.getName(), classHeader.getOptClassBaseType());
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        processClassDeclaration(classDeclaration);
    }

    /******************** Abstract class **************************************************************/

    @Override
    public void visit(AbstractClassHeader abstractClassHeader) {
        processClassHeader(abstractClassHeader,true, abstractClassHeader.getName(), abstractClassHeader.getOptClassBaseType());
    }

    @Override
    public void visit(AbstractClassDeclaration abstractClassDeclaration) {
        processClassDeclaration(abstractClassDeclaration);
    }

    /******************** Return type *****************************************************************/

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

    /******************** Method **********************************************************************/

    @Override
    public void visit(MethodHeader methodHeader) {
        processMethodHeader(methodHeader, false, methodHeader.getName(), methodHeader.getRetType());
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        processMethodDeclaration(methodDeclaration);
    }

    /******************** Abstract method *************************************************************/

    @Override
    public void visit(AbstractMethodHeader abstractMethodHeader) {
        processMethodHeader(abstractMethodHeader, true, abstractMethodHeader.getName(), abstractMethodHeader.getRetType());
    }

    @Override
    public void visit(AbstractMethodDeclaration abstractMethodDeclaration) {
        processMethodDeclaration(abstractMethodDeclaration);
    }

    /******************** Formal parameters ***********************************************************/

    @Override
    public void visit(FormalParameter formalParameter) {
        logDebugNodeVisit(formalParameter);
        // Production variables
        String name = formalParameter.getName();
        // Check if name is in use
        if (assertSymInCurrentScope(formalParameter, name)) return;
        // Type checking
        if (assertInvSym(formalParameter, currentTypeSym, "Formal parameter type")) return;
        // Insert obj into symbol table
        MJSymbol sym = MJTable.insert(MJSymbol.Var, name, formalParameter.getOptArrayBrackets() instanceof ArrayBrackets ?
                new MJType(MJType.Array, currentTypeSym.getType()) : currentTypeSym.getType());
        sym.setFpPos(currentFormalParamCount++);
        // Log object definition
        logInfo(formalParameter, MessageType.DEF_SYM, MJType.getTypeName(sym.getType()) + " formal parameter", sym);
    }

    @Override
    public void visit(MethodStatementListStart methodStatementListStart) {
        logDebugNodeVisit(methodStatementListStart);
        // Add formal parameters and local variables before statements to allow recursion...
        MJTable.chainLocalSymbols(currentMethodSym);
    }

    /******************** Statements ******************************************************************/

    @Override
    public void visit(BreakStatement breakStatement) {
        logDebugNodeVisit(breakStatement);
        if (forDepth == 0) logError(breakStatement, MessageType.MISPLACED_BREAK);
    }

    @Override
    public void visit(ContinueStatement continueStatement) {
        logDebugNodeVisit(continueStatement);
        if (forDepth == 0) logError(continueStatement, MessageType.MISPLACED_CONTINUE);
    }

    @Override
    public void visit(ReadStatement readStatement) {
        logDebugNodeVisit(readStatement);
        // Parameter semantic check
        MJSymbol designatorSym = readStatement.getDesignator().mjsymbol;
        if (designatorSym == MJTable.noSym) {
            logError(readStatement, MessageType.OTHER, "Designator object not initialized for read statement!");
        } else if (designatorSym.getKind() != MJSymbol.Var && designatorSym.getKind() != MJSymbol.Elem && designatorSym.getKind() != MJSymbol.Fld) {
            logError(readStatement, MessageType.OTHER, "Read statement parameter must be either a variable, array element or class field!");
        } else {
            assertTypeNotBasic(readStatement, designatorSym.getType(), "Read statement parameter");
        }
    }

    @Override
    public void visit(PrintStatement printStatement) {
        logDebugNodeVisit(printStatement);
        // Parameter semantic check
        PrintExpr ex = printStatement.getPrintExpr();
        MJSymbol typeSym;
        if (ex instanceof  PrintExpressionAndConst) typeSym = ((PrintExpressionAndConst) ex).getExpr().mjsymbol;
        else typeSym = ((PrintOnlyExpression) ex).getExpr().mjsymbol;
        assertTypeNotBasic(printStatement, typeSym.getType(), "Print statement parameter");
    }

    @Override
    public void visit(ReturnStatement returnStatement) {
        logDebugNodeVisit(returnStatement);
        if (currentMethodSym == MJTable.noSym) logError(returnStatement, MessageType.MISPLACED_RETURN);
        else {
            returnFound = true;
            /* TODO: Uncomment this once Expr is done...
            Struct retType = MJTable.voidType;
            if (returnStatement.getOptExpr() instanceof SingleExpression) {
                retType = ((SingleExpression) returnStatement.getOptExpr()).getExpr().struct;
            }
            if (!currentMethod.getType().compatibleWith(retType)) {
                log_error(returnStatement, MessageType.INCOMPATIBLE_TYPES, "Return expression type", "method type");
            }*/
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
        if (ifDepth < 0) logError(ifOptElseStatement, MessageType.INV_COMPILER_OBJ, "if depth", ifDepth);
    }

    @Override
    public void visit(ForStatementHeader forStatementHeader) {
        forDepth++;
        logDebugNodeVisit(forStatementHeader);
    }

    @Override
    public void visit(ForStatement forStatement) {
        logDebugNodeVisit(forStatement);
        forDepth--;
        if (forDepth < 0) logError(forStatement, MessageType.INV_COMPILER_OBJ, "for depth", forDepth);
    }

    @Override
    public void visit(ForEachStatementHeader forEachStatementHeader) {
        logDebugNodeVisit(forEachStatementHeader);
        // Production variables
        String name = forEachStatementHeader.getName();
        MJSymbol designatorSym = forEachStatementHeader.getDesignator().mjsymbol;
        // Iterator checks
        MJSymbol iteratorSym = MJTable.findSymbolInAnyScope(name);
        boolean iteratorSymValid = iteratorSym != MJTable.noSym;
        if (!iteratorSymValid) logError(forEachStatementHeader, MessageType.SYM_NOT_DEF, name, MJSymbol.Var);
        else if (iteratorSym.getKind() != MJSymbol.Var) logError(forEachStatementHeader, MessageType.SYM_DEF_INV_KIND, null, name, "local or global variable");
        else if (iteratorSym.isReadOnly()) logError(forEachStatementHeader, MessageType.ITERATOR_IN_USE, name);
        // Array checks
        boolean designatorSymValid = !assertInvSym(forEachStatementHeader, designatorSym, "Designator");
        if (designatorSymValid && designatorSym.getType().getKind() != MJType.Array) {
            logError(forEachStatementHeader, MessageType.OTHER, "Designator is not an array!");
            designatorSymValid = false;
        }
        // Type checks
        if (iteratorSymValid && designatorSymValid && !iteratorSym.getType().equals(designatorSym.getType().getElemType())) {
            logError(forEachStatementHeader, MessageType.INCOMPATIBLE_TYPES, "Iterator type", "type of array element");
        }
        // Set iterator variable to read-only inside foreach statement
        if (iteratorSymValid) iteratorSym.setReadOnly(true);
        forDepth++;
    }

    @Override
    public void visit(ForEachStatement forEachStatement) {
        logDebugNodeVisit(forEachStatement);
        forDepth--;
        if (forDepth < 0) logError(forEachStatement, MessageType.INV_COMPILER_OBJ, "for depth", forDepth);
        MJSymbol iteratorSym = MJTable.findSymbolInAnyScope(forEachStatement.getForEachStatementHeader().getName());
        if (iteratorSym != MJTable.noSym) {
            iteratorSym.setReadOnly(false); // set element back to read-write as we exited foreach statement
        }
    }

    /******************** Designator Statements *******************************************************/

    @Override
    public void visit(AssignmentStatement assignmentStatement) {
        logDebugNodeVisit(assignmentStatement);
        MJSymbol designatorSym = assignmentStatement.getDesignator().mjsymbol;
        MJSymbol expressionSym = ((AssignmentExpression) assignmentStatement.getAssignExpr()).getExpr().mjsymbol;
        // Check if value can be assigned to designator
        if (assertValueNotAssignableToSymbol(assignmentStatement, designatorSym)) return;
        // Type checks
        if (!expressionSym.getType().assignableTo(designatorSym.getType())) {
            logError(assignmentStatement, MessageType.INCOMPATIBLE_TYPES, MJType.getTypeName(expressionSym.getType()), MJType.getTypeName(designatorSym.getType()));
        }
    }

    @Override
    public void visit(VariableIncrementStatement variableIncrementStatement) {
        processVariableIncDec(variableIncrementStatement, variableIncrementStatement.getDesignator());
    }

    @Override
    public void visit(VariableDecrementStatement variableDecrementStatement) {
        processVariableIncDec(variableDecrementStatement, variableDecrementStatement.getDesignator());
    }

    /******************** Method call *************************************************************/

    @Override
    public void visit(MethodCallHeader methodCallHeader) {
        logDebugNodeVisit(methodCallHeader);
        methodCallHeader.mjsymbol = methodCallHeader.getDesignator().mjsymbol;
        actualParametersStack.createParameters();
    }

    @Override
    public void visit(MethodCall methodCall) {
        logDebugNodeVisit(methodCall);
        MJSymbol methodSym = methodCall.mjsymbol = methodCall.getMethodCallHeader().mjsymbol;

        if (assertInvSym(methodCall, methodSym, "Method call designator")) return;

        if (methodSym.getKind() != MJSymbol.Meth) {
            logError(methodCall, MessageType.SYM_DEF_INV_KIND, null, methodSym.getName(), "method");
            return;
        }

        List<MJSymbol> actualParametersList = actualParametersStack.getParameters();

        if (actualParametersList == null) {
            logError(methodCall, MessageType.OTHER, "Method call processing but method call header not found!");
            return;
        }

        int formalParametersCount = methodSym.getLevel();

        if (actualParametersList.size() != formalParametersCount) {
            logError(methodCall, MJSemanticAnalyzerLogger.MessageType.OTHER, "Wrong number of parameters!");
            return;
        }

        List<MJSymbol> locals = methodSym.getLocalSymbolsList();
        int i = 0;
        for (MJSymbol currentParameter : locals) {
            if (i >= formalParametersCount) break;
            if (!actualParametersList.get(i).getType().assignableTo(currentParameter.getType())) {
                logError(methodCall, MJSemanticAnalyzerLogger.MessageType.INV_ACT_PARAM, i + 1);
            }
            i++;
        }

        logInfo(methodCall, MessageType.OTHER, "Method call! Method name: '" + methodSym.getName() + "'.");
    }

    /******************** Actual parameters ***********************************************************/

    @Override
    public void visit(ActualParameter actualParameter) {
        logDebugNodeVisit(actualParameter);
        MJSymbol expressionSym = actualParameter.getExpr().mjsymbol;

        if (assertInvSym(actualParameter, expressionSym, "Actual parameter expression")) return;

        if (!actualParametersStack.insertActualParameter(expressionSym)) {
            logError(actualParameter, MessageType.OTHER, "Actual parameter processing but method call header not found!");
        }
    }

    /******************** Conditions ******************************************************************/

    @Override
    public void visit(SimpleFact simpleFact) {
        logDebugNodeVisit(simpleFact);
        simpleFact.mjsymbol = simpleFact.getExpr().mjsymbol;
        // Check if type is boolean
        if (!simpleFact.mjsymbol.getType().equals(MJTable.boolType)) {
            logError(simpleFact, MessageType.INCOMPATIBLE_TYPES, MJType.getTypeName(simpleFact.mjsymbol.getType()), MJType.getTypeName(MJTable.boolType));
        }
    }

    @Override
    public void visit(ComplexFact complexFact) {
        logDebugNodeVisit(complexFact);
        MJType tl = complexFact.getExpr().mjsymbol.getType();
        MJType tr = complexFact.getExpr().mjsymbol.getType();
        // Check if types are compatible
        if (!tl.compatibleWith(tr)) {
            logError(complexFact, MessageType.INCOMPATIBLE_TYPES, MJType.getTypeName(tl), MJType.getTypeName(tr));
            return;
        }
        if (tl.getKind() == MJType.Array || tl.getKind() == MJType.Class) {
            Relop rel = complexFact.getRelop();
            // Convert rel to string
            String op;
            if (rel instanceof EqualityOperator) {
                op = "==";
            } else if (rel instanceof InequalityOperator) {
                op = "!=";
            } else if (rel instanceof GreaterThanOperator) {
                op = ">";
            } else if (rel instanceof GreaterOrEqualOperator) {
                op = ">=";
            } else if (rel instanceof LessThanOperator) {
                op = "<";
            } else {
                op = "<=";
            }
            // Arrays and classes can only use '!=' or '==' relational operators
            if (!(rel instanceof EqualityOperator) && !(rel instanceof InequalityOperator)) {
                logError(complexFact, MessageType.UNDEF_OP, op, MJType.getTypeName(tl), MJType.getTypeName(tr));
            }
        }
    }

    // TODO: Look into whether conditions need to be further evaluated

    /******************** Expressions *****************************************************************/

    //------------------- Factors --------------------------------------------------------------------//

    @Override
    public void visit(DesignatorFactor designatorFactor) {
        logDebugNodeVisit(designatorFactor);
        designatorFactor.mjsymbol = designatorFactor.getDesignator().mjsymbol;
    }

    @Override
    public void visit(MethodCallFactor methodCallFactor) {
        logDebugNodeVisit(methodCallFactor);
        methodCallFactor.mjsymbol = new MJSymbol(methodCallFactor.getMethodCall().mjsymbol);
    }

    @Override
    public void visit(ConstantFactor constantFactor) {
        logDebugNodeVisit(constantFactor);
        ConstFactor cf = constantFactor.getConstFactor();
        if (cf instanceof ConstFactorInt) {
            constantFactor.mjsymbol = MJTable.intTypeSym;
        } else if (cf instanceof ConstFactorChar) {
            constantFactor.mjsymbol = MJTable.charTypeSym;
        } else {
            constantFactor.mjsymbol = MJTable.boolTypeSym;
        }
    }

    @Override
    public void visit(ObjectAllocateFactor objectAllocateFactor) {
        logDebugNodeVisit(objectAllocateFactor);
        MJSymbol typeSym = objectAllocateFactor.getType().mjsymbol;
        if (typeSym.getType().getKind() != MJType.Class || typeSym.isAbstract()) {
            logError(objectAllocateFactor, MessageType.OTHER, "Object type must be a non-abstract class type!");
            objectAllocateFactor.mjsymbol = MJTable.noSym;
        } else {
            objectAllocateFactor.mjsymbol = new MJSymbol(MJSymbol.Var, "", typeSym.getType());
        }
    }

    @Override
    public void visit(ArrayAllocateFactor arrayAllocateFactor) {
        logDebugNodeVisit(arrayAllocateFactor);
        MJSymbol typeSym = arrayAllocateFactor.getType().mjsymbol;
        MJSymbol expressionSym = arrayAllocateFactor.getExpr().mjsymbol;
        if (expressionSym.getType() != MJTable.intType) {
            arrayAllocateFactor.mjsymbol = MJTable.noSym;
        } else {
            arrayAllocateFactor.mjsymbol = new MJSymbol(MJSymbol.Var, "", new MJType(MJType.Array, typeSym.getType()));
        }
    }

    @Override
    public void visit(ParenthesesExpressionFactor parenthesesExpressionFactor) {
        logDebugNodeVisit(parenthesesExpressionFactor);
        parenthesesExpressionFactor.mjsymbol = parenthesesExpressionFactor.getExpr().mjsymbol;
    }

    @Override
    public void visit(MulopExpressions mulopExpressions) {
        logDebugNodeVisit(mulopExpressions);
    }

    //------------------- Terms ----------------------------------------------------------------------//

    @Override
    public void visit(Term term) {
        logDebugNodeVisit(term);
        term.mjsymbol = term.getFactor().mjsymbol;
    }

    @Override
    public void visit(AddopExpressions addopExpressions) {
        logDebugNodeVisit(addopExpressions);

    }

    @Override
    public void visit(Expression expression) {
        logDebugNodeVisit(expression);
        expression.mjsymbol = expression.getTerm().mjsymbol;
    }

    /******************** Designators *****************************************************************/

    @Override
    public void visit(DesignatorHeader designatorHeader) {
        logDebugNodeVisit(designatorHeader);

        String name = designatorHeader.getName();

//        System.out.print(name);

        designatorHeader.mjsymbol = MJTable.findSymbolInAnyScope(name);
        if (designatorHeader.mjsymbol == MJTable.noSym) {
            logError(designatorHeader, MessageType.SYM_NOT_DEF, null, name);
        }

        if (MJTable.getCurrentScope().getId() == ScopeID.CLASS_METHOD) {

        } else { // ScopeID.GLOBAL_METHOD

        }
    }

    @Override
    public void visit(NoDesignatorParts noDesignatorParts) {
        logDebugNodeVisit(noDesignatorParts);
//        System.out.println(noDesignatorParts.getParent());
    }

    @Override
    public void visit(DesignatorParts designatorParts) {
        logDebugNodeVisit(designatorParts);
//        System.out.println(designatorParts.getParent());
//        DesignatorPart part = designatorParts.getDesignatorPart();
//        if (part instanceof MemberAccess) {
//            System.out.print('.' + ((MemberAccess) part).getName());
//        } else {
//            System.out.print('[' + ((ElementAccess) part).getExpr().mjsymbol.toString("") + ']');
//        }
    }

    /*@Override
    public void visit(MemberAccess memberAccess) {
        logDebugNodeVisit(memberAccess);

        System.out.print('.' + memberAccess.getName());
    }

    @Override
    public void visit(ElementAccess elementAccess) {
        logDebugNodeVisit(elementAccess);

        System.out.print('[' + elementAccess.getExpr().mjsymbol.toString("") + ']');
    }*/

    @Override
    public void visit(Designator designator) {
        logDebugNodeVisit(designator);

        designator.mjsymbol = designator.getDesignatorHeader().mjsymbol;

//        System.out.println();
    }
}