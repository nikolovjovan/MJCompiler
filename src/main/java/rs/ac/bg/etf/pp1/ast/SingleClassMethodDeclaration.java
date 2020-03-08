// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class SingleClassMethodDeclaration extends ClassMethodDeclList {

    private ClassMethodDecl ClassMethodDecl;

    public SingleClassMethodDeclaration (ClassMethodDecl ClassMethodDecl) {
        this.ClassMethodDecl=ClassMethodDecl;
        if(ClassMethodDecl!=null) ClassMethodDecl.setParent(this);
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
        if(ClassMethodDecl!=null) ClassMethodDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ClassMethodDecl!=null) ClassMethodDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ClassMethodDecl!=null) ClassMethodDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleClassMethodDeclaration(\n");

        if(ClassMethodDecl!=null)
            buffer.append(ClassMethodDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleClassMethodDeclaration]");
        return buffer.toString();
    }
}
