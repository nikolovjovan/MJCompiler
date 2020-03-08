// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class AddAssignOperator extends Assignop {

    private AddopRight AddopRight;

    public AddAssignOperator (AddopRight AddopRight) {
        this.AddopRight=AddopRight;
        if(AddopRight!=null) AddopRight.setParent(this);
    }

    public AddopRight getAddopRight() {
        return AddopRight;
    }

    public void setAddopRight(AddopRight AddopRight) {
        this.AddopRight=AddopRight;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AddopRight!=null) AddopRight.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AddopRight!=null) AddopRight.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AddopRight!=null) AddopRight.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AddAssignOperator(\n");

        if(AddopRight!=null)
            buffer.append(AddopRight.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AddAssignOperator]");
        return buffer.toString();
    }
}
