// generated with ast extension for cup
// version 0.8
// 7/2/2020 13:23:6


package rs.ac.bg.etf.pp1.ast;

public class AbstractMethodDeclaration extends AbstractMethodDecl {

    private RetType RetType;
    private String methodName;
    private OptFormalParamList OptFormalParamList;

    public AbstractMethodDeclaration (RetType RetType, String methodName, OptFormalParamList OptFormalParamList) {
        this.RetType=RetType;
        if(RetType!=null) RetType.setParent(this);
        this.methodName=methodName;
        this.OptFormalParamList=OptFormalParamList;
        if(OptFormalParamList!=null) OptFormalParamList.setParent(this);
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

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(RetType!=null) RetType.accept(visitor);
        if(OptFormalParamList!=null) OptFormalParamList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(RetType!=null) RetType.traverseTopDown(visitor);
        if(OptFormalParamList!=null) OptFormalParamList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(RetType!=null) RetType.traverseBottomUp(visitor);
        if(OptFormalParamList!=null) OptFormalParamList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AbstractMethodDeclaration(\n");

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

        buffer.append(tab);
        buffer.append(") [AbstractMethodDeclaration]");
        return buffer.toString();
    }
}
