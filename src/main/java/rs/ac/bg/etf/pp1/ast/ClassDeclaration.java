// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class ClassDeclaration extends ClassDecl {

    private ClassHeader ClassHeader;
    private OptClassVarDeclList OptClassVarDeclList;
    private OptClassMethodDeclList OptClassMethodDeclList;

    public ClassDeclaration (ClassHeader ClassHeader, OptClassVarDeclList OptClassVarDeclList, OptClassMethodDeclList OptClassMethodDeclList) {
        this.ClassHeader=ClassHeader;
        if(ClassHeader!=null) ClassHeader.setParent(this);
        this.OptClassVarDeclList=OptClassVarDeclList;
        if(OptClassVarDeclList!=null) OptClassVarDeclList.setParent(this);
        this.OptClassMethodDeclList=OptClassMethodDeclList;
        if(OptClassMethodDeclList!=null) OptClassMethodDeclList.setParent(this);
    }

    public ClassHeader getClassHeader() {
        return ClassHeader;
    }

    public void setClassHeader(ClassHeader ClassHeader) {
        this.ClassHeader=ClassHeader;
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
        if(ClassHeader!=null) ClassHeader.accept(visitor);
        if(OptClassVarDeclList!=null) OptClassVarDeclList.accept(visitor);
        if(OptClassMethodDeclList!=null) OptClassMethodDeclList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ClassHeader!=null) ClassHeader.traverseTopDown(visitor);
        if(OptClassVarDeclList!=null) OptClassVarDeclList.traverseTopDown(visitor);
        if(OptClassMethodDeclList!=null) OptClassMethodDeclList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ClassHeader!=null) ClassHeader.traverseBottomUp(visitor);
        if(OptClassVarDeclList!=null) OptClassVarDeclList.traverseBottomUp(visitor);
        if(OptClassMethodDeclList!=null) OptClassMethodDeclList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassDeclaration(\n");

        if(ClassHeader!=null)
            buffer.append(ClassHeader.toString("  "+tab));
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
