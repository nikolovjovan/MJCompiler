// generated with ast extension for cup
// version 0.8
// 7/2/2020 13:23:6


package rs.ac.bg.etf.pp1.ast;

public class NoFormalParameters extends OptFormalParamList {

    public NoFormalParameters () {
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
        buffer.append("NoFormalParameters(\n");

        buffer.append(tab);
        buffer.append(") [NoFormalParameters]");
        return buffer.toString();
    }
}
