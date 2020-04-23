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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CodeGenerator extends VisitorAdaptor {

    private MJCodeGeneratorLogger logger = new MJCodeGeneratorLogger();

    private int errorCount = 0;

    private boolean generateCode = true;

    private JumpAddressStack jumpAddressStack = new JumpAddressStack(), loopAddressStack = new JumpAddressStack();
    private Stack<Designator> arrayDesignatorStack = new Stack<>();

    private MJSymbol programSym = MJTable.noSym;

    private List<Byte> classVirtualMethodTables = new ArrayList<>();

    /******************** Public methods / constructors ***************************************************************/

    public int getErrorCount() { return errorCount; }

    /******************** Error / debug methods ***********************************************************************/

    private void logDebugNodeVisit(SyntaxNode node) {
        logger.debug(MJUtils.getLineNumber(node), -1, MessageType.NODE_VISIT, node.getClass().getSimpleName());
    }

    // This method is public to allow MJCode to access it and log error messages!
    // Ugly I know but it seems the most reasonable solution while still using base classes and not reimplementing them.
    // Ideally I would never use the ugly code found in provided libraries, but since I must, this is my solution.
    public void logError(SyntaxNode node, Object... context) {
        errorCount++;
        logger.error(MJUtils.getLineNumber(node), -1, context);
    }

    /******************** Helper methods ******************************************************************************/

    private boolean visitStart(SyntaxNode node) {
        // Log node visit debug information
        logDebugNodeVisit(node);
        // Set current node in MJCode to allow it to call logError method.
        MJCode.setCurrentNode(node);
        // Return true if code should be generated
        return generateCode;
    }

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

    private void generateIncDec(MJSymbol designatorSym, boolean increment) {
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

    private void patchJumpAddresses(boolean loopStack, boolean trueJumps) {
        // Get specified jump patch address list
        List<Integer> patchAddressList = loopStack ?
                loopAddressStack.getJumpAddressList(trueJumps) : jumpAddressStack.getJumpAddressList(trueJumps);
        for (Integer patchAddress : patchAddressList) {
            MJCode.fixup(patchAddress);
        }
        // Clear the list since everything has been patched
        patchAddressList.clear();
    }

    private void processForDesignatorStatement(SyntaxNode node) {
        if (node.getParent() instanceof ForStatementHeader) {
            ForStatementHeader header = (ForStatementHeader) node.getParent();
            if (node == header.getForDesignatorStatement()) { // initStatement
                // Store current pc in header start node as it is the start address of condition check
                header.getForStatementHeaderStart().integer = MJCode.pc;
            } else { // updateStatement
                // Re-enable code generation as this node has been visited and no code has been generated
                generateCode = true;
            }
        }
    }

    private void processForCondition(SyntaxNode node) {
        if (node.getParent() instanceof ForStatementHeader) {
            // Disable code generation to prevent updateStatement from being prematurely generated
            generateCode = false;
        }
    }

    private void addWordToStaticData(int value, int address) {
        // Load constant value onto stack
        classVirtualMethodTables.add((byte) MJCode.const_);
        classVirtualMethodTables.add((byte) (value >> 24));
        classVirtualMethodTables.add((byte) (value >> 16));
        classVirtualMethodTables.add((byte) (value >> 8));
        classVirtualMethodTables.add((byte) (value));
        // Store value at specified static memory address
        classVirtualMethodTables.add((byte) MJCode.putstatic);
        classVirtualMethodTables.add((byte) (address >> 8));
        classVirtualMethodTables.add((byte) (address));
    }

    /******************** Program *************************************************************************************/

    @Override
    public void visit(ProgramHeader programHeader) {
        if (!visitStart(programHeader)) return;
        // Set generator in MJCode to allow it to use logError
        MJCode.setGenerator(this);
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
        // Set global program symbol
        programSym = programHeader.mjsymbol;
    }

    @Override
    public void visit(Program program) {
        if (!visitStart(program)) return;
        if (MJCode.pc >= MJConstants.MAX_CODE_SIZE) {
            logError(program, MessageType.INV_PROG_SIZE, MJCode.pc, MJConstants.MAX_CODE_SIZE);
        }
        // Reset global program symbol
        programSym = MJTable.noSym;
    }

    /******************** Global variables ****************************************************************************/

    @Override
    public void visit(Variable variable) {
        if (!visitStart(variable)) return;
        // Set global variable address and increment data size
        MJUtils.findLocalSymbol(programSym, variable.getName()).setAdr(MJCode.dataSize++);
    }

    /******************** Class ***************************************************************************************/

    @Override
    public void visit(ClassHeader classHeader) {
        if (!visitStart(classHeader)) return;
        // Save virtual method table address in class symbol
        classHeader.mjsymbol.setAdr(MJCode.dataSize);
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        if (!visitStart(classDeclaration)) return;
        MJSymbol classSym = classDeclaration.getClassHeader().mjsymbol;
        // Add method names and addresses
        for (MJSymbol memberSym : classSym.getType().getMembersList()) {
            if (memberSym.getKind() == MJSymbol.Meth) {
                // Add name
                String name = memberSym.getName();
                for (int i = 0; i < name.length(); i++) {
                    addWordToStaticData(name.charAt(i), MJCode.dataSize++);
                }
                // Add name terminator
                addWordToStaticData(-1, MJCode.dataSize++);
                // If method is inherited and not overridden update it's address
                if (memberSym.getParent() != classSym) {
                    MJSymbol it = memberSym;
                    while (MJUtils.isSymbolValid(it) && it.getAdr() == 0) {
                        it = MJUtils.findLocalSymbol(it.getParent(), name);
                    }
                    memberSym.setAdr(it.getAdr());
                }
                // Add method address
                addWordToStaticData(memberSym.getAdr(), MJCode.dataSize++);
            }
        }
        // Add table terminator
        addWordToStaticData(-2, MJCode.dataSize++);
    }

    /******************** Method **************************************************************************************/

    @Override
    public void visit(MethodHeader methodHeader) {
        if (!visitStart(methodHeader)) return;
        // Set method address
        methodHeader.mjsymbol.setAdr(MJCode.pc);
        if (methodHeader.getName().equals(MJConstants.MAIN)) {
            // Set main method address
            MJCode.mainPc = MJCode.pc;
            // Generate virtual method table initializer
            for (Byte b : classVirtualMethodTables) {
                MJCode.put(b);
            }
        }
        // Generate the entry
        MJCode.put(MJCode.enter);
        MJCode.put(methodHeader.mjsymbol.getLevel());
        MJCode.put(methodHeader.mjsymbol.getLocalSymbols().size());
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        if (!visitStart(methodDeclaration)) return;
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
        if (!visitStart(identifierDesignator)) return;
        MJSymbol sym = identifierDesignator.mjsymbol;
        if (sym.isReadOnly()) { // Read-only iterator variable for foreach statement
            Designator arrayDesignator = arrayDesignatorStack.peek();
            // Load array address
            arrayDesignator.traverseBottomUp(this);
            MJCode.load(arrayDesignator.mjsymbol);
            // Load array index (this variable will hold index inside the loop)
            MJCode.load(sym);
            // Change this node's mjsymbol to treat this as an array element which it actually is
            identifierDesignator.mjsymbol = new MJSymbol(MJSymbol.Elem, sym.getName(), sym.getType());
            if (identifierDesignator.getParent() instanceof DesignatorFactor) {
                // Change designator factor mjsymbol to this new one
                ((DesignatorFactor) identifierDesignator.getParent()).mjsymbol = identifierDesignator.mjsymbol;
            }
        } else { // Normal identifier
            if (MJUtils.isSymbolValid(sym.getParent()) &&
                    sym.getKind() == MJSymbol.Meth || sym.getKind() == MJSymbol.Fld) { // Class method or field
                // Load 'this' by loading first local method symbol (always the first formal parameter)
                MJCode.put(MJCode.load_n);
            }
            prepareForNextDesignatorNode(identifierDesignator, sym);
        }
    }

    @Override
    public void visit(MemberAccessDesignator memberAccessDesignator) {
        if (!visitStart(memberAccessDesignator)) return;
        prepareForNextDesignatorNode(memberAccessDesignator, memberAccessDesignator.mjsymbol);
    }

    @Override
    public void visit(ElementAccessDesignator elementAccessDesignator) {
        if (!visitStart(elementAccessDesignator)) return;
        prepareForNextDesignatorNode(elementAccessDesignator, elementAccessDesignator.mjsymbol);
    }

    //------------------- DesignatorStatement ------------------------------------------------------------------------//

    @Override
    public void visit(AssignmentHeader assignmentHeader) {
        if (!visitStart(assignmentHeader)) return;
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
        if (!visitStart(assignmentDesignatorStatement)) return;
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
        if (!visitStart(incrementDesignatorStatement)) return;
        generateIncDec(incrementDesignatorStatement.getDesignator().mjsymbol, true);
    }

    @Override
    public void visit(DecrementDesignatorStatement decrementDesignatorStatement) {
        if (!visitStart(decrementDesignatorStatement)) return;
        generateIncDec(decrementDesignatorStatement.getDesignator().mjsymbol, false);
    }

    /******************** Statement ***********************************************************************************/

    @Override
    public void visit(BreakStatement breakStatement) {
        if (!visitStart(breakStatement)) return;
        // Insert an unconditional jump to the address after the loop
        MJCode.putJump(0);
        // Insert patch address as a false jump address (used only for break statement patch addresses)
        loopAddressStack.insertFalseJumpAddress(MJCode.pc - 2);
    }

    @Override
    public void visit(ContinueStatement continueStatement) {
        if (!visitStart(continueStatement)) return;
        // Insert an unconditional jump to the update statement
        MJCode.putJump(0);
        // Insert patch address as a true jump address (used only for continue statement patch addresses)
        loopAddressStack.insertTrueJumpAddress(MJCode.pc - 2);
    }

    @Override
    public void visit(ReadStatement readStatement) {
        if (!visitStart(readStatement)) return;
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
        if (!visitStart(printStatement)) return;
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
        if (!visitStart(returnStatement)) return;
        // Generate exit and return instruction; return value is already on stack
        MJCode.put(MJCode.exit);
        MJCode.put(MJCode.return_);
    }

    @Override
    public void visit(IfStatementHeaderStart ifStatementHeaderStart) {
        if (!visitStart(ifStatementHeaderStart)) return;
        jumpAddressStack.createJumpAddressList();
    }

    @Override
    public void visit(IfStatementHeader ifStatementHeader) {
        if (!visitStart(ifStatementHeader)) return;
        MJSymbol conditionSym = ifStatementHeader.getCondition().mjsymbol;
        // Generate false jump for last relational operation
        MJCode.putFalseJump(conditionSym.getAdr(), 0);
        // Add patch address
        jumpAddressStack.insertFalseJumpAddress(MJCode.pc - 2);
        // Fix all true jumps
        patchJumpAddresses(false, true);
    }

    @Override
    public void visit(ElseStatementStart elseStatementStart) {
        if (!visitStart(elseStatementStart)) return;
        // Place an unconditional jump to skip else statement
        MJCode.putJump(0);
        // Remember jump address to patch
        elseStatementStart.integer = MJCode.pc - 2;
        // Fix all false jumps
        patchJumpAddresses(false, false);
    }

    @Override
    public void visit(IfOptElseStatement ifOptElseStatement) {
        if (!visitStart(ifOptElseStatement)) return;
        if (ifOptElseStatement.getOptElseStatement() instanceof ElseStatement) {
            // Fix unconditional jump
            MJCode.fixup(((ElseStatement) ifOptElseStatement.getOptElseStatement()).getElseStatementStart().integer);
        } else {
            // Fix all false jumps
            patchJumpAddresses(false, false);
        }
        jumpAddressStack.popJumpAddressList();
    }

    @Override
    public void visit(ForStatementHeaderStart forStatementHeaderStart) {
        if (!visitStart(forStatementHeaderStart)) return;
        jumpAddressStack.createJumpAddressList();
        loopAddressStack.createJumpAddressList();
    }

    @Override
    public void visit(ForStatementHeader forStatementHeader) {
        if (!visitStart(forStatementHeader)) return;
        if (forStatementHeader.getForCondition() instanceof SingleCondition) { // SingleCondition
            MJSymbol conditionSym = ((SingleCondition) forStatementHeader.getForCondition()).getCondition().mjsymbol;
            // Generate false jump for last relational operation
            MJCode.putFalseJump(conditionSym.getAdr(), 0);
            // Add patch address
            loopAddressStack.insertFalseJumpAddress(MJCode.pc - 2);
            // Fix all true jumps
            patchJumpAddresses(false, true);
        }
    }

    @Override
    public void visit(SingleDesignatorStatement singleDesignatorStatement) {
        visitStart(singleDesignatorStatement);
        processForDesignatorStatement(singleDesignatorStatement);
    }

    @Override
    public void visit(NoDesignatorStatement noDesignatorStatement) {
        visitStart(noDesignatorStatement);
        processForDesignatorStatement(noDesignatorStatement);
    }

    @Override
    public void visit(SingleCondition singleCondition) {
        visitStart(singleCondition);
        processForCondition(singleCondition);
    }

    @Override
    public void visit(NoCondition noCondition) {
        visitStart(noCondition);
        processForCondition(noCondition);
    }

    @Override
    public void visit(ForStatement forStatement) {
        if (!visitStart(forStatement)) return;
        ForStatementHeader header = forStatement.getForStatementHeader();
        int conditionCheckAddress = header.getForStatementHeaderStart().integer;
        if (header.getForDesignatorStatement1() instanceof SingleDesignatorStatement) { // SingleDesignatorStatement
            // Patch all continue statements
            patchJumpAddresses(true, true);
            // Generate code for update statement
            ((SingleDesignatorStatement) header.getForDesignatorStatement1()).getDesignatorStatement()
                    .traverseBottomUp(this);
        } else { // NoDesignatorStatement
            // Small optimization - patch all true jumps to condition check address instead of this address because
            //                      there is no update statement so no need to jump twice
            int tmp = MJCode.pc;
            // Change pc to the address of the condition check to patch it
            MJCode.pc = conditionCheckAddress;
            // Patch all continue statements
            patchJumpAddresses(true, true);
            // Change pc back to current address
            MJCode.pc = tmp;
        }
        // Generate an unconditional jump statement to condition check
        MJCode.putJump(conditionCheckAddress);
        // Patch all false jumps (conditions and break statements)
        patchJumpAddresses(false, false); // for conditions
        patchJumpAddresses(true, false); // break statements
        jumpAddressStack.popJumpAddressList();
        loopAddressStack.popJumpAddressList();
    }

    @Override
    public void visit(ForEachStatementHeaderStart forEachStatementHeaderStart) {
        visitStart(forEachStatementHeaderStart);
        jumpAddressStack.createJumpAddressList();
        loopAddressStack.createJumpAddressList();
        // Prevent array designator from generating code
        generateCode = false;
    }

    @Override
    public void visit(ForEachStatementHeader forEachStatementHeader) {
        visitStart(forEachStatementHeader);
        // Re-enable code generation
        generateCode = true;
        MJSymbol iteratorSym = forEachStatementHeader.mjsymbol;
        MJSymbol arraySym = forEachStatementHeader.getDesignator().mjsymbol;
        // Generate for initStatement (it = 0)
        MJCode.loadConst(0);
        MJCode.store(iteratorSym);
        // Remember condition check address
        forEachStatementHeader.getForEachStatementHeaderStart().integer = MJCode.pc;
        // Add condition check (it < len(arr))
        MJCode.load(iteratorSym);
        // Generate required array designator parts
        forEachStatementHeader.getDesignator().traverseBottomUp(this);
        // Load array address
        MJCode.load(arraySym);
        MJCode.put(MJCode.arraylength);
        MJCode.putFalseJump(MJCode.lt, 0);
        // Add patch address (will jump to last part of foreach loop where iterator will get current value)
        loopAddressStack.insertFalseJumpAddress(MJCode.pc - 2);
        // Set iterator variable to read-only inside foreach statement
        iteratorSym.setReadOnly(true);
        // Add array designator node to stack (used to generate array address load)
        arrayDesignatorStack.push(forEachStatementHeader.getDesignator());
    }

    @Override
    public void visit(ForEachStatement forEachStatement) {
        if (!visitStart(forEachStatement)) return;
        ForEachStatementHeader header = forEachStatement.getForEachStatementHeader();
        MJSymbol iteratorSym = header.mjsymbol;
        MJSymbol arraySym = header.getDesignator().mjsymbol;
        // Patch all true jumps (continue statements)
        patchJumpAddresses(true, true);
        // Generate for updateStatement (it++)
        generateIncDec(iteratorSym, true);
        // Generate an unconditional jump statement to condition check
        MJCode.putJump(header.getForEachStatementHeaderStart().integer);
        // Patch all false jumps (for condition false jump and break statements)
        patchJumpAddresses(false, false); // conditions
        patchJumpAddresses(true, false); // break statements
        // Load current element value into iterator variable...
        // - Load array address and index assuming it is in range
        MJCode.load(arraySym);
        MJCode.load(iteratorSym);
        // - Check if index is outside the range
        MJCode.load(iteratorSym);
        MJCode.load(arraySym);
        MJCode.put(MJCode.arraylength);
        MJCode.putTrueJump(MJCode.lt, 0);
        int patchAddress = MJCode.pc - 2;
        // - If it is not decrement it by one (since it cannot be greater than len(arr) only equal to)
        MJCode.loadConst(1);
        MJCode.put(MJCode.sub);
        // - Fix jump address
        MJCode.fixup(patchAddress);
        // - Load element value
        MJCode.load(new MJSymbol(MJSymbol.Elem, null, arraySym.getType().getElemType()));
        // - Store into iterator variable
        MJCode.store(iteratorSym);
        // Now iterator variable has the value of current element when loop was exited
        jumpAddressStack.popJumpAddressList();
        loopAddressStack.popJumpAddressList();
        // Set iterator symbol back to read-write as we exited foreach statement
        iteratorSym.setReadOnly(false);
        // Pop designator node
        arrayDesignatorStack.pop();
    }

    /******************** Method call *********************************************************************************/

    @Override
    public void visit(MethodCall methodCall) {
        if (!visitStart(methodCall)) return;
        MJSymbol methodSym = methodCall.mjsymbol;
        if (MJUtils.isSymbolValid(methodSym.getParent())) { // Class method
            // Generate code for object address loading again (this time after all actual parameters)
            methodCall.getMethodCallHeader().getDesignator().traverseBottomUp(this);
            // Load virtual method table pointer
            MJCode.put(MJCode.getfield);
            MJCode.put2(0);
            // Generate method call instruction
            MJCode.put(MJCode.invokevirtual);
            String methodName = methodSym.getName();
            for (int i = 0; i < methodName.length(); i++) {
                MJCode.put4(methodName.charAt(i));
            }
            MJCode.put4(-1);
        } else { // Global method
            MJCode.putCall(methodSym.getAdr());
        }
        if (methodSym.getType() != MJTable.voidType && methodCall.getParent() instanceof MethodCallDesignatorStatement) {
            // MethodCallDesignatorStatement does not need the return value so pop it of the expression stack
            // MethodCallFactor does need the return value so that is why there is a parent check
            MJCode.put(MJCode.pop);
        }
    }

    /******************** Expression **********************************************************************************/

    //------------------- Expression ---------------------------------------------------------------------------------//

    @Override
    public void visit(AssignmentExpression assignmentExpression) {
        if (!visitStart(assignmentExpression)) return;
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
        if (!visitStart(multipleTermsExpression)) return;
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
        if (!visitStart(singleTermExpression)) return;
        // If expression is negated put a neg instruction.
        if (singleTermExpression.getOptSign() instanceof MinusSign) {
            MJCode.put(MJCode.neg);
        }
    }

    //------------------- Term ---------------------------------------------------------------------------------------//

    @Override
    public void visit(MultipleFactorsTerm multipleFactorsTerm) {
        if (!visitStart(multipleFactorsTerm)) return;
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
        if (!visitStart(designatorFactor)) return;
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
        if (!visitStart(constantFactor)) return;
        MJCode.load(constantFactor.mjsymbol);
    }

    @Override
    public void visit(AllocatorFactor allocatorFactor) {
        if (!visitStart(allocatorFactor)) return;
        MJSymbol sym = allocatorFactor.getType().mjsymbol;
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
        if (!visitStart(multipleTermsCondition)) return;
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
        if (!visitStart(singleTermCondition)) return;
        // Propagate condition term relational operator by propagating it's symbol
        singleTermCondition.mjsymbol = singleTermCondition.getCondTerm().mjsymbol;
        // If this is not the last term in a condition, add a true jump
        prepareForNextConditionTermNode(singleTermCondition, singleTermCondition.mjsymbol.getAdr());
    }

    @Override
    public void visit(MultipleFactsConditionTerm multipleFactsConditionTerm) {
        if (!visitStart(multipleFactsConditionTerm)) return;
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
        if (!visitStart(singleFactConditionTerm)) return;
        // Propagate condition fact relational operator by propagating it's symbol
        singleFactConditionTerm.mjsymbol = singleFactConditionTerm.getCondFact().mjsymbol;
        // If this is not the last factor in a term, add a false jump
        prepareForNextConditionFactNode(singleFactConditionTerm, singleFactConditionTerm.mjsymbol.getAdr());
    }

    @Override
    public void visit(ComplexConditionFact complexConditionFact) {
        if (!visitStart(complexConditionFact)) return;
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
        if (!visitStart(simpleConditionFact)) return;
        // Boolean designator already on stack, load a boolean constant (false = 0) to compare it to
        MJCode.loadConst(0);
        // Store relational operator code in adr field of this node's mjsymbol
        simpleConditionFact.mjsymbol = new MJSymbol(MJSymbol.Con, simpleConditionFact.getExpr().mjsymbol.getName(),
                MJTable.boolType, MJCode.ne, -1);
    }
}