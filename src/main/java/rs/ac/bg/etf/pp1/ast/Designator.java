// generated with ast extension for cup
// version 0.8
// 2/2/2020 21:26:27


package rs.ac.bg.etf.pp1.ast;

public class Designator implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private String I1;
    private OptDesignatorPartList OptDesignatorPartList;

    public Designator (String I1, OptDesignatorPartList OptDesignatorPartList) {
        this.I1=I1;
        this.OptDesignatorPartList=OptDesignatorPartList;
        if(OptDesignatorPartList!=null) OptDesignatorPartList.setParent(this);
    }

    public String getI1() {
        return I1;
    }

    public void setI1(String I1) {
        this.I1=I1;
    }

    public OptDesignatorPartList getOptDesignatorPartList() {
        return OptDesignatorPartList;
    }

    public void setOptDesignatorPartList(OptDesignatorPartList OptDesignatorPartList) {
        this.OptDesignatorPartList=OptDesignatorPartList;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptDesignatorPartList!=null) OptDesignatorPartList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptDesignatorPartList!=null) OptDesignatorPartList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptDesignatorPartList!=null) OptDesignatorPartList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Designator(\n");

        buffer.append(" "+tab+I1);
        buffer.append("\n");

        if(OptDesignatorPartList!=null)
            buffer.append(OptDesignatorPartList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Designator]");
        return buffer.toString();
    }
}
