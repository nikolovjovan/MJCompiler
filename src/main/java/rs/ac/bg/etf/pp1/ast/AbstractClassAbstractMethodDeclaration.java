// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class AbstractClassAbstractMethodDeclaration extends AbstractClassMethodDecl {

    private AccessModifier AccessModifier;
    private AbstractMethodDecl AbstractMethodDecl;

    public AbstractClassAbstractMethodDeclaration (AccessModifier AccessModifier, AbstractMethodDecl AbstractMethodDecl) {
        this.AccessModifier=AccessModifier;
        if(AccessModifier!=null) AccessModifier.setParent(this);
        this.AbstractMethodDecl=AbstractMethodDecl;
        if(AbstractMethodDecl!=null) AbstractMethodDecl.setParent(this);
    }

    public AccessModifier getAccessModifier() {
        return AccessModifier;
    }

    public void setAccessModifier(AccessModifier AccessModifier) {
        this.AccessModifier=AccessModifier;
    }

    public AbstractMethodDecl getAbstractMethodDecl() {
        return AbstractMethodDecl;
    }

    public void setAbstractMethodDecl(AbstractMethodDecl AbstractMethodDecl) {
        this.AbstractMethodDecl=AbstractMethodDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AccessModifier!=null) AccessModifier.accept(visitor);
        if(AbstractMethodDecl!=null) AbstractMethodDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AccessModifier!=null) AccessModifier.traverseTopDown(visitor);
        if(AbstractMethodDecl!=null) AbstractMethodDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AccessModifier!=null) AccessModifier.traverseBottomUp(visitor);
        if(AbstractMethodDecl!=null) AbstractMethodDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AbstractClassAbstractMethodDeclaration(\n");

        if(AccessModifier!=null)
            buffer.append(AccessModifier.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AbstractMethodDecl!=null)
            buffer.append(AbstractMethodDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AbstractClassAbstractMethodDeclaration]");
        return buffer.toString();
    }
}
