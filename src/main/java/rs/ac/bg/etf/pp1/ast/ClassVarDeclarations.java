// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class ClassVarDeclarations extends OptClassVarDeclList {

    private OptClassVarDeclList OptClassVarDeclList;
    private ClassVarDecl ClassVarDecl;

    public ClassVarDeclarations (OptClassVarDeclList OptClassVarDeclList, ClassVarDecl ClassVarDecl) {
        this.OptClassVarDeclList=OptClassVarDeclList;
        if(OptClassVarDeclList!=null) OptClassVarDeclList.setParent(this);
        this.ClassVarDecl=ClassVarDecl;
        if(ClassVarDecl!=null) ClassVarDecl.setParent(this);
    }

    public OptClassVarDeclList getOptClassVarDeclList() {
        return OptClassVarDeclList;
    }

    public void setOptClassVarDeclList(OptClassVarDeclList OptClassVarDeclList) {
        this.OptClassVarDeclList=OptClassVarDeclList;
    }

    public ClassVarDecl getClassVarDecl() {
        return ClassVarDecl;
    }

    public void setClassVarDecl(ClassVarDecl ClassVarDecl) {
        this.ClassVarDecl=ClassVarDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptClassVarDeclList!=null) OptClassVarDeclList.accept(visitor);
        if(ClassVarDecl!=null) ClassVarDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptClassVarDeclList!=null) OptClassVarDeclList.traverseTopDown(visitor);
        if(ClassVarDecl!=null) ClassVarDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptClassVarDeclList!=null) OptClassVarDeclList.traverseBottomUp(visitor);
        if(ClassVarDecl!=null) ClassVarDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassVarDeclarations(\n");

        if(OptClassVarDeclList!=null)
            buffer.append(OptClassVarDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ClassVarDecl!=null)
            buffer.append(ClassVarDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassVarDeclarations]");
        return buffer.toString();
    }
}
