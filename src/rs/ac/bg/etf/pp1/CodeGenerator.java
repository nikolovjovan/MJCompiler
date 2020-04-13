package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.helpers.MJConstants;
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

    private MJSymbol currentClassSym = MJTable.noSym;

    /******************** Public methods / constructors ***************************************************************/

    public int getMainPC() { return mainPC; }

    public int getErrorCount() { return errorCount; }

    /******************** Error / debug methods ***********************************************************************/

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

    /******************** Helper methods ******************************************************************************/

    private void prepareDesignatorForStore(MJSymbol designatorSym) {
        // Duplicates parameters on stack to enable operators which both load and store into designator
        // Operators that require this are: ++, --, +=, -=, *=, /= and %=
        if (designatorSym.getKind() == MJSymbol.Fld) {
            MJCode.put(MJCode.dup);
        } else if (designatorSym.getKind() == MJSymbol.Elem) {
            MJCode.put(MJCode.dup2);
        }
    }

    private void processIncOrDec(SyntaxNode info, MJSymbol designatorSym, boolean increment) {
        logDebugNodeVisit(info);
        if (designatorSym.getKind() == MJSymbol.Var && designatorSym.getLevel() == 1) { // local variables
            MJCode.put(MJCode.inc);
            MJCode.put(designatorSym.getAdr());
            MJCode.put(increment ? 1 : -1);
        } else {
            prepareDesignatorForStore(designatorSym);
            MJCode.load(designatorSym);
            MJCode.loadConst(1);
            MJCode.put(increment ? MJCode.add : MJCode.sub);
            MJCode.store(designatorSym);
        }
    }

    private void insertArithmeticOperator(Rightop op) {
        if (op instanceof RightAddOperator) {
            RightAddop addOp = ((RightAddOperator) op).getRightAddop();
            if (addOp instanceof AddAssignOperator) {
                MJCode.put(MJCode.add);
            } else {
                MJCode.put(MJCode.sub);
            }
        } else {
            RightMulop mulOp = ((RightMulOperator) op).getRightMulop();
            if (mulOp instanceof MulAssignOperator) {
                MJCode.put(MJCode.mul);
            } else if (mulOp instanceof DivAssignOperator) {
                MJCode.put(MJCode.div);
            } else {
                MJCode.put(MJCode.rem);
            }
        }
    }

    /******************** Program *************************************************************************************/

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

    /******************** Class ***************************************************************************************/

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

    /******************** Abstract class ******************************************************************************/

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

    /******************** Method **************************************************************************************/

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

    /******************** Designator **********************************************************************************/

    @Override
    public void visit(IdentifierDesignator identifierDesignator) {
        logDebugNodeVisit(identifierDesignator);
        MJSymbol designatorSym = identifierDesignator.mjsymbol;
        if (MJUtils.isSymbolValid(currentClassSym) && (designatorSym.getKind() == MJSymbol.Fld ||
                designatorSym.getKind() == MJSymbol.Meth)) {
            MJSymbol this_ = MJTable.findSymbolInAnyScope(MJConstants.THIS);
            MJCode.load(this_);
        }
        if (MJUtils.isValueAssignableToSymbol(designatorSym) &&
                identifierDesignator.getParent() instanceof ElementAccessDesignator) {
            MJCode.load(designatorSym);
        }
    }

    @Override
    public void visit(MemberAccessDesignator memberAccessDesignator) {
        logDebugNodeVisit(memberAccessDesignator);
        MJSymbol designatorSym = memberAccessDesignator.getDesignator().mjsymbol;
        if (MJUtils.isValueAssignableToSymbol(designatorSym)) {
            MJCode.load(designatorSym);
        }
        if (MJUtils.isValueAssignableToSymbol(designatorSym) &&
                memberAccessDesignator.getParent() instanceof ElementAccessDesignator) {
            MJCode.load(designatorSym);
        }
    }

    @Override
    public void visit(AssignmentHeader assignmentHeader) {
        logDebugNodeVisit(assignmentHeader);
        MJSymbol designatorSym = assignmentHeader.getDesignator().mjsymbol;
        AssignmentDesignatorStatement statement = (AssignmentDesignatorStatement) assignmentHeader.getParent();
        AssignmentFooter footer = (AssignmentFooter) statement.getAssignFooter();
        if (!(footer.getAssignop() instanceof AssignOperator)) {
            prepareDesignatorForStore(designatorSym);
            MJCode.load(designatorSym);
        }
    }

    @Override
    public void visit(AssignmentDesignatorStatement assignmentDesignatorStatement) {
        logDebugNodeVisit(assignmentDesignatorStatement);
        AssignmentHeader header = (AssignmentHeader) assignmentDesignatorStatement.getAssignHeader();
        AssignmentFooter footer = (AssignmentFooter) assignmentDesignatorStatement.getAssignFooter();
        MJSymbol designatorSym = header.getDesignator().mjsymbol;
        if (!(footer.getAssignop() instanceof AssignOperator)) {
            insertArithmeticOperator(((RightOperator) footer.getAssignop()).getRightop());
        }
        MJCode.store(designatorSym);
    }

    @Override
    public void visit(IncrementDesignatorStatement incrementDesignatorStatement) {
        processIncOrDec(incrementDesignatorStatement, incrementDesignatorStatement.getDesignator().mjsymbol, true);
    }

    @Override
    public void visit(DecrementDesignatorStatement decrementDesignatorStatement) {
        processIncOrDec(decrementDesignatorStatement, decrementDesignatorStatement.getDesignator().mjsymbol, false);
    }

    /******************** Statement ***********************************************************************************/

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

    /******************** Method call *********************************************************************************/

    @Override
    public void visit(MethodCall methodCall) {
        logDebugNodeVisit(methodCall);
        int relativeAddress = methodCall.mjsymbol.getAdr() - MJCode.pc;
        MJCode.put(MJCode.call);
        MJCode.put2(relativeAddress);
    }

    /******************** Expression **********************************************************************************/

    //------------------- Expression ---------------------------------------------------------------------------------//

    @Override
    public void visit(AssignmentExpression assignmentExpression) {
        logDebugNodeVisit(assignmentExpression);
        MJSymbol leftSym = assignmentExpression.getLeftExpr().mjsymbol;
        // Depending on operation insert appropriate instruction
        insertArithmeticOperator(assignmentExpression.getRightop());
        // TODO: Check dup for object fields
        // Store value into left expression symbol
        MJCode.put(leftSym.getKind() == MJSymbol.Var ? MJCode.dup :
                (leftSym.getKind() == MJSymbol.Fld ? MJCode.dup_x1 : MJCode.dup_x2));
        MJCode.store(leftSym);
    }

    @Override
    public void visit(MultipleTermsExpression multipleTermsExpression) {
        logDebugNodeVisit(multipleTermsExpression);
        // Depending on operation insert appropriate instruction
        LeftAddop op = multipleTermsExpression.getLeftAddop();
        if (op instanceof AddOperator) {
            MJCode.put(MJCode.add);
        } else {
            MJCode.put(MJCode.sub);
        }
    }

    @Override
    public void visit(SingleTermExpression singleTermExpression) {
        logDebugNodeVisit(singleTermExpression);
        // If expression is negated put a neg instruction
        if (singleTermExpression.getOptSign() instanceof MinusSign) {
            MJCode.put(MJCode.neg);
        }
    }

    //------------------- Term ---------------------------------------------------------------------------------------//

    @Override
    public void visit(MultipleFactorsTerm multipleFactorsTerm) {
        logDebugNodeVisit(multipleFactorsTerm);
        // Depending on operation insert appropriate instruction
        LeftMulop op = multipleFactorsTerm.getLeftMulop();
        if (op instanceof MulOperator) {
            MJCode.put(MJCode.mul);
        } else if (op instanceof DivOperator) {
            MJCode.put(MJCode.div);
        } else {
            MJCode.put(MJCode.rem);
        }
    }

    //------------------- Factor -------------------------------------------------------------------------------------//

    @Override
    public void visit(DesignatorFactor designatorFactor) {
        logDebugNodeVisit(designatorFactor);
        MJSymbol designatorSym = designatorFactor.mjsymbol;
        if (designatorSym.getKind() == MJSymbol.Fld || designatorSym.getKind() == MJSymbol.Elem) {
            if (designatorFactor.getParent() instanceof SingleFactorTerm) {
                SingleFactorTerm sft = (SingleFactorTerm) designatorFactor.getParent();
                if (sft.getParent() instanceof SingleTermExpression) {
                    SingleTermExpression ste = (SingleTermExpression) sft.getParent();
                    if (ste.getParent() instanceof AssignmentExpression) {
                        prepareDesignatorForStore(designatorSym);
                    }
                }
            }
        }
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
}