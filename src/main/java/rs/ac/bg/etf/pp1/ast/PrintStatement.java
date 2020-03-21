// generated with ast extension for cup
// version 0.8
// 20/2/2020 12:43:10


package rs.ac.bg.etf.pp1.ast;

public class PrintStatement extends Statement {

    private PrintExpr PrintExpr;

    public PrintStatement (PrintExpr PrintExpr) {
        this.PrintExpr=PrintExpr;
        if(PrintExpr!=null) PrintExpr.setParent(this);
    }

    public PrintExpr getPrintExpr() {
        return PrintExpr;
    }

    public void setPrintExpr(PrintExpr PrintExpr) {
        this.PrintExpr=PrintExpr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(PrintExpr!=null) PrintExpr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(PrintExpr!=null) PrintExpr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(PrintExpr!=null) PrintExpr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("PrintStatement(\n");

        if(PrintExpr!=null)
            buffer.append(PrintExpr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [PrintStatement]");
        return buffer.toString();
    }
}
