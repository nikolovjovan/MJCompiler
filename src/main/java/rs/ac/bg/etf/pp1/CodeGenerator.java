package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.loggers.MJCodeGeneratorLogger;
import rs.ac.bg.etf.pp1.loggers.MJCodeGeneratorLogger.MessageType;
import rs.ac.bg.etf.pp1.mj.runtime.MJCode;
import rs.ac.bg.etf.pp1.symboltable.MJTable;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJSymbol;

public class CodeGenerator extends VisitorAdaptor {

    private static final int MAX_CODE_SIZE = 8192;

    private static final String MAIN = "main";

    private MJCodeGeneratorLogger logger = new MJCodeGeneratorLogger();

    private int errorCount = 0;
    private int mainPC;

    /******************** Public methods / constructors ***********************************************/

    public int getMainPC() { return mainPC; }

    public int getErrorCount() { return errorCount; }

    /******************** Error / debug methods *******************************************************/

    private int getLineNumber(SyntaxNode info) {
        if (info == null || info.getLine() <= 0) return -1;
        return info.getLine();
    }

    private void logDebug(SyntaxNode info, Object... context) {
        logger.debug(getLineNumber(info), -1, context);
    }

    private void logDebugNodeVisit(SyntaxNode info) {
        logger.debug(getLineNumber(info), -1, MessageType.NODE_VISIT, info.getClass().getSimpleName());
    }

    private void logError(SyntaxNode info, Object... context) {
        errorCount++;
        logger.error(getLineNumber(info), -1, context);
    }

    /******************** Helper methods **************************************************************/

    /******************** Program *********************************************************************/

    @Override
    public void visit(ProgramName programName) {
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
        if (MJCode.pc >= MAX_CODE_SIZE) {
            logError(program, MessageType.INV_PROG_SIZE, MJCode.pc, MAX_CODE_SIZE);
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
    public void visit(PrintStatement printStatement) {
        logDebugNodeVisit(printStatement);
        PrintExpr printExpr = printStatement.getPrintExpr();

        MJSymbol typeSymbol;
        int repetition = 1;
        int width = 5;
        int instruction = MJCode.print;

        if (printExpr instanceof PrintOnlyExpression) {
            typeSymbol = ((PrintOnlyExpression) printExpr).getExpr().mjsymbol;
        } else {
            typeSymbol = ((PrintExpressionAndConst) printExpr).getExpr().mjsymbol;
            repetition = ((PrintExpressionAndConst) printExpr).getConstValue();
        }

        if (typeSymbol.getType() == MJTable.charType) {
            width = 1; // char print size
            instruction = MJCode.bprint;
        }

        if (repetition == 1) {
            MJCode.loadConst(width);
            MJCode.put(instruction);
        } else if (repetition > 1) {
            MJCode.loadConst(repetition);

            int loopStart = MJCode.pc;

            MJCode.put(MJCode.dup2);
            MJCode.put(MJCode.pop);
            MJCode.loadConst(width);
            MJCode.put(instruction);

            MJCode.loadConst(1);
            MJCode.put(MJCode.sub);

            MJCode.put(MJCode.dup);
            MJCode.loadConst(0);
            MJCode.put(MJCode.jne);
            MJCode.put2(loopStart - MJCode.pc + 1);

            MJCode.put(MJCode.pop);
            MJCode.put(MJCode.pop);
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

    /******************** Designators *****************************************************************/

    @Override
    public void visit(Designator designator) {
        logDebugNodeVisit(designator);
        if (designator.mjsymbol != null && designator.mjsymbol != MJTable.noSym) {
            MJCode.load(designator.mjsymbol);
        }
    }
}