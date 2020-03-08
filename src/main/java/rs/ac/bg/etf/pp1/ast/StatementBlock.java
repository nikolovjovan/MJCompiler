// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class StatementBlock extends Statement {

    private OptStatementList OptStatementList;

    public StatementBlock (OptStatementList OptStatementList) {
        this.OptStatementList=OptStatementList;
        if(OptStatementList!=null) OptStatementList.setParent(this);
    }

    public OptStatementList getOptStatementList() {
        return OptStatementList;
    }

    public void setOptStatementList(OptStatementList OptStatementList) {
        this.OptStatementList=OptStatementList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptStatementList!=null) OptStatementList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptStatementList!=null) OptStatementList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptStatementList!=null) OptStatementList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("StatementBlock(\n");

        if(OptStatementList!=null)
            buffer.append(OptStatementList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [StatementBlock]");
        return buffer.toString();
    }
}
