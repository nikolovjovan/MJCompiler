// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class SingleVariable extends VarList {

    private Var Var;

    public SingleVariable (Var Var) {
        this.Var=Var;
        if(Var!=null) Var.setParent(this);
    }

    public Var getVar() {
        return Var;
    }

    public void setVar(Var Var) {
        this.Var=Var;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Var!=null) Var.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Var!=null) Var.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Var!=null) Var.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleVariable(\n");

        if(Var!=null)
            buffer.append(Var.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleVariable]");
        return buffer.toString();
    }
}
