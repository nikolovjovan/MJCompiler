// generated with ast extension for cup
// version 0.8
// 7/2/2020 13:23:6


package rs.ac.bg.etf.pp1.ast;

public class MultipleActualParameters extends ActualParamList {

    private ActualParamList ActualParamList;
    private ActualParam ActualParam;

    public MultipleActualParameters (ActualParamList ActualParamList, ActualParam ActualParam) {
        this.ActualParamList=ActualParamList;
        if(ActualParamList!=null) ActualParamList.setParent(this);
        this.ActualParam=ActualParam;
        if(ActualParam!=null) ActualParam.setParent(this);
    }

    public ActualParamList getActualParamList() {
        return ActualParamList;
    }

    public void setActualParamList(ActualParamList ActualParamList) {
        this.ActualParamList=ActualParamList;
    }

    public ActualParam getActualParam() {
        return ActualParam;
    }

    public void setActualParam(ActualParam ActualParam) {
        this.ActualParam=ActualParam;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ActualParamList!=null) ActualParamList.accept(visitor);
        if(ActualParam!=null) ActualParam.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ActualParamList!=null) ActualParamList.traverseTopDown(visitor);
        if(ActualParam!=null) ActualParam.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ActualParamList!=null) ActualParamList.traverseBottomUp(visitor);
        if(ActualParam!=null) ActualParam.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MultipleActualParameters(\n");

        if(ActualParamList!=null)
            buffer.append(ActualParamList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ActualParam!=null)
            buffer.append(ActualParam.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MultipleActualParameters]");
        return buffer.toString();
    }
}
