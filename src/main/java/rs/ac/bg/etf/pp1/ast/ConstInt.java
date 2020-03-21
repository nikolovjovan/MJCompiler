// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class ConstInt extends Const {

    private OptSign OptSign;
    private Integer value;

    public ConstInt (OptSign OptSign, Integer value) {
        this.OptSign=OptSign;
        if(OptSign!=null) OptSign.setParent(this);
        this.value=value;
    }

    public OptSign getOptSign() {
        return OptSign;
    }

    public void setOptSign(OptSign OptSign) {
        this.OptSign=OptSign;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value=value;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptSign!=null) OptSign.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptSign!=null) OptSign.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptSign!=null) OptSign.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstInt(\n");

        if(OptSign!=null)
            buffer.append(OptSign.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+value);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstInt]");
        return buffer.toString();
    }
}
