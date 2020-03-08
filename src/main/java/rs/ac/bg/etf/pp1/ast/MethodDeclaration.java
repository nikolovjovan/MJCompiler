// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class MethodDeclaration extends MethodDecl {

    private RetType RetType;
    private String methodName;
    private OptFormalParamList OptFormalParamList;
    private OptLocalVarDeclList OptLocalVarDeclList;
    private OptStatementList OptStatementList;

    public MethodDeclaration (RetType RetType, String methodName, OptFormalParamList OptFormalParamList, OptLocalVarDeclList OptLocalVarDeclList, OptStatementList OptStatementList) {
        this.RetType=RetType;
        if(RetType!=null) RetType.setParent(this);
        this.methodName=methodName;
        this.OptFormalParamList=OptFormalParamList;
        if(OptFormalParamList!=null) OptFormalParamList.setParent(this);
        this.OptLocalVarDeclList=OptLocalVarDeclList;
        if(OptLocalVarDeclList!=null) OptLocalVarDeclList.setParent(this);
        this.OptStatementList=OptStatementList;
        if(OptStatementList!=null) OptStatementList.setParent(this);
    }

    public RetType getRetType() {
        return RetType;
    }

    public void setRetType(RetType RetType) {
        this.RetType=RetType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName=methodName;
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
        if(RetType!=null) RetType.accept(visitor);
        if(OptFormalParamList!=null) OptFormalParamList.accept(visitor);
        if(OptLocalVarDeclList!=null) OptLocalVarDeclList.accept(visitor);
        if(OptStatementList!=null) OptStatementList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(RetType!=null) RetType.traverseTopDown(visitor);
        if(OptFormalParamList!=null) OptFormalParamList.traverseTopDown(visitor);
        if(OptLocalVarDeclList!=null) OptLocalVarDeclList.traverseTopDown(visitor);
        if(OptStatementList!=null) OptStatementList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(RetType!=null) RetType.traverseBottomUp(visitor);
        if(OptFormalParamList!=null) OptFormalParamList.traverseBottomUp(visitor);
        if(OptLocalVarDeclList!=null) OptLocalVarDeclList.traverseBottomUp(visitor);
        if(OptStatementList!=null) OptStatementList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodDeclaration(\n");

        if(RetType!=null)
            buffer.append(RetType.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+methodName);
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
