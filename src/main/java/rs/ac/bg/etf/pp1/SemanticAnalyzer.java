package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.loggers.MJSemanticAnalyzerLogger;
import rs.ac.bg.etf.pp1.loggers.MJSemanticAnalyzerLogger.MessageType;
import rs.ac.bg.etf.pp1.symboltable.*;
import rs.ac.bg.etf.pp1.symboltable.MJTab.ScopeId;
import rs.ac.bg.etf.pp1.symboltable.concepts.*;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJObj.Access;
import rs.ac.bg.etf.pp1.util.MJUtils;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;

public class SemanticAnalyzer extends VisitorAdaptor {

    // TODO: Add symbol usage detection...

    // TODO: Add member count checks...
    private static final int MAX_GLOBAL_VARIABLE_COUNT = 65536;
    private static final int MAX_CLASS_FIELD_COUNT = 65536;
    private static final int MAX_LOCAL_VARIABLE_COUNT = 256;

    private static final String MAIN = "main";
    private static final String THIS = "this";

    private MJSemanticAnalyzerLogger logger = new MJSemanticAnalyzerLogger();

    private int errorCount = 0;
    private int constCount = 0;
    private int varCount = 0;
    private int classCount = 0;
    private int methodCount = 0;

    private int currentFormalParamCount = 0;
    private int currentLocalVarCount = 0;
    private int currentClassFieldCount = 0;

    private Obj currentType = Tab.noObj;

    private Obj currentClass = Tab.noObj;
    private Access currentAccess = Access.PUBLIC;

    private Obj currentMethod = Tab.noObj;
    private boolean returnFound = false;

    private int ifDepth = 0;
    private int forDepth = 0;

    /******************** Error / debug methods *******************************************************/

    private int get_line_number(SyntaxNode info) {
        if (info == null || info.getLine() <= 0) return -1;
        return info.getLine();
    }

    private void log_debug(SyntaxNode info, Object... context) {
        logger.debug(get_line_number(info), -1, context);
    }

    private void log_debug_node_visit(SyntaxNode info) {
        logger.debug(get_line_number(info), -1, MessageType.NODE_VISIT, info.getClass().getSimpleName());
    }

    private void log_info(SyntaxNode info, Object... context) {
        logger.info(get_line_number(info), -1, context);
    }

    private void log_warn(SyntaxNode info, Object... context) {
        logger.warn(get_line_number(info), -1, context);
    }

    private void log_error(SyntaxNode info, Object... context) {
        errorCount++;
        logger.error(get_line_number(info), -1, context);
    }

    private boolean assert_inv_obj(SyntaxNode info, Obj obj, String objName) {
        if (obj == null || obj == Tab.noObj) {
            log_error(info, MessageType.INV_OBJ, objName);
            return true;
        }
        return false;
    }

    private boolean assert_sym_in_use(SyntaxNode info, String symName) {
        if (MJTab.find(symName) != Tab.noObj) {
            log_error(info, MessageType.SYM_IN_USE, symName);
            return true;
        }
        return false;
    }

    private boolean assert_sym_in_current_scope(SyntaxNode info, String symName) {
        if (MJTab.currentScope.findSymbol(symName) != null) {
            log_error(info, MessageType.SYM_IN_USE, symName);
            return true;
        }
        return false;
    }

    private boolean assert_type_not_basic(SyntaxNode info, Struct type, String kindName) {
        if (!MJUtils.is_type_basic(type)) {
            log_error(info, MessageType.TYPE_NOT_BASIC, kindName);
            return true;
        }
        return false;
    }

    /******************** Public methods / constructors ***********************************************/

    public SemanticAnalyzer() {
        MJTab.init();
    }

    public int getErrorCount() { return errorCount; }
    public int getConstCount() { return constCount; }
    public int getVarCount() { return varCount; }
    public int getClassCount() { return classCount; }
    public int getMethodCount() { return methodCount; }

    /******************** Helper methods **************************************************************/

    private void process_variable(SyntaxNode info, String name, boolean array) {
        log_debug_node_visit(info);
        // Type checking
        if (assert_inv_obj(info, currentType, "Variable type")) return;
        // Check if name is in use
        if (assert_sym_in_current_scope(info, name)) return;
        Obj newObj = MJTab.insert(MJTab.getCurrentScopeId() == ScopeId.CLASS ? Obj.Fld : Obj.Var, name, array ?
                new MJStruct(Struct.Array, currentType.getType()) : currentType.getType());
        newObj.setLevel(MJTab.getCurrentLevel());
        ((MJObj) newObj).setAccess(currentAccess); // only relevant for class variables;
        if (MJTab.getCurrentScopeId() == ScopeId.PROGRAM) varCount++;
        // Log object definition
        log_info(info, MessageType.DEF_OBJ, MJStruct.getTypeName(newObj.getType()) + " variable", newObj);
    }

