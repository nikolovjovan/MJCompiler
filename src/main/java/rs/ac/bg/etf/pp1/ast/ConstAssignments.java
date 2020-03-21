// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class ConstAssignments extends ConstAssignmentList {

    private ConstAssignmentList ConstAssignmentList;
    private ConstAssignment ConstAssignment;

    public ConstAssignments (ConstAssignmentList ConstAssignmentList, ConstAssignment ConstAssignment) {
        this.ConstAssignmentList=ConstAssignmentList;
        if(ConstAssignmentList!=null) ConstAssignmentList.setParent(this);
        this.ConstAssignment=ConstAssignment;
        if(ConstAssignment!=null) ConstAssignment.setParent(this);
    }

    public ConstAssignmentList getConstAssignmentList() {
        return ConstAssignmentList;
    }

    public void setConstAssignmentList(ConstAssignmentList ConstAssignmentList) {
        this.ConstAssignmentList=ConstAssignmentList;
    }

    public ConstAssignment getConstAssignment() {
        return ConstAssignment;
    }

    public void setConstAssignment(ConstAssignment ConstAssignment) {
        this.ConstAssignment=ConstAssignment;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstAssignmentList!=null) ConstAssignmentList.accept(visitor);
        if(ConstAssignment!=null) ConstAssignment.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstAssignmentList!=null) ConstAssignmentList.traverseTopDown(visitor);
        if(ConstAssignment!=null) ConstAssignment.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstAssignmentList!=null) ConstAssignmentList.traverseBottomUp(visitor);
        if(ConstAssignment!=null) ConstAssignment.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstAssignments(\n");

        if(ConstAssignmentList!=null)
            buffer.append(ConstAssignmentList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstAssignment!=null)
            buffer.append(ConstAssignment.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstAssignments]");
        return buffer.toString();
    }
}
