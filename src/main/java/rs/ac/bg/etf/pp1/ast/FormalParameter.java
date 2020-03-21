// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class FormalParameter extends FormalParam {

    private Type Type;
    private String name;
    private OptArrayBrackets OptArrayBrackets;

    public FormalParameter (Type Type, String name, OptArrayBrackets OptArrayBrackets) {
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.name=name;
        this.OptArrayBrackets=OptArrayBrackets;
        if(OptArrayBrackets!=null) OptArrayBrackets.setParent(this);
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
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
        if(Type!=null) Type.accept(visitor);
        if(OptArrayBrackets!=null) OptArrayBrackets.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(OptArrayBrackets!=null) OptArrayBrackets.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(OptArrayBrackets!=null) OptArrayBrackets.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FormalParameter(\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+name);
        buffer.append("\n");

        if(OptArrayBrackets!=null)
            buffer.append(OptArrayBrackets.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FormalParameter]");
        return buffer.toString();
    }
}