    private void process_class_header(SyntaxNode info, boolean abs, String name, OptClassBaseType optBaseType) {
        log_debug_node_visit(info);
        // Check base type
        Struct baseType = Tab.noType;
        if (optBaseType instanceof ClassBaseType) {
            Type type = ((ClassBaseType) optBaseType).getType();
            Obj typeObj = MJTab.currentScope.findSymbol(type.getName());
            if (typeObj == Tab.noObj) log_error(info, MessageType.SYM_NOT_DEF, type.getName(), Obj.Type);
            else if (typeObj.getKind() != Obj.Type || typeObj.getType().getKind() != Struct.Class) log_error(info, MessageType.SYM_DEF_INV_KIND, null, type.getName(), "class type");
            else baseType = typeObj.getType();
        }
        // If symbol with same name is already defined, make a new obj outside symbol table to continue analysis
        if (assert_sym_in_current_scope(info, name)) {
            currentClass = new MJObj(Obj.Type, name, new MJStruct(Struct.Class, baseType));
        } else {
            currentClass = MJTab.insert(Obj.Type, name, new MJStruct(Struct.Class, baseType));
        }
        ((MJObj) currentClass).setAbstract(abs);
        classCount++;
        MJTab.openScope(ScopeId.CLASS);
        if (baseType != Tab.noType) {
            for (Obj o : baseType.getMembers()) MJTab.currentScope.addToLocals(new MJObj(o));
        }
    }

    private void process_class_declaration(SyntaxNode info) {
        log_debug_node_visit(info);
        // Add local symbols and close scope
        MJTab.chainLocalSymbols(currentClass.getType()); // set members
        MJTab.closeScope(ScopeId.PROGRAM);
        // Log object definition
        log_info(info, MessageType.DEF_OBJ, "class", currentClass);
        currentClass = Tab.noObj;
    }

    private void process_method_header(SyntaxNode info, boolean abs, String name, RetType retType) {
        log_debug_node_visit(info);
        // TODO: Figure out inheritance and method overriding...
        Struct returnType = MJTab.voidType;
        // Check return type
        if (!assert_inv_obj(info, retType.obj, "return type")) returnType = retType.obj.getType();
        // If symbol with same name is already defined, make a new obj outside symbol table to continue analysis
        if (assert_sym_in_current_scope(info, name)) {
            currentMethod = new MJObj(Obj.Meth, name, returnType);
        } else {
            currentMethod = MJTab.insert(Obj.Meth, name, returnType);
        }
        if (abs) {
            if (currentClass == null || currentClass == Tab.noObj) log_error(info, MessageType.OTHER, "Abstract method definition outside abstract class!");
            else if (!((MJObj) currentClass).isAbstract()) log_error(info, MessageType.OTHER, "Abstract method definition inside a concrete class!");
            ((MJObj) currentMethod).setAbstract(true);
        }
        if (currentClass == null || currentClass == Tab.noObj) methodCount++;
        MJTab.openScope(MJTab.getCurrentScopeId() == ScopeId.PROGRAM ? ScopeId.GLOBAL_METHOD : ScopeId.CLASS_METHOD);
        returnFound = false;
        currentFormalParamCount = 0;
        if (MJTab.getCurrentScopeId() == ScopeId.CLASS_METHOD) {
            // Insert 'this' as implicit first formal parameter
            MJTab.insert(Obj.Var, THIS, currentClass.getType());
            currentFormalParamCount++;
        }
    }

    private void process_method_declaration(SyntaxNode info) {
        log_debug_node_visit(info);
        if (currentMethod.getType() != MJTab.voidType && !returnFound) {
            // TODO: Make this throw a runtime exception...
            //       Instruction: 57: trap b (where b should be 1 for runtime error probably...)
            log_warn(info, MessageType.OTHER, "Method return type is not 'void' but method does not contain a return statement!");
        }
        MJTab.closeScope(MJTab.getCurrentScopeId() == ScopeId.GLOBAL_METHOD ? ScopeId.PROGRAM : ScopeId.CLASS);
        currentMethod.setLevel(currentFormalParamCount);
        // Log object definition
        log_info(info, MessageType.DEF_OBJ, "method", currentMethod);
        currentMethod = null;
    }

