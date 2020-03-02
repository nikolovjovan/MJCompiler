// generated with ast extension for cup
// version 0.8
// 2/2/2020 21:26:27


package rs.ac.bg.etf.pp1.ast;

public class AbstractClassAbstractMethodDeclaration extends AbstractClassMethodDecl {

    private AccessModifier AccessModifier;
    private AbstractClassMethodDecl AbstractClassMethodDecl;

    public AbstractClassAbstractMethodDeclaration (AccessModifier AccessModifier, AbstractClassMethodDecl AbstractClassMethodDecl) {
        this.AccessModifier=AccessModifier;
        if(AccessModifier!=null) AccessModifier.setParent(this);
        this.AbstractClassMethodDecl=AbstractClassMethodDecl;
        if(AbstractClassMethodDecl!=null) AbstractClassMethodDecl.setParent(this);
    }

    public AccessModifier getAccessModifier() {
        return AccessModifier;
    }

    public void setAccessModifier(AccessModifier AccessModifier) {
        this.AccessModifier=AccessModifier;
    }

    public AbstractClassMethodDecl getAbstractClassMethodDecl() {
        return AbstractClassMethodDecl;
    }

    public void setAbstractClassMethodDecl(AbstractClassMethodDecl AbstractClassMethodDecl) {
        this.AbstractClassMethodDecl=AbstractClassMethodDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AccessModifier!=null) AccessModifier.accept(visitor);
        if(AbstractClassMethodDecl!=null) AbstractClassMethodDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AccessModifier!=null) AccessModifier.traverseTopDown(visitor);
        if(AbstractClassMethodDecl!=null) AbstractClassMethodDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AccessModifier!=null) AccessModifier.traverseBottomUp(visitor);
        if(AbstractClassMethodDecl!=null) AbstractClassMethodDecl.traverseBottomUp(visitor);
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

        if(AbstractClassMethodDecl!=null)
            buffer.append(AbstractClassMethodDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AbstractClassAbstractMethodDeclaration]");
        return buffer.toString();
    }
}
