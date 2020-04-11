package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.helpers.MJConstants;
import rs.ac.bg.etf.pp1.loggers.MJCodeGeneratorLogger;
import rs.ac.bg.etf.pp1.loggers.MJCodeGeneratorLogger.MessageType;
import rs.ac.bg.etf.pp1.mj.runtime.MJCode;
import rs.ac.bg.etf.pp1.symboltable.MJTable;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJSymbol;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJType;
import rs.ac.bg.etf.pp1.util.MJUtils;

public class CodeGenerator extends VisitorAdaptor {

    private static final String MAIN = "main";

    private MJCodeGeneratorLogger logger = new MJCodeGeneratorLogger();

    private int errorCount = 0;
    private int mainPC;

    private MJSymbol currentClassSym = MJTable.noSym;
    private MJSymbol thisObjectSym = MJTable.noSym;

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
    public void visit(ProgramHeader programHeader) {
        // Set generator in MJCode to allow it to use logError
        MJCode.setGenerator(this);
        logDebugNodeVisit(programHeader);
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
        if (MJCode.pc >= MJConstants.MAX_CODE_SIZE) {
            logError(program, MessageType.INV_PROG_SIZE, MJCode.pc, MJConstants.MAX_CODE_SIZE);
        }
    }

    /******************** Class ***********************************************************************/

    @Override
    public void visit(ClassHeader classHeader) {
        logDebugNodeVisit(classHeader);
        currentClassSym = classHeader.mjsymbol;
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        logDebugNodeVisit(classDeclaration);
        currentClassSym = MJTable.noSym;
    }

    /******************** Abstract class **************************************************************/

    @Override
    public void visit(AbstractClassHeader abstractClassHeader) {
        logDebugNodeVisit(abstractClassHeader);
        currentClassSym = abstractClassHeader.mjsymbol;
    }

    @Override
    public void visit(AbstractClassDeclaration abstractClassDeclaration) {
        logDebugNodeVisit(abstractClassDeclaration);
        currentClassSym = MJTable.noSym;
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
        // TODO: Add trap 1
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
        OptPrintWidth optPrintWidth = printStatement.getOptPrintWidth();
        int width = 0;
        if (optPrintWidth instanceof PrintWidth) {
            width = ((PrintWidth) optPrintWidth).getWidth();
        }
        MJCode.loadConst(width);
        MJCode.put(printStatement.getExpr().mjsymbol.getType() == MJTable.charType ? MJCode.bprint : MJCode.print);
    }

    /******************** Designator Statements *******************************************************/

    @Override
    public void visit(AssignmentDesignatorStatement assignmentDesignatorStatement) {
        logDebugNodeVisit(assignmentDesignatorStatement);
        MJCode.store(assignmentDesignatorStatement.getDesignator().mjsymbol);
    }

    @Override
    public void visit(IncrementDesignatorStatement incrementDesignatorStatement) {
        logDebugNodeVisit(incrementDesignatorStatement);
        MJSymbol designatorSym = incrementDesignatorStatement.getDesignator().mjsymbol;
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
    public void visit(DecrementDesignatorStatement decrementDesignatorStatement) {
        logDebugNodeVisit(decrementDesignatorStatement);
        MJSymbol designatorSym = decrementDesignatorStatement.getDesignator().mjsymbol;
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

    /******************** Method call *****************************************************************/

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
        if (!MJUtils.isSymbolValid(designatorFactor.mjsymbol)) return;
        MJCode.load(designatorFactor.mjsymbol);
    }

    @Override
    public void visit(ConstantFactor constantFactor) {
        logDebugNodeVisit(constantFactor);
        MJCode.load(constantFactor.mjsymbol);
    }

    @Override
    public void visit(AllocatorFactor allocatorFactor) {
        logDebugNodeVisit(allocatorFactor);
        if (!MJUtils.isSymbolValid(allocatorFactor.mjsymbol)) return;
        MJSymbol sym = allocatorFactor.mjsymbol;
        if (allocatorFactor.getOptArrayIndexer() instanceof SingleArrayIndexer) { // array allocator
            MJCode.put(MJCode.newarray);
            MJCode.put(sym.getType() == MJTable.charType ? 0 : 1);
        } else { // object allocator
            MJCode.put(MJCode.new_);
            // Object size = number of fields * 4B
            MJCode.put2(sym.getType().getNumberOfFields() * 4);
            // Initialize virtual method table pointer
            MJCode.put(MJCode.dup);
            MJCode.loadConst(sym.getAdr());
            MJCode.store(sym.getType().getMembersTable().searchKey(MJConstants.VMT_POINTER));
        }
    }

    /******************** Designators *****************************************************************/

    // TODO: Test this solution and possibly tweak it...

    @Override
    public void visit(IdentifierDesignator identifierDesignator) {
        logDebugNodeVisit(identifierDesignator);
        if (!MJUtils.isSymbolValid(identifierDesignator.mjsymbol)) return;
        MJSymbol designatorSym = identifierDesignator.mjsymbol;
        if (MJUtils.isSymbolValid(currentClassSym) && (designatorSym.getKind() == MJSymbol.Fld ||
                designatorSym.getKind() == MJSymbol.Meth)) {
            MJSymbol this_ = MJTable.findSymbolInAnyScope(MJConstants.THIS);
            MJCode.load(this_);
        }
        if (designatorSym.getType().getKind() == MJType.Array) {
            MJCode.load(designatorSym);
        }
    }

    @Override
    public void visit(MemberAccessDesignator memberAccessDesignator) {
        logDebugNodeVisit(memberAccessDesignator);
        if (!MJUtils.isSymbolValid(memberAccessDesignator.mjsymbol)) return;
        MJSymbol designatorSym = memberAccessDesignator.getDesignator().mjsymbol;
        if (MJUtils.isValueAssignableToSymbol(designatorSym)) {
            MJCode.load(designatorSym);
        }
        if (designatorSym.getType().getKind() == MJType.Array) {
            MJCode.load(designatorSym);
        }
    }
}