    /******************** Program *********************************************************************/

    @Override
    public void visit(ProgramName programName) {
        log_debug_node_visit(programName);
        // Production variables
        String name = programName.getName();
        // Insert object into symbol table
        programName.obj = MJTab.currentScope.findSymbol(name);
        if (programName.obj == null) {
            programName.obj = MJTab.insert(Obj.Prog, name, Tab.noType);
        } else {
            log_error(programName, MessageType.SYM_DEF_INV_KIND, null, name, "valid program name");
            // Create a new object outside symbol table to continue analysis
            programName.obj = new MJObj(Obj.Prog, name, Tab.noType);
        }
        // Open program scope
        MJTab.openScope(ScopeId.PROGRAM);
    }

    @Override
    public void visit(Program program) {
        log_debug_node_visit(program);
        // Add local symbols and close scope
        Obj programObj = program.getProgramName().obj;
        MJTab.chainLocalSymbols(programObj);
        // Check main method is defined and valid type
        Obj mainObj = MJTab.currentScope.findSymbol(MAIN);
        if (mainObj == null || mainObj == Tab.noObj) {
            log_error(program, MessageType.SYM_NOT_DEF, "Main method");
        } else if (mainObj.getKind() != Obj.Meth) {
            log_error(program, MessageType.SYM_DEF_INV_KIND, null, MAIN, "method");
        } else if (mainObj.getType() != MJTab.voidType || mainObj.getLevel() != 0) {
            log_error(program, MessageType.SYM_DEF_INV_KIND, "Main method defined but", "void method with zero arguments");
        } else { // Log object definition
            log_info(program, MessageType.DEF_OBJ, "program", programObj);
        }
        // Close scope at the end in order to be able to find main
        MJTab.closeScope(ScopeId.UNIVERSE);
    }

    /******************** Type ************************************************************************/

    @Override
    public void visit(Type type) {
        log_debug_node_visit(type);
        // Production variables
        String name = type.getName();
        // Search for defined type
        type.obj = MJTab.find(name);
        if (type.obj == Tab.noObj) {
            log_error(type, MessageType.SYM_NOT_DEF, name, Obj.Type);
            currentType = Tab.noObj;
        } else if (type.obj.getKind() != Obj.Type) {
            log_error(type, MessageType.SYM_DEF_INV_KIND, null, name, "type");
            type.obj = currentType = Tab.noObj;
        } else currentType = type.obj;
    }

    /******************** Const ***********************************************************************/

    @Override
    public void visit(ConstAssignment constAssignment) {
        log_debug_node_visit(constAssignment);
        // Production variables
        String name = constAssignment.getName();
        Const value = constAssignment.getConst();
        // Check if name is in use
        if (assert_sym_in_use(constAssignment, name)) return;
        // Type checking
        if (assert_inv_obj(constAssignment, currentType, "Constant type")) return;
        if (assert_type_not_basic(constAssignment, currentType.getType(), "Constant")) return;
        // Get assigned value
        Struct assignedType;
        int assignedValue;
        if (value instanceof ConstInt) {
            assignedType = Tab.intType;
            assignedValue = ((ConstInt) value).getValue();
            if (((ConstInt) value).getOptSign() instanceof Negative) assignedValue *= -1;
        } else if (value instanceof ConstChar) {
            assignedType = Tab.charType;
            assignedValue = ((ConstChar) value).getValue();
        } else {
            assignedType = MJTab.boolType;
            assignedValue = ((ConstBool) value).getValue() ? 1 : 0;
        }
        // Check if types match
        if (!assignedType.equals(currentType.getType())) { log_error(constAssignment, MessageType.INCOMPATIBLE_TYPES, "Constant type", "type of initial value"); return; }
        // Insert obj into symbol table
        Obj newObj = MJTab.insert(Obj.Con, name, assignedType);
        newObj.setAdr(assignedValue); // Set const value
        constCount++;
        // Log object definition
        log_info(constAssignment, MessageType.DEF_OBJ, MJStruct.getTypeName(assignedType) + " constant", newObj);
    }

    /******************** Access modifier *************************************************************/

    // TODO: THIS PROBABLY DOES NOT WORK! FIX IT BY IMPLEMENTING VISIT METHODS FOR EVERY TYPE!
    @Override
    public void visit(AccessModifier accessModifier) {
        log_debug_node_visit(accessModifier);
        // Set current access modifier
        if (accessModifier instanceof PublicAccess) {
            currentAccess = Access.PUBLIC;
        } else if (accessModifier instanceof ProtectedAccess) {
            currentAccess = Access.PROTECTED;
        } else {
            currentAccess = Access.PRIVATE;
        }
        // Log current access modifier
        log_debug(accessModifier, MessageType.CUR_ACC_MOD, accessModifier.toString());
    }

