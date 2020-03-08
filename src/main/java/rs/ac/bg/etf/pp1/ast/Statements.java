// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class Statements extends OptStatementList {

    private OptStatementList OptStatementList;
    private Statement Statement;

    public Statements (OptStatementList OptStatementList, Statement Statement) {
        this.OptStatementList=OptStatementList;
        if(OptStatementList!=null) OptStatementList.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
    }

    public OptStatementList getOptStatementList() {
        return OptStatementList;
    }

    public void setOptStatementList(OptStatementList OptStatementList) {
        this.OptStatementList=OptStatementList;
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
        if(OptStatementList!=null) OptStatementList.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptStatementList!=null) OptStatementList.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptStatementList!=null) OptStatementList.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statements(\n");

        if(OptStatementList!=null)
            buffer.append(OptStatementList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statements]");
        return buffer.toString();
    }
}
