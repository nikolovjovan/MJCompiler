// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class Designator implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private String name;
    private OptDesignatorPartList OptDesignatorPartList;

    public Designator (String name, OptDesignatorPartList OptDesignatorPartList) {
        this.name=name;
        this.OptDesignatorPartList=OptDesignatorPartList;
        if(OptDesignatorPartList!=null) OptDesignatorPartList.setParent(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
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

        buffer.append(" "+tab+name);
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
