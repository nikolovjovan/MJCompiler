// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class NoElseStatment extends OptElseStatement {

    public NoElseStatment () {
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
        buffer.append("NoElseStatment(\n");

        buffer.append(tab);
        buffer.append(") [NoElseStatment]");
        return buffer.toString();
    }
}
