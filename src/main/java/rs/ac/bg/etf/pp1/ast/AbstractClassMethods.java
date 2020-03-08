// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class AbstractClassMethods extends OptAbstractClassMethodDeclList {

    private AbstractClassMethodDeclList AbstractClassMethodDeclList;

    public AbstractClassMethods (AbstractClassMethodDeclList AbstractClassMethodDeclList) {
        this.AbstractClassMethodDeclList=AbstractClassMethodDeclList;
        if(AbstractClassMethodDeclList!=null) AbstractClassMethodDeclList.setParent(this);
    }

    public AbstractClassMethodDeclList getAbstractClassMethodDeclList() {
        return AbstractClassMethodDeclList;
    }

    public void setAbstractClassMethodDeclList(AbstractClassMethodDeclList AbstractClassMethodDeclList) {
        this.AbstractClassMethodDeclList=AbstractClassMethodDeclList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AbstractClassMethodDeclList!=null) AbstractClassMethodDeclList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AbstractClassMethodDeclList!=null) AbstractClassMethodDeclList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AbstractClassMethodDeclList!=null) AbstractClassMethodDeclList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AbstractClassMethods(\n");

        if(AbstractClassMethodDeclList!=null)
            buffer.append(AbstractClassMethodDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AbstractClassMethods]");
        return buffer.toString();
    }
}
