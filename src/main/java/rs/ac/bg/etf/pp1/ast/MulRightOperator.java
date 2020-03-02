// generated with ast extension for cup
// version 0.8
// 2/2/2020 21:26:27


package rs.ac.bg.etf.pp1.ast;

public class MulRightOperator extends Mulop {

    private MulopRight MulopRight;

    public MulRightOperator (MulopRight MulopRight) {
        this.MulopRight=MulopRight;
        if(MulopRight!=null) MulopRight.setParent(this);
    }

    public MulopRight getMulopRight() {
        return MulopRight;
    }

    public void setMulopRight(MulopRight MulopRight) {
        this.MulopRight=MulopRight;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MulopRight!=null) MulopRight.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MulopRight!=null) MulopRight.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MulopRight!=null) MulopRight.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MulRightOperator(\n");

        if(MulopRight!=null)
            buffer.append(MulopRight.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MulRightOperator]");
        return buffer.toString();
    }
}