    /******************** Global variables ************************************************************/

    @Override
    public void visit(Variable variable) {
        process_variable(variable, variable.getName(), variable.getOptArrayBrackets() instanceof ArrayBrackets);
    }

    /******************** Class variables *************************************************************/

    @Override
    public void visit(ClassVariable classVariable) {
        process_variable(classVariable, classVariable.getName(), classVariable.getOptArrayBrackets() instanceof ArrayBrackets);
    }

    /******************** Local variables *************************************************************/

    @Override
    public void visit(LocalVariable localVariable) {
        process_variable(localVariable, localVariable.getName(), localVariable.getOptArrayBrackets() instanceof ArrayBrackets);
    }

    /******************** Class ***********************************************************************/

    @Override
    public void visit(ClassHeader classHeader) {
        process_class_header(classHeader, false, classHeader.getName(), classHeader.getOptClassBaseType());
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        process_class_declaration(classDeclaration);
    }

    /******************** Abstract class **************************************************************/

    @Override
    public void visit(AbstractClassHeader abstractClassHeader) {
        process_class_header(abstractClassHeader,true, abstractClassHeader.getName(), abstractClassHeader.getOptClassBaseType());
    }

    @Override
    public void visit(AbstractClassDeclaration abstractClassDeclaration) {
        process_class_declaration(abstractClassDeclaration);
    }

    /******************** Return type *****************************************************************/

    @Override
    public void visit(ReturnType returnType) {
        log_debug_node_visit(returnType);
        returnType.obj = returnType.getType().obj;
    }

    @Override
    public void visit(ReturnVoid returnVoid) {
        log_debug_node_visit(returnVoid);
        returnVoid.obj = MJTab.voidObject;
    }

    /******************** Method **********************************************************************/

    @Override
    public void visit(MethodHeader methodHeader) {
        process_method_header(methodHeader, false, methodHeader.getName(), methodHeader.getRetType());
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        process_method_declaration(methodDeclaration);
    }

    /******************** Abstract method *************************************************************/

    @Override
    public void visit(AbstractMethodHeader abstractMethodHeader) {
        process_method_header(abstractMethodHeader, true, abstractMethodHeader.getName(), abstractMethodHeader.getRetType());
    }

    @Override
    public void visit(AbstractMethodDeclaration abstractMethodDeclaration) {
        process_method_declaration(abstractMethodDeclaration);
    }

    /******************** Formal parameters ***********************************************************/

    @Override
    public void visit(FormalParameter formalParameter) {
        log_debug_node_visit(formalParameter);
        // Production variables
        String name = formalParameter.getName();
        // Check if name is in use
        if (assert_sym_in_current_scope(formalParameter, name)) return;
        // Type checking
        if (assert_inv_obj(formalParameter, currentType, "Formal parameter type")) return;
        // Insert obj into symbol table
        Obj newObj = MJTab.insert(Obj.Var, name, formalParameter.getOptArrayBrackets() instanceof ArrayBrackets ?
                new MJStruct(Struct.Array, currentType.getType()) : currentType.getType());
        newObj.setFpPos(currentFormalParamCount++);
        // Log object definition
        log_info(formalParameter, MessageType.DEF_OBJ, MJStruct.getTypeName(newObj.getType()) + " formal parameter", newObj);
    }

    @Override
    public void visit(MethodStatementListStart methodStatementListStart) {
        log_debug_node_visit(methodStatementListStart);
        // TODO: Remove this check, shouldn't be needed
        if (currentMethod == Tab.noObj) log_error(methodStatementListStart, MessageType.INV_OBJ_CHECK_REACHED, "Method obj", "method statements");
        else {
            // Add formal parameters and local variables before statements to allow recursion...
            MJTab.chainLocalSymbols(currentMethod);
        }
    }

    /******************** Statements ******************************************************************/

    @Override
    public void visit(BreakStatement breakStatement) {
        log_debug_node_visit(breakStatement);
        if (forDepth == 0) log_error(breakStatement, MessageType.MISPLACED_BREAK);
    }

    @Override
    public void visit(ContinueStatement continueStatement) {
        log_debug_node_visit(continueStatement);
        if (forDepth == 0) log_error(continueStatement, MessageType.MISPLACED_CONTINUE);
    }

