package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.symboltable.*;
import rs.ac.bg.etf.pp1.symboltable.MyTab.ScopeId;
import rs.ac.bg.etf.pp1.symboltable.concepts.*;
import rs.ac.bg.etf.pp1.symboltable.concepts.MyObj.Access;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;

public class SemanticAnalyzer extends VisitorAdaptor {

    // TODO: Add symbol usage detection...

    private static final int MAX_GLOBAL_VARIABLE_COUNT = 65536;
    private static final int MAX_CLASS_FIELD_COUNT = 65536;
    private static final int MAX_LOCAL_VARIABLE_COUNT = 256;

    private static final String MAIN = "main";
    private static final String THIS = "this";

    private String fileName;

    private Logger log = Logger.getLogger(getClass());
    private boolean debugInfo = true;

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

    /******************** Public methods / constructors ***********************************************/

    public SemanticAnalyzer(String fileName) {
        MyTab.init();
        this.fileName = fileName;
    }

    public int getErrorCount() { return errorCount; }
    public int getConstCount() { return constCount; }
    public int getVarCount() { return varCount; }
    public int getClassCount() { return classCount; }
    public int getMethodCount() { return methodCount; }

    /******************** Error / debug methods *******************************************************/

    private String format_message(String message, SyntaxNode info) {
        if (info == null || info.getLine() <= 0) return message;
        return fileName + ':' + info.getLine() + ": " + message;
    }

    private void report_error(String message, SyntaxNode info) {
        errorCount++;
        print_error(message, info);
    }

    private void report_info(String message, SyntaxNode info) {
        debugInfo = true;
        print_info(message, info);
        debugInfo = false;
    }

    private void print_error(String message, SyntaxNode info) {
        String formattedMessage = format_message(message, info);
        log.error(formattedMessage);
        System.err.println("ERROR: " + formattedMessage);
    }

    private void print_warning(String message, SyntaxNode info) {
        String formattedMessage = format_message(message, info);
        log.warn(formattedMessage);
        System.out.println("WARNING: " + formattedMessage);
    }

    private void print_info(String message, SyntaxNode info) {
        if (!debugInfo) return;
        String formattedMessage = format_message(message, info);
        log.info(formattedMessage);
        System.out.println("INFO: " + formattedMessage);
    }

    /******************** Helper methods **************************************************************/

    private int getConstIntValue(ConstInt constInt) {
        if (constInt.getOptSign() instanceof Negative) return -1 * constInt.getValue();
        return constInt.getValue();
    }

    private int process_variable(String name, boolean array, SyntaxNode info) {
        if (currentType == Tab.noObj) {
            report_error("Variable type is invalid!", info);
            return 1;
        }
        if (MyTab.currentScope.findSymbol(name) != null) {
            report_error("Symbol '" + name + "' is already in use!", info);
            return 2;
        }
        /* TODO: Figure out if superclass fields can be hidden by redefining them...
        Obj foundObj = MyTab.currentScope.findSymbol(name);
        if (foundObj != null) {
            if (foundObj.getKind() != Obj.Fld || MyTab.getCurrentScopeId() != ScopeId.CLASS) {
                report_error("Symbol '" + name + "' is already in use!", info);
                return 2;
            }
            print_info("Base class field redefinition for symbol '" + name + "'.", info);

            return 0;
        } */
        Obj newVar = MyTab.insert(MyTab.getCurrentScopeId() == ScopeId.CLASS ? Obj.Fld : Obj.Var, name, array ? new MyStruct(Struct.Array, currentType.getType()) : currentType.getType());
        if (newVar == null || newVar == Tab.noObj) {
            report_error("Failed to insert a new object into symbol table for variable '" + name + "'!", info);
            return 3;
        }
        if (MyTab.getCurrentScopeId() == ScopeId.PROGRAM) varCount++;
        newVar.setLevel(MyTab.getCurrentLevel());
        ((MyObj) newVar).setAccess(currentAccess); // only relevant for class variables;
        report_info("Defined " + MyStruct.getTypeName(newVar.getType()) + " variable '" + name + "'. Object string: " + newVar, info);
        return 0;
    }

