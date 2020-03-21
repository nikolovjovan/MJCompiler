// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class AbstractClassMethodDeclaration extends AbstractClassMethodDecl {

    private AccessModifier AccessModifier;
    private MethodDecl MethodDecl;

    public AbstractClassMethodDeclaration (AccessModifier AccessModifier, MethodDecl MethodDecl) {
        this.AccessModifier=AccessModifier;
        if(AccessModifier!=null) AccessModifier.setParent(this);
        this.MethodDecl=MethodDecl;
        if(MethodDecl!=null) MethodDecl.setParent(this);
    }

    public AccessModifier getAccessModifier() {
        return AccessModifier;
    }

    public void setAccessModifier(AccessModifier AccessModifier) {
        this.AccessModifier=AccessModifier;
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
        if(AccessModifier!=null) AccessModifier.accept(visitor);
        if(MethodDecl!=null) MethodDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AccessModifier!=null) AccessModifier.traverseTopDown(visitor);
        if(MethodDecl!=null) MethodDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AccessModifier!=null) AccessModifier.traverseBottomUp(visitor);
        if(MethodDecl!=null) MethodDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AbstractClassMethodDeclaration(\n");

        if(AccessModifier!=null)
            buffer.append(AccessModifier.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(MethodDecl!=null)
            buffer.append(MethodDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AbstractClassMethodDeclaration]");
        return buffer.toString();
    }
}
