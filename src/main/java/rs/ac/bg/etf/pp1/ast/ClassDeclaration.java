// generated with ast extension for cup
// version 0.8
// 2/2/2020 21:26:27


package rs.ac.bg.etf.pp1.ast;

public class ClassDeclaration extends ClassDecl {

    private String className;
    private OptClassBaseType OptClassBaseType;
    private OptClassVarDeclList OptClassVarDeclList;
    private OptClassMethodDeclList OptClassMethodDeclList;

    public ClassDeclaration (String className, OptClassBaseType OptClassBaseType, OptClassVarDeclList OptClassVarDeclList, OptClassMethodDeclList OptClassMethodDeclList) {
        this.className=className;
        this.OptClassBaseType=OptClassBaseType;
        if(OptClassBaseType!=null) OptClassBaseType.setParent(this);
        this.OptClassVarDeclList=OptClassVarDeclList;
        if(OptClassVarDeclList!=null) OptClassVarDeclList.setParent(this);
        this.OptClassMethodDeclList=OptClassMethodDeclList;
        if(OptClassMethodDeclList!=null) OptClassMethodDeclList.setParent(this);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className=className;
    }

    public OptClassBaseType getOptClassBaseType() {
        return OptClassBaseType;
    }

    public void setOptClassBaseType(OptClassBaseType OptClassBaseType) {
        this.OptClassBaseType=OptClassBaseType;
    }

    public OptClassVarDeclList getOptClassVarDeclList() {
        return OptClassVarDeclList;
    }

    public void setOptClassVarDeclList(OptClassVarDeclList OptClassVarDeclList) {
        this.OptClassVarDeclList=OptClassVarDeclList;
    }

    public OptClassMethodDeclList getOptClassMethodDeclList() {
        return OptClassMethodDeclList;
    }

    public void setOptClassMethodDeclList(OptClassMethodDeclList OptClassMethodDeclList) {
        this.OptClassMethodDeclList=OptClassMethodDeclList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptClassBaseType!=null) OptClassBaseType.accept(visitor);
        if(OptClassVarDeclList!=null) OptClassVarDeclList.accept(visitor);
        if(OptClassMethodDeclList!=null) OptClassMethodDeclList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptClassBaseType!=null) OptClassBaseType.traverseTopDown(visitor);
        if(OptClassVarDeclList!=null) OptClassVarDeclList.traverseTopDown(visitor);
        if(OptClassMethodDeclList!=null) OptClassMethodDeclList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptClassBaseType!=null) OptClassBaseType.traverseBottomUp(visitor);
        if(OptClassVarDeclList!=null) OptClassVarDeclList.traverseBottomUp(visitor);
        if(OptClassMethodDeclList!=null) OptClassMethodDeclList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassDeclaration(\n");

        buffer.append(" "+tab+className);
        buffer.append("\n");

        if(OptClassBaseType!=null)
            buffer.append(OptClassBaseType.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptClassVarDeclList!=null)
            buffer.append(OptClassVarDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptClassMethodDeclList!=null)
            buffer.append(OptClassMethodDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassDeclaration]");
        return buffer.toString();
    }
}
