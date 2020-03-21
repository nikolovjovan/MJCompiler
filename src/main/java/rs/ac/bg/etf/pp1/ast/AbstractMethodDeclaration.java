// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class AbstractMethodDeclaration extends AbstractMethodDecl {

    private AbstractMethodHeader AbstractMethodHeader;
    private OptFormalParamList OptFormalParamList;

    public AbstractMethodDeclaration (AbstractMethodHeader AbstractMethodHeader, OptFormalParamList OptFormalParamList) {
        this.AbstractMethodHeader=AbstractMethodHeader;
        if(AbstractMethodHeader!=null) AbstractMethodHeader.setParent(this);
        this.OptFormalParamList=OptFormalParamList;
        if(OptFormalParamList!=null) OptFormalParamList.setParent(this);
    }

    public AbstractMethodHeader getAbstractMethodHeader() {
        return AbstractMethodHeader;
    }

    public void setAbstractMethodHeader(AbstractMethodHeader AbstractMethodHeader) {
        this.AbstractMethodHeader=AbstractMethodHeader;
    }

    public OptFormalParamList getOptFormalParamList() {
        return OptFormalParamList;
    }

    public void setOptFormalParamList(OptFormalParamList OptFormalParamList) {
        this.OptFormalParamList=OptFormalParamList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AbstractMethodHeader!=null) AbstractMethodHeader.accept(visitor);
        if(OptFormalParamList!=null) OptFormalParamList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AbstractMethodHeader!=null) AbstractMethodHeader.traverseTopDown(visitor);
        if(OptFormalParamList!=null) OptFormalParamList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AbstractMethodHeader!=null) AbstractMethodHeader.traverseBottomUp(visitor);
        if(OptFormalParamList!=null) OptFormalParamList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AbstractMethodDeclaration(\n");

        if(AbstractMethodHeader!=null)
            buffer.append(AbstractMethodHeader.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptFormalParamList!=null)
            buffer.append(OptFormalParamList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AbstractMethodDeclaration]");
        return buffer.toString();
    }
}
