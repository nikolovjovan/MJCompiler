// generated with ast extension for cup
// version 0.8
// 7/2/2020 13:23:6


package rs.ac.bg.etf.pp1.ast;

public class MethodDeclarations extends OptMethodDeclList {

    private OptMethodDeclList OptMethodDeclList;
    private MethodDecl MethodDecl;

    public MethodDeclarations (OptMethodDeclList OptMethodDeclList, MethodDecl MethodDecl) {
        this.OptMethodDeclList=OptMethodDeclList;
        if(OptMethodDeclList!=null) OptMethodDeclList.setParent(this);
        this.MethodDecl=MethodDecl;
        if(MethodDecl!=null) MethodDecl.setParent(this);
    }

    public OptMethodDeclList getOptMethodDeclList() {
        return OptMethodDeclList;
    }

    public void setOptMethodDeclList(OptMethodDeclList OptMethodDeclList) {
        this.OptMethodDeclList=OptMethodDeclList;
    }

    public MethodDecl getMethodDecl() {
        return MethodDecl;
    }

    public void setMethodDecl(MethodDecl MethodDecl) {
        this.MethodDecl=MethodDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptMethodDeclList!=null) OptMethodDeclList.accept(visitor);
        if(MethodDecl!=null) MethodDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptMethodDeclList!=null) OptMethodDeclList.traverseTopDown(visitor);
        if(MethodDecl!=null) MethodDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptMethodDeclList!=null) OptMethodDeclList.traverseBottomUp(visitor);
        if(MethodDecl!=null) MethodDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodDeclarations(\n");

        if(OptMethodDeclList!=null)
            buffer.append(OptMethodDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(MethodDecl!=null)
            buffer.append(MethodDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MethodDeclarations]");
        return buffer.toString();
    }
}
