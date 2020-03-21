// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class ForStatement extends Statement {

    private OptDesignatorStatement OptDesignatorStatement;
    private ForCondition ForCondition;
    private OptDesignatorStatement OptDesignatorStatement1;
    private Statement Statement;

    public ForStatement (OptDesignatorStatement OptDesignatorStatement, ForCondition ForCondition, OptDesignatorStatement OptDesignatorStatement1, Statement Statement) {
        this.OptDesignatorStatement=OptDesignatorStatement;
        if(OptDesignatorStatement!=null) OptDesignatorStatement.setParent(this);
        this.ForCondition=ForCondition;
        if(ForCondition!=null) ForCondition.setParent(this);
        this.OptDesignatorStatement1=OptDesignatorStatement1;
        if(OptDesignatorStatement1!=null) OptDesignatorStatement1.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
    }

    public OptDesignatorStatement getOptDesignatorStatement() {
        return OptDesignatorStatement;
    }

    public void setOptDesignatorStatement(OptDesignatorStatement OptDesignatorStatement) {
        this.OptDesignatorStatement=OptDesignatorStatement;
    }

    public ForCondition getForCondition() {
        return ForCondition;
    }

    public void setForCondition(ForCondition ForCondition) {
        this.ForCondition=ForCondition;
    }

    public OptDesignatorStatement getOptDesignatorStatement1() {
        return OptDesignatorStatement1;
    }

    public void setOptDesignatorStatement1(OptDesignatorStatement OptDesignatorStatement1) {
        this.OptDesignatorStatement1=OptDesignatorStatement1;
    }

    public Statement getStatement() {
        return Statement;
    }

    public void setStatement(Statement Statement) {
        this.Statement=Statement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptDesignatorStatement!=null) OptDesignatorStatement.accept(visitor);
        if(ForCondition!=null) ForCondition.accept(visitor);
        if(OptDesignatorStatement1!=null) OptDesignatorStatement1.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptDesignatorStatement!=null) OptDesignatorStatement.traverseTopDown(visitor);
        if(ForCondition!=null) ForCondition.traverseTopDown(visitor);
        if(OptDesignatorStatement1!=null) OptDesignatorStatement1.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptDesignatorStatement!=null) OptDesignatorStatement.traverseBottomUp(visitor);
        if(ForCondition!=null) ForCondition.traverseBottomUp(visitor);
        if(OptDesignatorStatement1!=null) OptDesignatorStatement1.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ForStatement(\n");

        if(OptDesignatorStatement!=null)
            buffer.append(OptDesignatorStatement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForCondition!=null)
            buffer.append(ForCondition.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptDesignatorStatement1!=null)
            buffer.append(OptDesignatorStatement1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ForStatement]");
        return buffer.toString();
    }
}
