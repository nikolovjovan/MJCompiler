package rs.ac.bg.etf.pp1.symboltable.concepts;

import rs.etf.pp1.symboltable.concepts.*;

public class MyObj extends Obj {

    public enum Access { PUBLIC, PROTECTED, PRIVATE }

    private static String indentation;
    private static StringBuilder currentIndentation = new StringBuilder();

    private static void nextIndentationLevel() {
        currentIndentation.append(indentation);
    }

    private static void previousIndentationLevel() {
        if (currentIndentation.length() > 0) {
            currentIndentation.setLength(currentIndentation.length() - indentation.length());
        }
    }

    private boolean abs = false;
    private Access access = Access.PUBLIC;

    public MyObj(Obj obj) {
        super(obj.getKind(), obj.getName(), obj.getType(), obj.getAdr(), obj.getLevel());
        if (obj instanceof MyObj) {
            MyObj myObj = (MyObj) obj;
            abs = myObj.abs;
            access = myObj.access;
        }
    }

    public MyObj(int kind, String name, Struct type) {
        super(kind, name, type);
    }

    public MyObj(int kind, String name, Struct type, int adr, int level) {
        super(kind, name, type, adr, level);
    }

    public boolean isAbstract() {
        return abs;
    }

    public void setAbstract(boolean abs) {
        this.abs = abs;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public String toString(String indentation) {
        MyObj.indentation = indentation;
        StringBuilder output = new StringBuilder();
        // kind
        switch (getKind()) {
            case Obj.Con:   output.append("Constant "); break;
            case Obj.Var:   output.append("Variable "); break;
            case Obj.Type:  output.append("Type "); break;
            case Obj.Meth:  output.append("Method "); break;
            case Obj.Fld:   output.append("Field "); break;
            case Obj.Elem:  output.append("Element "); break;
            case Obj.Prog:  output.append("Program "); break;
        }
        // name
        output.append('\'').append(getName()).append("': ");
        // field access

        // type
        if (getKind() != Obj.Var  || !"this".equalsIgnoreCase(getName())) output.append(MyStruct.getTypeName(getType()));
        // adr
        output.append(", ").append(getAdr());
        // level
        output.append(", ").append(getLevel()).append(' ');
        // locals
        if (getKind() == Obj.Prog || getKind() == Obj.Meth) {
            output.append('\n');
            MyObj.nextIndentationLevel();
            for (Obj o : getLocalSymbols()) output.append(currentIndentation.toString()).append(o).append('\n');
            if (getKind() == Obj.Prog || getKind() == Obj.Meth) MyObj.previousIndentationLevel();
        }
        return output.toString();
    }

    @Override
    public String toString() {
        return toString("  ");
    }
}