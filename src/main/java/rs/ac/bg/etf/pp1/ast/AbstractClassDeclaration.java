// generated with ast extension for cup
// version 0.8
// 2/2/2020 21:26:27


package rs.ac.bg.etf.pp1.ast;

public class AbstractClassDeclaration extends AbstractClassDecl {

    private String className;
    private OptClassBaseType OptClassBaseType;
    private OptClassVarDeclList OptClassVarDeclList;
    private OptAbstractClassMethodDeclList OptAbstractClassMethodDeclList;

    public AbstractClassDeclaration (String className, OptClassBaseType OptClassBaseType, OptClassVarDeclList OptClassVarDeclList, OptAbstractClassMethodDeclList OptAbstractClassMethodDeclList) {
        this.className=className;
        this.OptClassBaseType=OptClassBaseType;
        if(OptClassBaseType!=null) OptClassBaseType.setParent(this);
        this.OptClassVarDeclList=OptClassVarDeclList;
        if(OptClassVarDeclList!=null) OptClassVarDeclList.setParent(this);
        this.OptAbstractClassMethodDeclList=OptAbstractClassMethodDeclList;
        if(OptAbstractClassMethodDeclList!=null) OptAbstractClassMethodDeclList.setParent(this);
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
        if(OptClassBaseType!=null) OptClassBaseType.accept(visitor);
        if(OptClassVarDeclList!=null) OptClassVarDeclList.accept(visitor);
        if(OptAbstractClassMethodDeclList!=null) OptAbstractClassMethodDeclList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptClassBaseType!=null) OptClassBaseType.traverseTopDown(visitor);
        if(OptClassVarDeclList!=null) OptClassVarDeclList.traverseTopDown(visitor);
        if(OptAbstractClassMethodDeclList!=null) OptAbstractClassMethodDeclList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptClassBaseType!=null) OptClassBaseType.traverseBottomUp(visitor);
        if(OptClassVarDeclList!=null) OptClassVarDeclList.traverseBottomUp(visitor);
        if(OptAbstractClassMethodDeclList!=null) OptAbstractClassMethodDeclList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AbstractClassDeclaration(\n");

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
