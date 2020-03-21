// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class Program implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    public rs.etf.pp1.symboltable.concepts.Obj obj = null;

    private ProgramName ProgramName;
    private OptProgramDeclList OptProgramDeclList;
    private OptMethodDeclList OptMethodDeclList;

    public Program (ProgramName ProgramName, OptProgramDeclList OptProgramDeclList, OptMethodDeclList OptMethodDeclList) {
        this.ProgramName=ProgramName;
        if(ProgramName!=null) ProgramName.setParent(this);
        this.OptProgramDeclList=OptProgramDeclList;
        if(OptProgramDeclList!=null) OptProgramDeclList.setParent(this);
        this.OptMethodDeclList=OptMethodDeclList;
        if(OptMethodDeclList!=null) OptMethodDeclList.setParent(this);
    }

    public ProgramName getProgramName() {
        return ProgramName;
    }

    public void setProgramName(ProgramName ProgramName) {
        this.ProgramName=ProgramName;
    }

    public OptProgramDeclList getOptProgramDeclList() {
        return OptProgramDeclList;
    }

    public void setOptProgramDeclList(OptProgramDeclList OptProgramDeclList) {
        this.OptProgramDeclList=OptProgramDeclList;
    }

    public OptMethodDeclList getOptMethodDeclList() {
        return OptMethodDeclList;
    }

    public void setOptMethodDeclList(OptMethodDeclList OptMethodDeclList) {
        this.OptMethodDeclList=OptMethodDeclList;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ProgramName!=null) ProgramName.accept(visitor);
        if(OptProgramDeclList!=null) OptProgramDeclList.accept(visitor);
        if(OptMethodDeclList!=null) OptMethodDeclList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ProgramName!=null) ProgramName.traverseTopDown(visitor);
        if(OptProgramDeclList!=null) OptProgramDeclList.traverseTopDown(visitor);
        if(OptMethodDeclList!=null) OptMethodDeclList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ProgramName!=null) ProgramName.traverseBottomUp(visitor);
        if(OptProgramDeclList!=null) OptProgramDeclList.traverseBottomUp(visitor);
        if(OptMethodDeclList!=null) OptMethodDeclList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Program(\n");

        if(ProgramName!=null)
            buffer.append(ProgramName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptProgramDeclList!=null)
            buffer.append(OptProgramDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptMethodDeclList!=null)
            buffer.append(OptMethodDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Program]");
        return buffer.toString();
    }
}
