// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class MultipleClassMethodDeclarations extends ClassMethodDeclList {

    private ClassMethodDeclList ClassMethodDeclList;
    private ClassMethodDecl ClassMethodDecl;

    public MultipleClassMethodDeclarations (ClassMethodDeclList ClassMethodDeclList, ClassMethodDecl ClassMethodDecl) {
        this.ClassMethodDeclList=ClassMethodDeclList;
        if(ClassMethodDeclList!=null) ClassMethodDeclList.setParent(this);
        this.ClassMethodDecl=ClassMethodDecl;
        if(ClassMethodDecl!=null) ClassMethodDecl.setParent(this);
    }

    public ClassMethodDeclList getClassMethodDeclList() {
        return ClassMethodDeclList;
    }

    public void setClassMethodDeclList(ClassMethodDeclList ClassMethodDeclList) {
        this.ClassMethodDeclList=ClassMethodDeclList;
    }

    public ClassMethodDecl getClassMethodDecl() {
        return ClassMethodDecl;
    }

    public void setClassMethodDecl(ClassMethodDecl ClassMethodDecl) {
        this.ClassMethodDecl=ClassMethodDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ClassMethodDeclList!=null) ClassMethodDeclList.accept(visitor);
        if(ClassMethodDecl!=null) ClassMethodDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ClassMethodDeclList!=null) ClassMethodDeclList.traverseTopDown(visitor);
        if(ClassMethodDecl!=null) ClassMethodDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ClassMethodDeclList!=null) ClassMethodDeclList.traverseBottomUp(visitor);
        if(ClassMethodDecl!=null) ClassMethodDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MultipleClassMethodDeclarations(\n");

        if(ClassMethodDeclList!=null)
            buffer.append(ClassMethodDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ClassMethodDecl!=null)
            buffer.append(ClassMethodDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MultipleClassMethodDeclarations]");
        return buffer.toString();
    }
}
