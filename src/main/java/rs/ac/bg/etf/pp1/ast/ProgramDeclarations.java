// generated with ast extension for cup
// version 0.8
// 8/2/2020 22:52:21


package rs.ac.bg.etf.pp1.ast;

public class ProgramDeclarations extends OptProgramDeclList {

    private OptProgramDeclList OptProgramDeclList;
    private ProgramDecl ProgramDecl;

    public ProgramDeclarations (OptProgramDeclList OptProgramDeclList, ProgramDecl ProgramDecl) {
        this.OptProgramDeclList=OptProgramDeclList;
        if(OptProgramDeclList!=null) OptProgramDeclList.setParent(this);
        this.ProgramDecl=ProgramDecl;
        if(ProgramDecl!=null) ProgramDecl.setParent(this);
    }

    public OptProgramDeclList getOptProgramDeclList() {
        return OptProgramDeclList;
    }

    public void setOptProgramDeclList(OptProgramDeclList OptProgramDeclList) {
        this.OptProgramDeclList=OptProgramDeclList;
    }

    public ProgramDecl getProgramDecl() {
        return ProgramDecl;
    }

    public void setProgramDecl(ProgramDecl ProgramDecl) {
        this.ProgramDecl=ProgramDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptProgramDeclList!=null) OptProgramDeclList.accept(visitor);
        if(ProgramDecl!=null) ProgramDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptProgramDeclList!=null) OptProgramDeclList.traverseTopDown(visitor);
        if(ProgramDecl!=null) ProgramDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptProgramDeclList!=null) OptProgramDeclList.traverseBottomUp(visitor);
        if(ProgramDecl!=null) ProgramDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ProgramDeclarations(\n");

        if(OptProgramDeclList!=null)
            buffer.append(OptProgramDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ProgramDecl!=null)
            buffer.append(ProgramDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ProgramDeclarations]");
        return buffer.toString();
    }
}
