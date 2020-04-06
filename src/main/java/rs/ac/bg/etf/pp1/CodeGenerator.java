package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.loggers.MJCodeGeneratorLogger;
import rs.ac.bg.etf.pp1.loggers.MJCodeGeneratorLogger.MessageType;
import rs.ac.bg.etf.pp1.mj.runtime.MJCode;
import rs.ac.bg.etf.pp1.symboltable.MJTable;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJSymbol;
import rs.ac.bg.etf.pp1.util.MJUtils;

public class CodeGenerator extends VisitorAdaptor {

    private static final String MAIN = "main";

    private MJCodeGeneratorLogger logger = new MJCodeGeneratorLogger();

    private int errorCount = 0;
    private int mainPC;

    /******************** Public methods / constructors ***********************************************/

    public int getMainPC() { return mainPC; }

    public int getErrorCount() { return errorCount; }

    /******************** Error / debug methods *******************************************************/

    private void logDebug(SyntaxNode info, Object... context) {
        logger.debug(MJUtils.getLineNumber(info), -1, context);
    }

    private void logDebugNodeVisit(SyntaxNode info) {
        logger.debug(MJUtils.getLineNumber(info), -1, MessageType.NODE_VISIT, info.getClass().getSimpleName());
        // Set current node in MJCode to allow it to use logError
        MJCode.setCurrentNode(info);
    }

    // This method is public to allow MJCode to access it and log error messages!
    // Ugly I know but it seems the most reasonable solution while still using base classes and not reimplementing them.
    // Ideally I would never use the ugly code found in provided libraries, but since I must, this is my solution.
    public void logError(SyntaxNode info, Object... context) {
        errorCount++;
        logger.error(MJUtils.getLineNumber(info), -1, context);
    }

    /******************** Helper methods **************************************************************/

    /******************** Program *********************************************************************/

    @Override
    public void visit(ProgramName programName) {
        // Set generator in MJCode to allow it to use logError
        MJCode.setGenerator(this);
        logDebugNodeVisit(programName);
        // Generate code for predeclared method: chr
        MJTable.chrMethodSym.setAdr(MJCode.pc);
        MJCode.put(MJCode.return_);
        // Generate code for predeclared method: ord
        MJTable.ordMethodSym.setAdr(MJCode.pc);
        MJCode.put(MJCode.return_);
        // Generate code for predeclared method: len
        MJTable.lenMethodSym.setAdr(MJCode.pc);
        MJCode.put(MJCode.arraylength);
        MJCode.put(MJCode.return_);
    }

    @Override
    public void visit(Program program) {
        logDebugNodeVisit(program);
        if (MJCode.pc >= MJCode.MAX_CODE_SIZE) {
            logError(program, MessageType.INV_PROG_SIZE, MJCode.pc, MJCode.MAX_CODE_SIZE);
        }
    }

    /******************** Method **********************************************************************/

    @Override
    public void visit(MethodHeader methodHeader) {
        logDebugNodeVisit(methodHeader);
        // Check if method is MAIN
        if (methodHeader.getName().equals(MAIN)) {
            mainPC = MJCode.pc;
        }
        methodHeader.mjsymbol.setAdr(MJCode.pc);
        // Generate the entry
        MJCode.put(MJCode.enter);
        MJCode.put(methodHeader.mjsymbol.getLevel());
        MJCode.put(methodHeader.mjsymbol.getLocalSymbols().size());
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        logDebugNodeVisit(methodDeclaration);
        // Generate exit
        MJCode.put(MJCode.exit);
        MJCode.put(MJCode.return_);
    }

    /******************** Statements ******************************************************************/

    @Override
    public void visit(ReadStatement readStatement) {
        logDebugNodeVisit(readStatement);
        MJSymbol designatorSym = readStatement.getDesignator().mjsymbol;
        if (designatorSym.getType() == MJTable.charType) {
            MJCode.put(MJCode.bread);
        } else {
            MJCode.put(MJCode.read);
        }
        MJCode.store(designatorSym);
    }

    @Override
    public void visit(PrintStatement printStatement) {
        logDebugNodeVisit(printStatement);
        PrintExpr printExpr = printStatement.getPrintExpr();

        MJSymbol typeSym;
        int width = 0;

        if (printExpr instanceof PrintOnlyExpression) {
            typeSym = ((PrintOnlyExpression) printExpr).getExpr().mjsymbol;
        } else {
            typeSym = ((PrintExpressionAndConst) printExpr).getExpr().mjsymbol;
            width = ((PrintExpressionAndConst) printExpr).getConstValue();
        }

        MJCode.loadConst(width);
        MJCode.put(typeSym.getType() == MJTable.charType ? MJCode.bprint : MJCode.print);
    }

    /******************** Designator Statements *******************************************************/

    @Override
    public void visit(AssignmentStatement assignmentStatement) {
        logDebugNodeVisit(assignmentStatement);
        MJCode.store(assignmentStatement.getDesignator().mjsymbol);
    }

    @Override
    public void visit(VariableIncrementStatement variableIncrementStatement) {
        logDebugNodeVisit(variableIncrementStatement);
        MJSymbol designatorSym = variableIncrementStatement.getDesignator().mjsymbol;
        if (designatorSym.getKind() == MJSymbol.Var && designatorSym.getLevel() == 1) { // local variables
            MJCode.put(MJCode.inc);
            MJCode.put(designatorSym.getAdr());
            MJCode.put(1);
        } else {
            MJCode.load(designatorSym);
            MJCode.loadConst(1);
            MJCode.put(MJCode.add);
            MJCode.store(designatorSym);
        }
    }

    @Override
    public void visit(VariableDecrementStatement variableDecrementStatement) {
        logDebugNodeVisit(variableDecrementStatement);
        MJSymbol designatorSym = variableDecrementStatement.getDesignator().mjsymbol;
        if (designatorSym.getKind() == MJSymbol.Var && designatorSym.getLevel() == 1) { // local variables
            MJCode.put(MJCode.inc);
            MJCode.put(designatorSym.getAdr());
            MJCode.put(-1);
        } else {
            MJCode.load(designatorSym);
            MJCode.loadConst(1);
            MJCode.put(MJCode.sub);
            MJCode.store(designatorSym);
        }
    }

    /******************** Method call *************************************************************/

    @Override
    public void visit(MethodCall methodCall) {
        logDebugNodeVisit(methodCall);
        int relativeAddress = methodCall.mjsymbol.getAdr() - MJCode.pc;
        MJCode.put(MJCode.call);
        MJCode.put2(relativeAddress);
    }

    /******************** Expressions *****************************************************************/

    //------------------- Factors --------------------------------------------------------------------//

    @Override
    public void visit(DesignatorFactor designatorFactor) {
        logDebugNodeVisit(designatorFactor);
        // TODO: Remove this check once semantic analysis ensures designator symbol is valid
        if (MJUtils.isSymbolValid(designatorFactor.mjsymbol)) {
            MJCode.load(designatorFactor.mjsymbol);
        }
    }

    @Override
    public void visit(ConstantFactor constantFactor) {
        logDebugNodeVisit(constantFactor);
        ConstFactor cf = constantFactor.getConstFactor();
        int value;
        if (cf instanceof ConstFactorInt) {
            value = ((ConstFactorInt) cf).getValue();
        } else if (cf instanceof ConstFactorChar) {
            value = ((ConstFactorChar) cf).getValue();
        } else {
            value = ((ConstFactorBool) cf).getValue() ? 1 : 0;
        }
        MJCode.load(new MJSymbol(MJSymbol.Con, null, MJTable.charType, value, 0));
    }
}