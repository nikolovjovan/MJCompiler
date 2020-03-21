// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class MultipleAbstractClassMethods extends AbstractClassMethodDeclList {

    private AbstractClassMethodDeclList AbstractClassMethodDeclList;
    private AbstractClassMethodDecl AbstractClassMethodDecl;

    public MultipleAbstractClassMethods (AbstractClassMethodDeclList AbstractClassMethodDeclList, AbstractClassMethodDecl AbstractClassMethodDecl) {
        this.AbstractClassMethodDeclList=AbstractClassMethodDeclList;
        if(AbstractClassMethodDeclList!=null) AbstractClassMethodDeclList.setParent(this);
        this.AbstractClassMethodDecl=AbstractClassMethodDecl;
        if(AbstractClassMethodDecl!=null) AbstractClassMethodDecl.setParent(this);
    }

    public AbstractClassMethodDeclList getAbstractClassMethodDeclList() {
        return AbstractClassMethodDeclList;
    }

    public void setAbstractClassMethodDeclList(AbstractClassMethodDeclList AbstractClassMethodDeclList) {
        this.AbstractClassMethodDeclList=AbstractClassMethodDeclList;
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
        if(AbstractClassMethodDeclList!=null) AbstractClassMethodDeclList.accept(visitor);
        if(AbstractClassMethodDecl!=null) AbstractClassMethodDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AbstractClassMethodDeclList!=null) AbstractClassMethodDeclList.traverseTopDown(visitor);
        if(AbstractClassMethodDecl!=null) AbstractClassMethodDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AbstractClassMethodDeclList!=null) AbstractClassMethodDeclList.traverseBottomUp(visitor);
        if(AbstractClassMethodDecl!=null) AbstractClassMethodDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MultipleAbstractClassMethods(\n");

        if(AbstractClassMethodDeclList!=null)
            buffer.append(AbstractClassMethodDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AbstractClassMethodDecl!=null)
            buffer.append(AbstractClassMethodDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MultipleAbstractClassMethods]");
        return buffer.toString();
    }
}
