// generated with ast extension for cup
// version 0.8
// 2/2/2020 21:26:27


package rs.ac.bg.etf.pp1.ast;

public class FunctionCallFactor extends Factor {

    private Designator Designator;
    private OptActualParamList OptActualParamList;

    public FunctionCallFactor (Designator Designator, OptActualParamList OptActualParamList) {
        this.Designator=Designator;
        if(Designator!=null) Designator.setParent(this);
        this.OptActualParamList=OptActualParamList;
        if(OptActualParamList!=null) OptActualParamList.setParent(this);
    }

    public Designator getDesignator() {
        return Designator;
    }

    public void setDesignator(Designator Designator) {
        this.Designator=Designator;
    }

    public OptActualParamList getOptActualParamList() {
        return OptActualParamList;
    }

    public void setOptActualParamList(OptActualParamList OptActualParamList) {
        this.OptActualParamList=OptActualParamList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Designator!=null) Designator.accept(visitor);
        if(OptActualParamList!=null) OptActualParamList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Designator!=null) Designator.traverseTopDown(visitor);
        if(OptActualParamList!=null) OptActualParamList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Designator!=null) Designator.traverseBottomUp(visitor);
        if(OptActualParamList!=null) OptActualParamList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FunctionCallFactor(\n");

        if(Designator!=null)
            buffer.append(Designator.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptActualParamList!=null)
            buffer.append(OptActualParamList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FunctionCallFactor]");
        return buffer.toString();
    }
}