    private int process_class_header(boolean abs, String name, OptClassBaseType optBaseType, SyntaxNode info) {
        if (MyTab.currentScope.findSymbol(name) != null) {
            report_error("Symbol '" + name + "' is already in use!", info);
            return 1;
        }
        Struct baseType = Tab.noType;
        if (optBaseType instanceof ClassBaseType) {
            Type type = ((ClassBaseType) optBaseType).getType();
            Obj typeObj = MyTab.currentScope.findSymbol(type.getName());
            if (typeObj == Tab.noObj) {
                report_error("Type '" + type.getName() + "' is not defined!", info);
                return 2;
            }
            if (typeObj.getKind() != Obj.Type) {
                report_error("'" + type.getName() + "' is not a type!", info);
                return 3;
            }
            baseType = typeObj.getType();
            if (baseType.getKind() != Struct.Class) {
                report_error("'" + type.getName() + "' is not a class type!", info);
                return 4;
            }
        }
        currentClass = MyTab.insert(Obj.Type, name, new MyStruct(Struct.Class, baseType));
        if (currentClass == null || currentClass == Tab.noObj) {
            report_error("Failed to insert a new object into symbol table for class '" + name + "'!", info);
            return 5;
        }
        ((MyObj) currentClass).setAbstract(abs);
        classCount++;
        MyTab.openScope(ScopeId.CLASS);
        if (baseType != Tab.noType) {
            for (Obj o : baseType.getMembers()) {
                // TODO: Figure out which members need to be copied into subclass...
                if (!MyTab.currentScope.addToLocals(new MyObj(o))) {
                    report_error("Failed to copy base type members into current scope!", info);
                    return 6;
                }
            }
        }
        return 0;
    }

    private int process_class_declaration(SyntaxNode info) {
        if (currentClass == Tab.noObj) {
            report_error("Class obj not initialized but class declaration reached!", info);
            return 1;
        }
        MyTab.chainLocalSymbols(currentClass.getType()); // set members
        MyTab.closeScope(ScopeId.PROGRAM);
        report_info("Defined class '" + currentClass.getName() + "'. Object string: " + currentClass, info);
        currentClass = Tab.noObj;
        return 0;
    }

    private int process_method_header(boolean abs, String name, RetType retType, SyntaxNode info) {
        if (MyTab.currentScope.findSymbol(name) != null) {
            report_error("Symbol '" + name + "' is already in use!", info);
            return 1;
        }
        if (retType.obj == Tab.noObj) {
            report_error("Method return type invalid!", info);
            return 2;
        }
        currentMethod = MyTab.insert(Obj.Meth, name, retType.obj.getType());
        if (currentMethod == null || currentMethod == Tab.noObj) {
            report_error("Failed to insert a new object into symbol table for method '" + name + "'!", info);
            return 3;
        }
        if (abs) {
            if (currentClass == null || currentClass == Tab.noObj) {
                report_error("Abstract method definition outside abstract class!", info);
                return 4;
            }
            if (!((MyObj) currentClass).isAbstract()) {
                report_error("Abstract method definition inside a concrete class!", info);
                return 5;
            }
            ((MyObj) currentMethod).setAbstract(true); // set method to abstract
        }
        if (currentClass == null || currentClass == Tab.noObj) methodCount++;
        MyTab.openScope(MyTab.getCurrentScopeId() == ScopeId.PROGRAM ? ScopeId.GLOBAL_METHOD : ScopeId.CLASS_METHOD);
        currentFormalParamCount = 0;
        if (MyTab.getCurrentScopeId() == ScopeId.CLASS_METHOD) { // insert 'this' as implicit formal param
            MyTab.insert(Obj.Var, THIS, currentClass.getType());
            currentFormalParamCount++;
        }
        return 0;
    }

    private int process_method_declaration(SyntaxNode info) {
        if (currentMethod == Tab.noObj) {
            report_error("Method obj not initialized but method declaration reached!", info);
            return 1;
        }
        if (currentMethod.getType() != MyTab.voidType && !returnFound) {
            // TODO: Make this throw a runtime exception instead of semantic error...
            //       Possibly retain a report but do info/warn instead of error and don't fail semantic pass.
            //       Instruction: 57: trap b (where b should be 1 for runtime error probably...)
            /*report_error("Method return type is not 'void' but method does not contain a return statement!", info);
            return 2;*/
            print_warning("Method return type is not 'void' but method does not contain a return statement!", info);
        }
        MyTab.chainLocalSymbols(currentMethod);
        MyTab.closeScope(MyTab.getCurrentScopeId() == ScopeId.GLOBAL_METHOD ? ScopeId.PROGRAM : ScopeId.CLASS);
        currentMethod.setLevel(currentFormalParamCount);
        report_info("Defined method '" + currentMethod.getName() + "'. Object string: " + currentMethod, info);
        currentMethod = null;
        returnFound = false;
        return 0;
    }