    @Override
    public void visit(ReadStatement readStatement) {
        log_debug_node_visit(readStatement);
        // Parameter semantic check
        Obj designator = readStatement.getDesignator().obj;
        if (designator == Tab.noObj) {
            log_error(readStatement, MessageType.OTHER, "Designator object not initialized for read statement!");
        } else if (designator.getKind() != Obj.Var && designator.getKind() != Obj.Elem && designator.getKind() != Obj.Fld) {
            log_error(readStatement, MessageType.OTHER, "Read statement parameter must be either a variable, array element or class field!");
        } else {
            assert_type_not_basic(readStatement, designator.getType(), "Read statement parameter");
        }
    }

    @Override
    public void visit(PrintStatement printStatement) {
        log_debug_node_visit(printStatement);
        // Parameter semantic check
        PrintExpr ex = printStatement.getPrintExpr();
        Struct type;
        if (ex instanceof  PrintExpressionAndConst) type = ((PrintExpressionAndConst) ex).getExpr().struct;
        else type = ((PrintOnlyExpression) ex).getExpr().struct;
        assert_type_not_basic(printStatement, type, "Print statement parameter");
    }

    @Override
    public void visit(ReturnStatement returnStatement) {
        log_debug_node_visit(returnStatement);
        if (currentMethod == Tab.noObj) log_error(returnStatement, MessageType.MISPLACED_RETURN);
        else {
            returnFound = true;
            /* TODO: Uncomment this once Expr is done...
            Struct retType = MJTab.voidType;
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
        log_debug_node_visit(ifStatementHeader);
        ifDepth++;
    }

    @Override
    public void visit(IfOptElseStatement ifOptElseStatement) {
        log_debug_node_visit(ifOptElseStatement);
        ifDepth--;
        if (ifDepth < 0) log_error(ifOptElseStatement, MessageType.INV_COMPILER_OBJ, "if depth", ifDepth);
    }

    @Override
    public void visit(ForStatementHeader forStatementHeader) {
        forDepth++;
        log_debug_node_visit(forStatementHeader);
    }

    @Override
    public void visit(ForStatement forStatement) {
        log_debug_node_visit(forStatement);
        forDepth--;
        if (forDepth < 0) log_error(forStatement, MessageType.INV_COMPILER_OBJ, "for depth", forDepth);
    }

    @Override
    public void visit(ForEachStatementHeader forEachStatementHeader) {
        log_debug_node_visit(forEachStatementHeader);
        // Production variables
        String name = forEachStatementHeader.getName();
        Obj designatorObj = forEachStatementHeader.getDesignator().obj;
        // Iterator checks
        Obj iteratorObj = MJTab.find(name);
        boolean iteratorObjValid = iteratorObj != Tab.noObj;
        if (!iteratorObjValid) log_error(forEachStatementHeader, MessageType.SYM_NOT_DEF, name, Obj.Var);
        else if (iteratorObj.getKind() != Obj.Var) log_error(forEachStatementHeader, MessageType.SYM_DEF_INV_KIND, null, name, "local or global variable");
        else if (((MJObj) iteratorObj).isReadOnly()) log_error(forEachStatementHeader, MessageType.ITERATOR_IN_USE, name);
        // Array checks
        boolean designatorObjValid = !assert_inv_obj(forEachStatementHeader, designatorObj, "Designator");
        if (designatorObjValid && designatorObj.getType().getKind() != Struct.Array) {
            log_error(forEachStatementHeader, MessageType.OTHER, "Designator is not an array!");
            designatorObjValid = false;
        }
        // Type checks
        if (iteratorObjValid && designatorObjValid && !iteratorObj.getType().equals(designatorObj.getType().getElemType())) {
            log_error(forEachStatementHeader, MessageType.INCOMPATIBLE_TYPES, "Iterator type", "type of array element");
        }
        // Set iterator variable to read-only inside foreach statement
        if (iteratorObjValid) ((MJObj) iteratorObj).setReadOnly(true);
        forDepth++;
    }

    @Override
    public void visit(ForEachStatement forEachStatement) {
        log_debug_node_visit(forEachStatement);
        forDepth--;
        if (forDepth < 0) log_error(forEachStatement, MessageType.INV_COMPILER_OBJ, "for depth", forDepth);
        Obj elem = MJTab.find(forEachStatement.getForEachStatementHeader().getName());
        ((MJObj) elem).setReadOnly(false); // set element back to read-write as we exited foreach statement
    }

    /******************** Designator Statements *******************************************************/

    @Override
    public void visit(MethodCallHeader MethodCallHeader) {
        super.visit(MethodCallHeader);
    }
}