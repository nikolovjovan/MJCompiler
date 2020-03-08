// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class ErrorClassBaseTypeAfterExtends extends OptClassBaseType {

    public ErrorClassBaseTypeAfterExtends () {
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
        buffer.append("ErrorClassBaseTypeAfterExtends(\n");

        buffer.append(tab);
        buffer.append(") [ErrorClassBaseTypeAfterExtends]");
        return buffer.toString();
    }
}
