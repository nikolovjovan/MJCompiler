// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class ClassHeader implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private String name;
    private OptClassBaseType OptClassBaseType;

    public ClassHeader (String name, OptClassBaseType OptClassBaseType) {
        this.name=name;
        this.OptClassBaseType=OptClassBaseType;
        if(OptClassBaseType!=null) OptClassBaseType.setParent(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public OptClassBaseType getOptClassBaseType() {
        return OptClassBaseType;
    }

    public void setOptClassBaseType(OptClassBaseType OptClassBaseType) {
        this.OptClassBaseType=OptClassBaseType;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptClassBaseType!=null) OptClassBaseType.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptClassBaseType!=null) OptClassBaseType.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptClassBaseType!=null) OptClassBaseType.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassHeader(\n");

        buffer.append(" "+tab+name);
        buffer.append("\n");

        if(OptClassBaseType!=null)
            buffer.append(OptClassBaseType.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassHeader]");
        return buffer.toString();
    }
}
