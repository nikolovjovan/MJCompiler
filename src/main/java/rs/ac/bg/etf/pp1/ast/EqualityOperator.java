// generated with ast extension for cup
// version 0.8
// 2/2/2020 21:26:27


package rs.ac.bg.etf.pp1.ast;

public class EqualityOperator extends Relop {

    public EqualityOperator () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("EqualityOperator(\n");

        buffer.append(tab);
        buffer.append(") [EqualityOperator]");
        return buffer.toString();
    }
}
