// generated with ast extension for cup
// version 0.8
// 7/2/2020 13:23:6


package rs.ac.bg.etf.pp1.ast;

public class ClassVariable extends ClassVarDecl {

    private AccessModifier AccessModifier;
    private VarDecl VarDecl;

    public ClassVariable (AccessModifier AccessModifier, VarDecl VarDecl) {
        this.AccessModifier=AccessModifier;
        if(AccessModifier!=null) AccessModifier.setParent(this);
        this.VarDecl=VarDecl;
        if(VarDecl!=null) VarDecl.setParent(this);
    }

    public AccessModifier getAccessModifier() {
        return AccessModifier;
    }

    public void setAccessModifier(AccessModifier AccessModifier) {
        this.AccessModifier=AccessModifier;
    }

    public VarDecl getVarDecl() {
        return VarDecl;
    }

    public void setVarDecl(VarDecl VarDecl) {
        this.VarDecl=VarDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AccessModifier!=null) AccessModifier.accept(visitor);
        if(VarDecl!=null) VarDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AccessModifier!=null) AccessModifier.traverseTopDown(visitor);
        if(VarDecl!=null) VarDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AccessModifier!=null) AccessModifier.traverseBottomUp(visitor);
        if(VarDecl!=null) VarDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassVariable(\n");

        if(AccessModifier!=null)
            buffer.append(AccessModifier.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDecl!=null)
            buffer.append(VarDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassVariable]");
        return buffer.toString();
    }
}
