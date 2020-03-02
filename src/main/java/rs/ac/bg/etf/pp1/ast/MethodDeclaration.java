// generated with ast extension for cup
// version 0.8
// 2/2/2020 21:26:27


package rs.ac.bg.etf.pp1.ast;

public class MethodDeclaration extends MethodDecl {

    private RetType RetType;
    private String methodName;
    private OptFormalParamList OptFormalParamList;
    private OptVarDeclList OptVarDeclList;
    private OptStatementList OptStatementList;

    public MethodDeclaration (RetType RetType, String methodName, OptFormalParamList OptFormalParamList, OptVarDeclList OptVarDeclList, OptStatementList OptStatementList) {
        this.RetType=RetType;
        if(RetType!=null) RetType.setParent(this);
        this.methodName=methodName;
        this.OptFormalParamList=OptFormalParamList;
        if(OptFormalParamList!=null) OptFormalParamList.setParent(this);
        this.OptVarDeclList=OptVarDeclList;
        if(OptVarDeclList!=null) OptVarDeclList.setParent(this);
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

    public OptVarDeclList getOptVarDeclList() {
        return OptVarDeclList;
    }

    public void setOptVarDeclList(OptVarDeclList OptVarDeclList) {
        this.OptVarDeclList=OptVarDeclList;
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
        if(OptVarDeclList!=null) OptVarDeclList.accept(visitor);
        if(OptStatementList!=null) OptStatementList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(RetType!=null) RetType.traverseTopDown(visitor);
        if(OptFormalParamList!=null) OptFormalParamList.traverseTopDown(visitor);
        if(OptVarDeclList!=null) OptVarDeclList.traverseTopDown(visitor);
        if(OptStatementList!=null) OptStatementList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(RetType!=null) RetType.traverseBottomUp(visitor);
        if(OptFormalParamList!=null) OptFormalParamList.traverseBottomUp(visitor);
        if(OptVarDeclList!=null) OptVarDeclList.traverseBottomUp(visitor);
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

        if(OptVarDeclList!=null)
            buffer.append(OptVarDeclList.toString("  "+tab));
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
