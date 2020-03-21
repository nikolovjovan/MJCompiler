// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class SingleLocalVariable extends LocalVarList {

    private LocalVar LocalVar;

    public SingleLocalVariable (LocalVar LocalVar) {
        this.LocalVar=LocalVar;
        if(LocalVar!=null) LocalVar.setParent(this);
    }

    public LocalVar getLocalVar() {
        return LocalVar;
    }

    public void setLocalVar(LocalVar LocalVar) {
        this.LocalVar=LocalVar;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(LocalVar!=null) LocalVar.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(LocalVar!=null) LocalVar.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(LocalVar!=null) LocalVar.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleLocalVariable(\n");

        if(LocalVar!=null)
            buffer.append(LocalVar.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleLocalVariable]");
        return buffer.toString();
    }
}
