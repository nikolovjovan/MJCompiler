// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class LocalVarDeclarations extends OptLocalVarDeclList {

    private OptLocalVarDeclList OptLocalVarDeclList;
    private LocalVarDecl LocalVarDecl;

    public LocalVarDeclarations (OptLocalVarDeclList OptLocalVarDeclList, LocalVarDecl LocalVarDecl) {
        this.OptLocalVarDeclList=OptLocalVarDeclList;
        if(OptLocalVarDeclList!=null) OptLocalVarDeclList.setParent(this);
        this.LocalVarDecl=LocalVarDecl;
        if(LocalVarDecl!=null) LocalVarDecl.setParent(this);
    }

    public OptLocalVarDeclList getOptLocalVarDeclList() {
        return OptLocalVarDeclList;
    }

    public void setOptLocalVarDeclList(OptLocalVarDeclList OptLocalVarDeclList) {
        this.OptLocalVarDeclList=OptLocalVarDeclList;
    }

    public LocalVarDecl getLocalVarDecl() {
        return LocalVarDecl;
    }

    public void setLocalVarDecl(LocalVarDecl LocalVarDecl) {
        this.LocalVarDecl=LocalVarDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptLocalVarDeclList!=null) OptLocalVarDeclList.accept(visitor);
        if(LocalVarDecl!=null) LocalVarDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptLocalVarDeclList!=null) OptLocalVarDeclList.traverseTopDown(visitor);
        if(LocalVarDecl!=null) LocalVarDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptLocalVarDeclList!=null) OptLocalVarDeclList.traverseBottomUp(visitor);
        if(LocalVarDecl!=null) LocalVarDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("LocalVarDeclarations(\n");

        if(OptLocalVarDeclList!=null)
            buffer.append(OptLocalVarDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(LocalVarDecl!=null)
            buffer.append(LocalVarDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [LocalVarDeclarations]");
        return buffer.toString();
    }
}
