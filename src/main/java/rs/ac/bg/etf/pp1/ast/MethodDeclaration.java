// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class MethodDeclaration extends MethodDecl {

    private MethodHeader MethodHeader;
    private OptFormalParamList OptFormalParamList;
    private OptLocalVarDeclList OptLocalVarDeclList;
    private OptStatementList OptStatementList;

    public MethodDeclaration (MethodHeader MethodHeader, OptFormalParamList OptFormalParamList, OptLocalVarDeclList OptLocalVarDeclList, OptStatementList OptStatementList) {
        this.MethodHeader=MethodHeader;
        if(MethodHeader!=null) MethodHeader.setParent(this);
        this.OptFormalParamList=OptFormalParamList;
        if(OptFormalParamList!=null) OptFormalParamList.setParent(this);
        this.OptLocalVarDeclList=OptLocalVarDeclList;
        if(OptLocalVarDeclList!=null) OptLocalVarDeclList.setParent(this);
        this.OptStatementList=OptStatementList;
        if(OptStatementList!=null) OptStatementList.setParent(this);
    }

    public MethodHeader getMethodHeader() {
        return MethodHeader;
    }

    public void setMethodHeader(MethodHeader MethodHeader) {
        this.MethodHeader=MethodHeader;
    }

    public OptFormalParamList getOptFormalParamList() {
        return OptFormalParamList;
    }

    public void setOptFormalParamList(OptFormalParamList OptFormalParamList) {
        this.OptFormalParamList=OptFormalParamList;
    }

    public OptLocalVarDeclList getOptLocalVarDeclList() {
        return OptLocalVarDeclList;
    }

    public void setOptLocalVarDeclList(OptLocalVarDeclList OptLocalVarDeclList) {
        this.OptLocalVarDeclList=OptLocalVarDeclList;
    }

    public OptStatementList getOptStatementList() {
        return OptStatementList;
    }

    public void setOptStatementList(OptStatementList OptStatementList) {
        this.OptStatementList=OptStatementList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MethodHeader!=null) MethodHeader.accept(visitor);
        if(OptFormalParamList!=null) OptFormalParamList.accept(visitor);
        if(OptLocalVarDeclList!=null) OptLocalVarDeclList.accept(visitor);
        if(OptStatementList!=null) OptStatementList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MethodHeader!=null) MethodHeader.traverseTopDown(visitor);
        if(OptFormalParamList!=null) OptFormalParamList.traverseTopDown(visitor);
        if(OptLocalVarDeclList!=null) OptLocalVarDeclList.traverseTopDown(visitor);
        if(OptStatementList!=null) OptStatementList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MethodHeader!=null) MethodHeader.traverseBottomUp(visitor);
        if(OptFormalParamList!=null) OptFormalParamList.traverseBottomUp(visitor);
        if(OptLocalVarDeclList!=null) OptLocalVarDeclList.traverseBottomUp(visitor);
        if(OptStatementList!=null) OptStatementList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodDeclaration(\n");

        if(MethodHeader!=null)
            buffer.append(MethodHeader.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptFormalParamList!=null)
            buffer.append(OptFormalParamList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptLocalVarDeclList!=null)
            buffer.append(OptLocalVarDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptStatementList!=null)
            buffer.append(OptStatementList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MethodDeclaration]");
        return buffer.toString();
    }
}
