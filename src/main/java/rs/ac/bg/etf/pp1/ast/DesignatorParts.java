// generated with ast extension for cup
// version 0.8
// 2/2/2020 21:26:27


package rs.ac.bg.etf.pp1.ast;

public class DesignatorParts extends OptDesignatorPartList {

    private OptDesignatorPartList OptDesignatorPartList;
    private DesignatorPart DesignatorPart;

    public DesignatorParts (OptDesignatorPartList OptDesignatorPartList, DesignatorPart DesignatorPart) {
        this.OptDesignatorPartList=OptDesignatorPartList;
        if(OptDesignatorPartList!=null) OptDesignatorPartList.setParent(this);
        this.DesignatorPart=DesignatorPart;
        if(DesignatorPart!=null) DesignatorPart.setParent(this);
    }

    public OptDesignatorPartList getOptDesignatorPartList() {
        return OptDesignatorPartList;
    }

    public void setOptDesignatorPartList(OptDesignatorPartList OptDesignatorPartList) {
        this.OptDesignatorPartList=OptDesignatorPartList;
    }

    public DesignatorPart getDesignatorPart() {
        return DesignatorPart;
    }

    public void setDesignatorPart(DesignatorPart DesignatorPart) {
        this.DesignatorPart=DesignatorPart;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptDesignatorPartList!=null) OptDesignatorPartList.accept(visitor);
        if(DesignatorPart!=null) DesignatorPart.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptDesignatorPartList!=null) OptDesignatorPartList.traverseTopDown(visitor);
        if(DesignatorPart!=null) DesignatorPart.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptDesignatorPartList!=null) OptDesignatorPartList.traverseBottomUp(visitor);
        if(DesignatorPart!=null) DesignatorPart.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesignatorParts(\n");

        if(OptDesignatorPartList!=null)
            buffer.append(OptDesignatorPartList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(DesignatorPart!=null)
            buffer.append(DesignatorPart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesignatorParts]");
        return buffer.toString();
    }
}
