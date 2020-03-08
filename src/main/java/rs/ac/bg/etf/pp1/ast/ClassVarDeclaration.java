// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class ClassVarDeclaration extends ClassVarDecl {

    private AccessModifier AccessModifier;
    private Type Type;
    private ClassVarList ClassVarList;

    public ClassVarDeclaration (AccessModifier AccessModifier, Type Type, ClassVarList ClassVarList) {
        this.AccessModifier=AccessModifier;
        if(AccessModifier!=null) AccessModifier.setParent(this);
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.ClassVarList=ClassVarList;
        if(ClassVarList!=null) ClassVarList.setParent(this);
    }

    public AccessModifier getAccessModifier() {
        return AccessModifier;
    }

    public void setAccessModifier(AccessModifier AccessModifier) {
        this.AccessModifier=AccessModifier;
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public ClassVarList getClassVarList() {
        return ClassVarList;
    }

    public void setClassVarList(ClassVarList ClassVarList) {
        this.ClassVarList=ClassVarList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AccessModifier!=null) AccessModifier.accept(visitor);
        if(Type!=null) Type.accept(visitor);
        if(ClassVarList!=null) ClassVarList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AccessModifier!=null) AccessModifier.traverseTopDown(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(ClassVarList!=null) ClassVarList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AccessModifier!=null) AccessModifier.traverseBottomUp(visitor);
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(ClassVarList!=null) ClassVarList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassVarDeclaration(\n");

        if(AccessModifier!=null)
            buffer.append(AccessModifier.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ClassVarList!=null)
            buffer.append(ClassVarList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassVarDeclaration]");
        return buffer.toString();
    }
}
