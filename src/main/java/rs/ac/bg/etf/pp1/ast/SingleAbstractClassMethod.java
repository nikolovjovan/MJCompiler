// generated with ast extension for cup
// version 0.8
// 7/2/2020 13:23:6


package rs.ac.bg.etf.pp1.ast;

public class SingleAbstractClassMethod extends AbstractClassMethodDeclList {

    private AbstractClassMethodDecl AbstractClassMethodDecl;

    public SingleAbstractClassMethod (AbstractClassMethodDecl AbstractClassMethodDecl) {
        this.AbstractClassMethodDecl=AbstractClassMethodDecl;
        if(AbstractClassMethodDecl!=null) AbstractClassMethodDecl.setParent(this);
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
        if(AbstractClassMethodDecl!=null) AbstractClassMethodDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AbstractClassMethodDecl!=null) AbstractClassMethodDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AbstractClassMethodDecl!=null) AbstractClassMethodDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleAbstractClassMethod(\n");

        if(AbstractClassMethodDecl!=null)
            buffer.append(AbstractClassMethodDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleAbstractClassMethod]");
        return buffer.toString();
    }
}
