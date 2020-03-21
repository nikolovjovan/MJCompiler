// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class AbstractClassDeclaration extends AbstractClassDecl {

    private AbstractClassHeader AbstractClassHeader;
    private OptClassVarDeclList OptClassVarDeclList;
    private OptAbstractClassMethodDeclList OptAbstractClassMethodDeclList;

    public AbstractClassDeclaration (AbstractClassHeader AbstractClassHeader, OptClassVarDeclList OptClassVarDeclList, OptAbstractClassMethodDeclList OptAbstractClassMethodDeclList) {
        this.AbstractClassHeader=AbstractClassHeader;
        if(AbstractClassHeader!=null) AbstractClassHeader.setParent(this);
        this.OptClassVarDeclList=OptClassVarDeclList;
        if(OptClassVarDeclList!=null) OptClassVarDeclList.setParent(this);
        this.OptAbstractClassMethodDeclList=OptAbstractClassMethodDeclList;
        if(OptAbstractClassMethodDeclList!=null) OptAbstractClassMethodDeclList.setParent(this);
    }

    public AbstractClassHeader getAbstractClassHeader() {
        return AbstractClassHeader;
    }

    public void setAbstractClassHeader(AbstractClassHeader AbstractClassHeader) {
        this.AbstractClassHeader=AbstractClassHeader;
    }

    public OptClassVarDeclList getOptClassVarDeclList() {
        return OptClassVarDeclList;
    }

    public void setOptClassVarDeclList(OptClassVarDeclList OptClassVarDeclList) {
        this.OptClassVarDeclList=OptClassVarDeclList;
    }

    public OptAbstractClassMethodDeclList getOptAbstractClassMethodDeclList() {
        return OptAbstractClassMethodDeclList;
    }

    public void setOptAbstractClassMethodDeclList(OptAbstractClassMethodDeclList OptAbstractClassMethodDeclList) {
        this.OptAbstractClassMethodDeclList=OptAbstractClassMethodDeclList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AbstractClassHeader!=null) AbstractClassHeader.accept(visitor);
        if(OptClassVarDeclList!=null) OptClassVarDeclList.accept(visitor);
        if(OptAbstractClassMethodDeclList!=null) OptAbstractClassMethodDeclList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AbstractClassHeader!=null) AbstractClassHeader.traverseTopDown(visitor);
        if(OptClassVarDeclList!=null) OptClassVarDeclList.traverseTopDown(visitor);
        if(OptAbstractClassMethodDeclList!=null) OptAbstractClassMethodDeclList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AbstractClassHeader!=null) AbstractClassHeader.traverseBottomUp(visitor);
        if(OptClassVarDeclList!=null) OptClassVarDeclList.traverseBottomUp(visitor);
        if(OptAbstractClassMethodDeclList!=null) OptAbstractClassMethodDeclList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AbstractClassDeclaration(\n");

        if(AbstractClassHeader!=null)
            buffer.append(AbstractClassHeader.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptClassVarDeclList!=null)
            buffer.append(OptClassVarDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptAbstractClassMethodDeclList!=null)
            buffer.append(OptAbstractClassMethodDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AbstractClassDeclaration]");
        return buffer.toString();
    }
}
