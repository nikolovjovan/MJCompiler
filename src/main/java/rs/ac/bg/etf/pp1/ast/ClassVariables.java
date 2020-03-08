// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class ClassVariables extends ClassVarList {

    private ClassVarList ClassVarList;
    private ClassVar ClassVar;

    public ClassVariables (ClassVarList ClassVarList, ClassVar ClassVar) {
        this.ClassVarList=ClassVarList;
        if(ClassVarList!=null) ClassVarList.setParent(this);
        this.ClassVar=ClassVar;
        if(ClassVar!=null) ClassVar.setParent(this);
    }

    public ClassVarList getClassVarList() {
        return ClassVarList;
    }

    public void setClassVarList(ClassVarList ClassVarList) {
        this.ClassVarList=ClassVarList;
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
        if(ClassVarList!=null) ClassVarList.accept(visitor);
        if(ClassVar!=null) ClassVar.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ClassVarList!=null) ClassVarList.traverseTopDown(visitor);
        if(ClassVar!=null) ClassVar.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ClassVarList!=null) ClassVarList.traverseBottomUp(visitor);
        if(ClassVar!=null) ClassVar.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassVariables(\n");

        if(ClassVarList!=null)
            buffer.append(ClassVarList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ClassVar!=null)
            buffer.append(ClassVar.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassVariables]");
        return buffer.toString();
    }
}
