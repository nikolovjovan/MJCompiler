// generated with ast extension for cup
// version 0.8
// 2/2/2020 21:26:27


package rs.ac.bg.etf.pp1.ast;

public class Expression extends Expr {

    private OptSign OptSign;
    private Term Term;
    private OptAddopExprList OptAddopExprList;

    public Expression (OptSign OptSign, Term Term, OptAddopExprList OptAddopExprList) {
        this.OptSign=OptSign;
        if(OptSign!=null) OptSign.setParent(this);
        this.Term=Term;
        if(Term!=null) Term.setParent(this);
        this.OptAddopExprList=OptAddopExprList;
        if(OptAddopExprList!=null) OptAddopExprList.setParent(this);
    }

    public OptSign getOptSign() {
        return OptSign;
    }

    public void setOptSign(OptSign OptSign) {
        this.OptSign=OptSign;
    }

    public Term getTerm() {
        return Term;
    }

    public void setTerm(Term Term) {
        this.Term=Term;
    }

    public OptAddopExprList getOptAddopExprList() {
        return OptAddopExprList;
    }

    public void setOptAddopExprList(OptAddopExprList OptAddopExprList) {
        this.OptAddopExprList=OptAddopExprList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptSign!=null) OptSign.accept(visitor);
        if(Term!=null) Term.accept(visitor);
        if(OptAddopExprList!=null) OptAddopExprList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptSign!=null) OptSign.traverseTopDown(visitor);
        if(Term!=null) Term.traverseTopDown(visitor);
        if(OptAddopExprList!=null) OptAddopExprList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptSign!=null) OptSign.traverseBottomUp(visitor);
        if(Term!=null) Term.traverseBottomUp(visitor);
        if(OptAddopExprList!=null) OptAddopExprList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Expression(\n");

        if(OptSign!=null)
            buffer.append(OptSign.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Term!=null)
            buffer.append(Term.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptAddopExprList!=null)
            buffer.append(OptAddopExprList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Expression]");
        return buffer.toString();
    }
}
