// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class ClassVariable extends ClassVar {

    private String name;
    private OptArrayBrackets OptArrayBrackets;

    public ClassVariable (String name, OptArrayBrackets OptArrayBrackets) {
        this.name=name;
        this.OptArrayBrackets=OptArrayBrackets;
        if(OptArrayBrackets!=null) OptArrayBrackets.setParent(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public OptArrayBrackets getOptArrayBrackets() {
        return OptArrayBrackets;
    }

    public void setOptArrayBrackets(OptArrayBrackets OptArrayBrackets) {
        this.OptArrayBrackets=OptArrayBrackets;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptArrayBrackets!=null) OptArrayBrackets.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptArrayBrackets!=null) OptArrayBrackets.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptArrayBrackets!=null) OptArrayBrackets.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassVariable(\n");

        buffer.append(" "+tab+name);
        buffer.append("\n");

        if(OptArrayBrackets!=null)
            buffer.append(OptArrayBrackets.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassVariable]");
        return buffer.toString();
    }
}
