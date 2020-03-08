// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class LocalVariable extends LocalVar {

    private String varName;
    private OptArrayBrackets OptArrayBrackets;

    public LocalVariable (String varName, OptArrayBrackets OptArrayBrackets) {
        this.varName=varName;
        this.OptArrayBrackets=OptArrayBrackets;
        if(OptArrayBrackets!=null) OptArrayBrackets.setParent(this);
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName=varName;
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
        buffer.append("LocalVariable(\n");

        buffer.append(" "+tab+varName);
        buffer.append("\n");

        if(OptArrayBrackets!=null)
            buffer.append(OptArrayBrackets.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [LocalVariable]");
        return buffer.toString();
    }
}
