package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.helpers.JumpAddressStack;
import rs.ac.bg.etf.pp1.helpers.MJConstants;
import rs.ac.bg.etf.pp1.loggers.MJCodeGeneratorLogger;
import rs.ac.bg.etf.pp1.loggers.MJCodeGeneratorLogger.MessageType;
import rs.ac.bg.etf.pp1.mj.runtime.MJCode;
import rs.ac.bg.etf.pp1.symboltable.MJTable;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJSymbol;
import rs.ac.bg.etf.pp1.util.MJUtils;

import java.util.List;

public class CodeGenerator extends VisitorAdaptor {

    private static final String MAIN = "main";

    private MJCodeGeneratorLogger logger = new MJCodeGeneratorLogger();

    private int errorCount = 0;
    private int mainPC;

    private MJSymbol currentClassSym = MJTable.noSym;

    private JumpAddressStack jumpAddressStack = new JumpAddressStack();

    /******************** Public methods / constructors ***************************************************************/

    public int getMainPC() { return mainPC; }

    public int getErrorCount() { return errorCount; }

    /******************** Error / debug methods ***********************************************************************/

    private void logDebugNodeVisit(SyntaxNode info) {
        logger.debug(MJUtils.getLineNumber(info), -1, MessageType.NODE_VISIT, info.getClass().getSimpleName());
        // Set current node in MJCode to allow it to call logError method.
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

    // Checks if there is a designator part after this one (MemberAccessDesignator or ElementAccessDesignator) and
    // loads current designator symbol onto the stack in order to allow the next part to load.
    // For element access this must be done in previous node because ArrayIndexer contains an Expr node which puts
    // element index onto the stack. As all instructions require array address be on the stack before index this is
    // the only way to achieve that.
    // For class member access this can be done in the following node but for consistency sake it is done in
    // in previous node.
    private void prepareForNextDesignatorNode(SyntaxNode node, MJSymbol sym) {
        if (node.getParent() instanceof MemberAccessDesignator || node.getParent() instanceof ElementAccessDesignator) {
            MJCode.load(sym);
        }
    }

    // Duplicates elements on the stack to enable operators which both load and store into the designator.
    // Operators that require this are: ++, --, +=, -=, *=, /= and %=.
    private void duplicateDesignator(MJSymbol sym) {
        if (sym.getKind() == MJSymbol.Fld) {
            MJCode.put(MJCode.dup);
        } else if (sym.getKind() == MJSymbol.Elem) {
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
            duplicateDesignator(designatorSym);
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

    // Checks if there is a condition fact after this one (MultipleFactsConditionTerm) and inserts a false jump for
    // the specified relative operator. Also adds jump address to current false jump address list.
    private void prepareForNextConditionFactNode(SyntaxNode node, int op) {
        if (node.getParent() instanceof MultipleFactsConditionTerm) {
            // Generate false jump since the next operator is AND
            MJCode.putFalseJump(op, 0);
            // Add patch address
            jumpAddressStack.insertFalseJumpAddress(MJCode.pc - 2);
        }
    }

    // Checks if there is a condition term after this one (MultipleTermsCondition) and inserts a true jump for
    // the specified relative operator. Also adds jump address to current true jump address list.
    private void prepareForNextConditionTermNode(SyntaxNode node, int op) {
        if (node.getParent() instanceof MultipleTermsCondition) {
            // Generate true jump since the next operator is OR
            MJCode.putTrueJump(op, 0);
            // Add patch address
            jumpAddressStack.insertTrueJumpAddress(MJCode.pc - 2);
        }
    }

    private void patchJumpAddresses(boolean trueJumps) {
        // Get specified jump patch address list
        List<Integer> patchAddressList = jumpAddressStack.getJumpAddressList(trueJumps);
        for (Integer patchAddress : patchAddressList) {
            MJCode.fixup(patchAddress);
        }
        // Clear the list since everything has been patched
        patchAddressList.clear();
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
        if (methodDeclaration.getMethodHeader().mjsymbol.getType() == MJTable.voidType) {
            // Insert an exit and a return instruction in case method return type is void. This is not optimized and
            // can generate unreachable code if all control flows contain a return instruction.
            // Checking control flow would require a complex implementation, so this is not optimized and can generate
            // unreachable code. However it will always work so that's a worthy trade-off in this case.
            MJCode.put(MJCode.exit);
            MJCode.put(MJCode.return_);
        } else {
            // Insert a trap instruction which will generate a runtime error and will only be executed if there is no
            // return statement in current control flow. Again, this is a trade-off similar to one above.
            MJCode.put(MJCode.trap);
            MJCode.put(1);
        }
    }

    /******************** Designator **********************************************************************************/

    //------------------- Designator ---------------------------------------------------------------------------------//

    @Override
    public void visit(IdentifierDesignator identifierDesignator) {
        logDebugNodeVisit(identifierDesignator);
        MJSymbol sym = identifierDesignator.mjsymbol;
        if (sym.isReadOnly()) { // Read-only iterator variable for foreach statement
            // Load array address (parent symbol gets set to array this iterator loops through)
            MJCode.load(sym.getParent());
            // Load array index (this variable will hold index inside the loop)
            MJCode.load(sym);
            // Change this node's inner symbol to treat this as an array element which it actually is
            identifierDesignator.mjsymbol = new MJSymbol(MJSymbol.Elem, sym.getName(), sym.getType());
        } else { // Normal identifier
            if (MJUtils.isSymbolValid(currentClassSym) && currentClassSym.getLocalSymbols().contains(sym)) {
                MJSymbol this_ = MJTable.findSymbolInAnyScope(MJConstants.THIS);
                MJCode.load(this_);
            }
            prepareForNextDesignatorNode(identifierDesignator, sym);
        }
    }

    @Override
    public void visit(MemberAccessDesignator memberAccessDesignator) {
        logDebugNodeVisit(memberAccessDesignator);
        prepareForNextDesignatorNode(memberAccessDesignator, memberAccessDesignator.mjsymbol);
    }

    @Override
    public void visit(ElementAccessDesignator elementAccessDesignator) {
        logDebugNodeVisit(elementAccessDesignator);
        prepareForNextDesignatorNode(elementAccessDesignator, elementAccessDesignator.mjsymbol);
    }

    //------------------- DesignatorStatement ------------------------------------------------------------------------//


    @Override
    public void visit(SingleDesignatorStatement singleDesignatorStatement) {
        logDebugNodeVisit(singleDesignatorStatement);
        // TODO: Disable designator statement code generation to prevent for loop updateStatement to be generated
    }

    @Override
    public void visit(AssignmentHeader assignmentHeader) {
        logDebugNodeVisit(assignmentHeader);
        MJSymbol designatorSym = assignmentHeader.getDesignator().mjsymbol;
        AssignmentDesignatorStatement statement = (AssignmentDesignatorStatement) assignmentHeader.getParent();
        AssignmentFooter footer = (AssignmentFooter) statement.getAssignFooter();
        if (!(footer.getAssignop() instanceof AssignOperator)) {
            duplicateDesignator(designatorSym);
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
    public void visit(BreakStatement breakStatement) {
        logDebugNodeVisit(breakStatement);
        // TODO: Implement this method!
    }

    @Override
    public void visit(ContinueStatement continueStatement) {
        logDebugNodeVisit(continueStatement);
        // TODO: Implement this method!
    }

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

    @Override
    public void visit(ReturnStatement returnStatement) {
        logDebugNodeVisit(returnStatement);
        // Generate exit and return instruction; return value is already on stack
        MJCode.put(MJCode.exit);
        MJCode.put(MJCode.return_);
    }

    @Override
    public void visit(IfStatementHeaderStart ifStatementHeaderStart) {
        logDebugNodeVisit(ifStatementHeaderStart);
        jumpAddressStack.createJumpAddressList();
    }

    @Override
    public void visit(IfStatementHeader ifStatementHeader) {
        logDebugNodeVisit(ifStatementHeader);
        MJSymbol conditionSym = ifStatementHeader.getCondition().mjsymbol;
        // Generate false jump for last relational operation
        MJCode.putFalseJump(conditionSym.getAdr(), 0);
        // Add patch address
        jumpAddressStack.insertFalseJumpAddress(MJCode.pc - 2);
        // Fix all true jumps
        patchJumpAddresses(true);
    }

    @Override
    public void visit(ElseStatementStart elseStatementStart) {
        logDebugNodeVisit(elseStatementStart);
        // Place an unconditional jump to skip else statement
        MJCode.putJump(0);
        // Remember jump address to patch
        elseStatementStart.integer = MJCode.pc - 2;
        // Fix all false jumps
        patchJumpAddresses(false);
    }

    @Override
    public void visit(IfOptElseStatement ifOptElseStatement) {
        logDebugNodeVisit(ifOptElseStatement);
        if (ifOptElseStatement.getOptElseStatement() instanceof ElseStatement) {
            // Fix unconditional jump
            MJCode.fixup(((ElseStatement) ifOptElseStatement.getOptElseStatement()).getElseStatementStart().integer);
        } else {
            // Fix all false jumps
            patchJumpAddresses(false);
        }
        jumpAddressStack.popJumpAddressList();
    }

    @Override
    public void visit(ForStatementHeaderStart forStatementHeaderStart) {
        logDebugNodeVisit(forStatementHeaderStart);
        jumpAddressStack.createJumpAddressList();
    }

    @Override
    public void visit(ForStatementHeader forStatementHeader) {
        logDebugNodeVisit(forStatementHeader);
        // TODO: Enable designator statement code generation to allow inner statements to be generated
    }

    @Override
    public void visit(ForStatement forStatement) {
        logDebugNodeVisit(forStatement);
        // TODO: Generate updateStatement and a simple JMP statement to loop condition
        jumpAddressStack.popJumpAddressList();
    }

    @Override
    public void visit(ForEachStatementHeader forEachStatementHeader) {
        logDebugNodeVisit(forEachStatementHeader);
        jumpAddressStack.createJumpAddressList();
    }

    @Override
    public void visit(ForEachStatement forEachStatement) {
        logDebugNodeVisit(forEachStatement);
        jumpAddressStack.popJumpAddressList();
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
        // This could easily support standard '=' operator for expressions but since project specification does not
        // require it it is not currently supported. The only difference would be that '=' operator would not insert
        // any arithmetic instruction and just duplicate the result to allow storing.
        // Therefore, this implementation will always insert an arithmetic operator before duplicating the result.
        insertArithmeticOperator(assignmentExpression.getRightop());
        // Depending on the kind of the left symbol different dup instructions are used.
        MJSymbol sym = assignmentExpression.getLeftExpr().mjsymbol;
        if (sym.getKind() == MJSymbol.Var) {
            // Global and local variables do not push anything on the stack so regular dup instruction is fine here.
            // The top of the stack will contain: ... RESULT RESULT (RESULT is expression result)
            MJCode.put(MJCode.dup);
        } else if (sym.getKind() == MJSymbol.Fld) {
            // Class field designator pushes object's address onto the stack so the result must be placed before it.
            // Instruction dup_x1 does exactly that and the top of the stack will contain: ... RESULT ADDRESS RESULT
            MJCode.put(MJCode.dup_x1);
        } else {
            // Array element designator pushes both array's address and element index onto the stack so the result must
            // be placed two places down. This is done with dup_x2 instruction and after execution the top of the stack
            // will contain: ... RESULT ADDRESS INDEX RESULT
            MJCode.put(MJCode.dup_x2);
        }
        // Insert a store instruction to store value on the stack into the left symbol.
        // This instruction will remove everything from the stack except the duplicated result so the top of the stack
        // will contain: ... RESULT
        MJCode.store(sym);
    }

    @Override
    public void visit(MultipleTermsExpression multipleTermsExpression) {
        logDebugNodeVisit(multipleTermsExpression);
        // Depending on operation insert appropriate instruction.
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
        // If expression is negated put a neg instruction.
        if (singleTermExpression.getOptSign() instanceof MinusSign) {
            MJCode.put(MJCode.neg);
        }
    }

    //------------------- Term ---------------------------------------------------------------------------------------//

    @Override
    public void visit(MultipleFactorsTerm multipleFactorsTerm) {
        logDebugNodeVisit(multipleFactorsTerm);
        // Depending on operation insert appropriate instruction.
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
        MJSymbol sym = designatorFactor.mjsymbol;
        // If designator is a class field or an array element and it is on the right side of any assignment operator
        // (excluding '=' because expressions do not support this operator) it must be duplicated in order to preserve
        // the required address (and index for array element) to enable store instruction.
        if (sym.getKind() == MJSymbol.Fld || sym.getKind() == MJSymbol.Elem) {
            if (designatorFactor.getParent() instanceof SingleFactorTerm) {
                SingleFactorTerm sft = (SingleFactorTerm) designatorFactor.getParent();
                if (sft.getParent() instanceof SingleTermExpression) {
                    SingleTermExpression ste = (SingleTermExpression) sft.getParent();
                    if (ste.getParent() instanceof AssignmentExpression) {
                        duplicateDesignator(sym);
                    }
                }
            }
        }
        // In any case we need to load the symbol in order for it to be used in the expression.
        MJCode.load(sym);
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
        if (allocatorFactor.getOptArrayIndexer() instanceof SingleArrayIndexer) {
            // Array allocator
            MJCode.put(MJCode.newarray);
            MJCode.put(sym.getType() == MJTable.charType ? 0 : 1);
        } else {
            // Object allocator
            MJCode.put(MJCode.new_);
            // Object size = number of fields * 4B (size is required in bytes)
            MJCode.put2(sym.getType().getNumberOfFields() * 4);
            // Initialize virtual method table pointer.
            MJCode.put(MJCode.dup);
            MJCode.loadConst(sym.getAdr());
            MJCode.store(sym.getType().getMembersTable().searchKey(MJConstants.VMT_POINTER));
        }
    }

    /******************** Condition ***********************************************************************************/

    @Override
    public void visit(MultipleTermsCondition multipleTermsCondition) {
        logDebugNodeVisit(multipleTermsCondition);
        MJSymbol conditionSym = multipleTermsCondition.getCondition().mjsymbol;
        MJSymbol termSym = multipleTermsCondition.getCondTerm().mjsymbol;
        // Propagate condition term relational operator by creating a new symbol with same adr field
        multipleTermsCondition.mjsymbol = new MJSymbol(MJSymbol.Con, conditionSym.getName() + " || " + termSym.getAdr(),
                MJTable.boolType, termSym.getAdr(), termSym.getLevel());
        // If this is not the last term in a condition, add a true jump
        prepareForNextConditionTermNode(multipleTermsCondition, multipleTermsCondition.mjsymbol.getAdr());
    }

    @Override
    public void visit(SingleTermCondition singleTermCondition) {
        logDebugNodeVisit(singleTermCondition);
        // Propagate condition term relational operator by propagating it's symbol
        singleTermCondition.mjsymbol = singleTermCondition.getCondTerm().mjsymbol;
        // If this is not the last term in a condition, add a true jump
        prepareForNextConditionTermNode(singleTermCondition, singleTermCondition.mjsymbol.getAdr());
    }

    @Override
    public void visit(MultipleFactsConditionTerm multipleFactsConditionTerm) {
        logDebugNodeVisit(multipleFactsConditionTerm);
        MJSymbol termSym = multipleFactsConditionTerm.getCondTerm().mjsymbol;
        MJSymbol factSym = multipleFactsConditionTerm.getCondFact().mjsymbol;
        // Propagate condition fact relational operator by creating a new symbol with same adr field
        multipleFactsConditionTerm.mjsymbol = new MJSymbol(MJSymbol.Con, termSym.getName() + " && " + factSym.getAdr(),
                MJTable.boolType, factSym.getAdr(), factSym.getLevel());
        // If this is not the last factor in a term, add a false jump
        prepareForNextConditionFactNode(multipleFactsConditionTerm, multipleFactsConditionTerm.mjsymbol.getAdr());
    }

    @Override
    public void visit(SingleFactConditionTerm singleFactConditionTerm) {
        logDebugNodeVisit(singleFactConditionTerm);
        // Propagate condition fact relational operator by propagating it's symbol
        singleFactConditionTerm.mjsymbol = singleFactConditionTerm.getCondFact().mjsymbol;
        // If this is not the last factor in a term, add a false jump
        prepareForNextConditionFactNode(singleFactConditionTerm, singleFactConditionTerm.mjsymbol.getAdr());
    }

    @Override
    public void visit(ComplexConditionFact complexConditionFact) {
        logDebugNodeVisit(complexConditionFact);
        String leftName = complexConditionFact.getExpr().mjsymbol.getName();
        String rightName = complexConditionFact.getExpr().mjsymbol.getName();
        // Get relational operator code and name
        Relop relop = complexConditionFact.getRelop();
        int op;
        String opName;
        if (relop instanceof EqOperator) {
            op = MJCode.eq;
            opName = " == ";
        } else if (relop instanceof NeqOperator) {
            op = MJCode.ne;
            opName = " != ";
        } else if (relop instanceof GrtOperator) {
            op = MJCode.gt;
            opName = " > ";
        } else if (relop instanceof GeqOperator) {
            op = MJCode.ge;
            opName = " >= ";
        } else if (relop instanceof LssOperator) {
            op = MJCode.lt;
            opName = " < ";
        } else {
            op = MJCode.le;
            opName = " <= ";
        }
        // Store relational operator code in adr field of this node's mjsymbol
        complexConditionFact.mjsymbol = new MJSymbol(MJSymbol.Con, leftName + opName + rightName,
                MJTable.boolType, op, -1);
    }

    @Override
    public void visit(SimpleConditionFact simpleConditionFact) {
        logDebugNodeVisit(simpleConditionFact);
        // Boolean designator already on stack, load a boolean constant (false = 0) to compare it to
        MJCode.loadConst(0);
        // Store relational operator code in adr field of this node's mjsymbol
        simpleConditionFact.mjsymbol = new MJSymbol(MJSymbol.Con, simpleConditionFact.getExpr().mjsymbol.getName(),
                MJTable.boolType, MJCode.eq, -1);
    }
}