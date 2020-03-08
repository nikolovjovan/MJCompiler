// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class SingleClassVariable extends ClassVarList {

    private ClassVar ClassVar;

    public SingleClassVariable (ClassVar ClassVar) {
        this.ClassVar=ClassVar;
        if(ClassVar!=null) ClassVar.setParent(this);
    }

    public ClassVar getClassVar() {
        return ClassVar;
    }

    public void setClassVar(ClassVar ClassVar) {
        this.ClassVar=ClassVar;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ClassVar!=null) ClassVar.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ClassVar!=null) ClassVar.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ClassVar!=null) ClassVar.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleClassVariable(\n");

        if(ClassVar!=null)
            buffer.append(ClassVar.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleClassVariable]");
        return buffer.toString();
    }
}