    /******************** Program *********************************************************************/

    @Override
    public void visit(ProgramName programName) {
        programName.obj = MyTab.insert(Obj.Prog, programName.getName(), Tab.noType);
        MyTab.openScope(ScopeId.PROGRAM);
        print_info("Visited node 'ProgramName'.", programName);
    }

    @Override
    public void visit(Program program) {
        Obj main = MyTab.currentScope.findSymbol(MAIN);
        if (main == null || main == Tab.noObj) {
            report_error("Main method not defined!", program);
            return;
        }
        if (main.getKind() != Obj.Meth) {
            report_error("Symbol 'main' defined but is not a method!", program);
            return;
        }
        if (main.getType() != MyTab.voidType || main.getLevel() != 0) {
            report_error("Main method defined but is not a void type with zero arguments!", program);
            return;
        }
        Obj programObj = program.getProgramName().obj;
        MyTab.chainLocalSymbols(programObj);
        MyTab.closeScope(ScopeId.UNIVERSE);
        report_info("Defined program '" + programObj.getName() + "'. Object string: " + programObj, program);
        print_info("Visited node 'Program'.", program);
    }

    /******************** Type ************************************************************************/

    @Override
    public void visit(Type type) {
        Obj foundObj = MyTab.find(type.getName());
        if (foundObj == Tab.noObj) {
            report_error("Symbol '" + type.getName() + "' is not defined!", type);
            type.obj = currentType = Tab.noObj;
            return;
        }
        if (foundObj.getKind() != Obj.Type) {
            report_error("'" + type.getName() + "' is not a type!", type);
            type.obj = currentType = Tab.noObj;
            return;
        }
        type.obj = currentType = foundObj;
        print_info("Visited node 'Type'.", type);
    }

    /******************** Const ***********************************************************************/

    @Override
    public void visit(ConstAssignment constAssignment) {
        if (currentType == Tab.noObj) {
            report_error("Const type is invalid!", constAssignment);
            return;
        } else if (currentType.getType() != Tab.intType && currentType.getType() != Tab.charType && currentType.getType() != MyTab.boolType) {
            report_error("Const must be of type 'int', 'char' or 'bool'!", constAssignment);
            return;
        }
        String name = constAssignment.getName();
        if (MyTab.currentScope.findSymbol(name) != null) {
            report_error("Symbol '" + name + "' is already in use!", constAssignment);
            return;
        }
        Const c = constAssignment.getConst();
        Struct type;
        int value;
        if (c instanceof ConstInt) {
            type = Tab.intType;
            value = getConstIntValue((ConstInt) c);
        } else if (c instanceof ConstChar) {
            type = Tab.charType;
            value = ((ConstChar) c).getValue();
        } else {
            type = MyTab.boolType;
            value = ((ConstBool) c).getValue() ? 1 : 0;
        }
        if (type.equals(currentType.getType())) {
            Obj newConst = MyTab.insert(Obj.Con, name, type);
            newConst.setAdr(value);
            if (newConst == Tab.noObj) {
                report_error("Failed to insert a new object into symbol table for constant '" + name + "'!", constAssignment);
                return;
            }
            constCount++;
            report_info("Defined " + MyStruct.getTypeName(type) + " constant '" + name + "' with value: " + value + ". Object string: " + newConst, constAssignment);
        } else {
            report_error("Type mismatch of initial const value!", constAssignment);
        }
        print_info("Visited node 'ConstAssignment'.", constAssignment);
    }

    /******************** Access modifier *************************************************************/

    @Override
    public void visit(AccessModifier accessModifier) {
        if (accessModifier instanceof PublicAccess) {
            currentAccess = Access.PUBLIC;
        } else if (accessModifier instanceof ProtectedAccess) {
            currentAccess = Access.PROTECTED;
        } else {
            currentAccess = Access.PRIVATE;
        }
        print_info("Current access modifier: " + currentAccess, accessModifier);
        print_info("Visited node 'AccessModifier'.", accessModifier);
    }

    /******************** Global variables ************************************************************/

    @Override
    public void visit(Variable variable) {
        if (process_variable(variable.getName(), variable.getOptArrayBrackets() instanceof ArrayBrackets, variable) == 0) {
            print_info("Visited node 'Variable'.", variable);
        }
    }

