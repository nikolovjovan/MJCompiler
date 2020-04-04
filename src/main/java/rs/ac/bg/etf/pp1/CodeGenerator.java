package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.loggers.MJCodeGeneratorLogger;
import rs.ac.bg.etf.pp1.loggers.MJCodeGeneratorLogger.MessageType;
import rs.ac.bg.etf.pp1.loggers.MJSemanticAnalyzerLogger;
import rs.ac.bg.etf.pp1.symboltable.MJTab;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;

import java.util.Collection;
import java.util.List;

public class CodeGenerator extends VisitorAdaptor {

    private static final int MAX_CODE_SIZE = 8192;

    private static final String MAIN = "main";
    private static final String THIS = "this";

    private MJCodeGeneratorLogger logger = new MJCodeGeneratorLogger();

    private int errorCount = 0;
    private int mainPC;

    /******************** Public methods / constructors ***********************************************/

    public int getMainPC() { return mainPC; }

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

    private void log_error(SyntaxNode info, Object... context) {
        errorCount++;
        logger.error(get_line_number(info), -1, context);
    }

    /******************** Helper methods **************************************************************/

    /******************** Program *********************************************************************/

    @Override
    public void visit(ProgramName programName) {
        log_debug_node_visit(programName);
        // Insert global predefined methods
        Obj methodObj = MJTab.find("ord");
        methodObj.setAdr(Code.pc);
        Code.put(Code.return_);
        methodObj = MJTab.find("chr");
        methodObj.setAdr(Code.pc);
        Code.put(Code.return_);
        methodObj = MJTab.find("len");
        methodObj.setAdr(Code.pc);
        Code.put(Code.arraylength);
        Code.put(Code.return_);
    }

    @Override
    public void visit(Program program) {
        log_debug_node_visit(program);
        if (Code.pc >= MAX_CODE_SIZE) {
            log_error(program, MessageType.INV_PROG_SIZE, Code.pc, MAX_CODE_SIZE);
        }
    }

    /******************** Method **********************************************************************/

    @Override
    public void visit(MethodHeader methodHeader) {
        log_debug_node_visit(methodHeader);
        // Check if method is MAIN
        if (methodHeader.getName().equals(MAIN)) {
            mainPC = Code.pc;
        }
        methodHeader.obj.setAdr(Code.pc);
        // Generate the entry
        Code.put(Code.enter);
        Code.put(methodHeader.obj.getLevel());
        Code.put(methodHeader.obj.getLocalSymbols().size());
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        log_debug_node_visit(methodDeclaration);
        // Generate exit
        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    /******************** Statements ******************************************************************/

    @Override
    public void visit(PrintStatement printStatement) {
        log_debug_node_visit(printStatement);
        PrintExpr printExpr = printStatement.getPrintExpr();

        Obj typeObj;
        int repetition = 1;
        int width = 5;
        int instruction = Code.print;

        if (printExpr instanceof PrintOnlyExpression) {
            typeObj = ((PrintOnlyExpression) printExpr).getExpr().obj;
        } else {
            typeObj = ((PrintExpressionAndConst) printExpr).getExpr().obj;
            repetition = ((PrintExpressionAndConst) printExpr).getConstValue();
        }

        if (typeObj.getType() == Tab.charType) {
            width = 1; // char print size
            instruction = Code.bprint;
        }

        if (repetition == 1) {
            Code.loadConst(width);
            Code.put(instruction);
        } else if (repetition > 1) {
            Code.loadConst(repetition);

            int loopStart = Code.pc;

            Code.put(Code.dup2);
            Code.put(Code.pop);
            Code.loadConst(width);
            Code.put(instruction);

            Code.loadConst(1);
            Code.put(Code.sub);

            Code.put(Code.dup);
            Code.loadConst(0);
            Code.put(Code.jcc + Code.ne);
            Code.put2(loopStart - Code.pc + 1);

            Code.put(Code.pop);
            Code.put(Code.pop);
        }
    }

    /******************** Method call *************************************************************/

    @Override
    public void visit(MethodCall methodCall) {
        log_debug_node_visit(methodCall);
        int relativeAddress = methodCall.obj.getAdr() - Code.pc;
        Code.put(Code.call);
        Code.put2(relativeAddress);
    }

    /******************** Expressions *****************************************************************/

    //------------------- Factors --------------------------------------------------------------------//

    @Override
    public void visit(ConstantFactor constantFactor) {
        log_debug_node_visit(constantFactor);
        ConstFactor cf = constantFactor.getConstFactor();
        int value;
        if (cf instanceof ConstFactorInt) {
            value = ((ConstFactorInt) cf).getValue();
        } else if (cf instanceof ConstFactorChar) {
            value = ((ConstFactorChar) cf).getValue();
        } else {
            value = ((ConstFactorBool) cf).getValue() ? 1 : 0;
        }
        Obj tmp = new Obj(Obj.Con, "", MJTab.charType, value, 0);
        Code.load(tmp);
    }

    /******************** Designators *****************************************************************/

    @Override
    public void visit(Designator designator) {
        log_debug_node_visit(designator);
        if (designator.obj != null && designator.obj != Tab.noObj) {
            Code.load(designator.obj);
        }
    }
}