    /******************** Class variables *************************************************************/

    @Override
    public void visit(ClassVariable classVariable) {
        if (process_variable(classVariable.getName(), classVariable.getOptArrayBrackets() instanceof ArrayBrackets, classVariable) == 0) {
            print_info("Visited node 'ClassVariable'.", classVariable);
        }
    }

    /******************** Local variables *************************************************************/

    @Override
    public void visit(LocalVariable localVariable) {
        if (process_variable(localVariable.getName(), localVariable.getOptArrayBrackets() instanceof ArrayBrackets, localVariable) == 0) {
            print_info("Visited node 'LocalVariable'.", localVariable);
        }
    }

    /******************** Class ***********************************************************************/

    @Override
    public void visit(ClassHeader classHeader) {
        if (process_class_header(false, classHeader.getName(), classHeader.getOptClassBaseType(), classHeader) == 0) {
            print_info("Visited node 'ClassHeader'.", classHeader);
        }
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        if (process_class_declaration(classDeclaration) == 0) {
            print_info("Visited node 'ClassDeclaration'.", classDeclaration);
        }
    }

    /******************** Abstract class **************************************************************/

    @Override
    public void visit(AbstractClassHeader abstractClassHeader) {
        if (process_class_header(true, abstractClassHeader.getName(), abstractClassHeader.getOptClassBaseType(), abstractClassHeader) == 0) {
            print_info("Visited node 'AbstractClassHeader'.", abstractClassHeader);
        }
    }

    @Override
    public void visit(AbstractClassDeclaration abstractClassDeclaration) {
        if (process_class_declaration(abstractClassDeclaration) == 0) {
            print_info("Visited node 'AbstractClassDeclaration'.", abstractClassDeclaration);
        }
    }

    /******************** Return type *****************************************************************/

    @Override
    public void visit(ReturnType returnType) {
        returnType.obj = returnType.getType().obj;
    }

    @Override
    public void visit(ReturnVoid returnVoid) {
        returnVoid.obj = MyTab.voidObject;
    }

    /******************** Method **********************************************************************/

    @Override
    public void visit(MethodHeader methodHeader) {
        if (process_method_header(false, methodHeader.getName(), methodHeader.getRetType(), methodHeader) == 0) {
            print_info("Visited node 'MethodHeader'.", methodHeader);
        }
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        if (process_method_declaration(methodDeclaration) == 0) {
            print_info("Visited node 'MethodDeclaration'.", methodDeclaration);
        }
    }

    /******************** Abstract method *************************************************************/

    @Override
    public void visit(AbstractMethodHeader abstractMethodHeader) {
        if (process_method_header(true, abstractMethodHeader.getName(), abstractMethodHeader.getRetType(), abstractMethodHeader) == 0) {
            print_info("Visited node 'AbstractMethodHeader'.", abstractMethodHeader);
        }
    }

    @Override
    public void visit(AbstractMethodDeclaration abstractMethodDeclaration) {
        if (process_method_declaration(abstractMethodDeclaration) == 0) {
            print_info("Visited node 'AbstractMethodDeclaration'.", abstractMethodDeclaration);
        }
    }

    /******************** Formal parameters ***********************************************************/

    @Override
    public void visit(FormalParameter formalParameter) {
        String name = formalParameter.getName();
        if (currentType == Tab.noObj) {
            report_error("Formal parameter type is invalid!", formalParameter);
            return;
        }
        if (MyTab.currentScope.findSymbol(name) != null) {
            report_error("Symbol '" + name + "' is already in use!", formalParameter);
            return;
        }
        Obj newFormalParam = MyTab.insert(Obj.Var, name, formalParameter.getOptArrayBrackets() instanceof ArrayBrackets ?
                        new MyStruct(Struct.Array, currentType.getType()) : currentType.getType());
        if (newFormalParam == null || newFormalParam == Tab.noObj) {
            report_error("Failed to insert a new object into symbol table for formal parameter '" + name + "'!", formalParameter);
            return;
        }
        newFormalParam.setFpPos(currentFormalParamCount++);
        report_info("Defined " + MyStruct.getTypeName(newFormalParam.getType()) + " formal parameter '" + name + "'. Object string: " + newFormalParam, formalParameter);
        print_info("Visited node 'FormalParameter'.", formalParameter);
    }
}