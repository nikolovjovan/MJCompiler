package rs.ac.bg.etf.pp1.symboltable.concepts;

import rs.etf.pp1.symboltable.concepts.*;

public class MJObj extends Obj {

    public enum Access { DEFAULT, PUBLIC, PROTECTED, PRIVATE }

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
    private Access access = Access.DEFAULT;
    private boolean readOnly = false;

    public MJObj(Obj obj) {
        super(obj.getKind(), obj.getName(), obj.getType(), obj.getAdr(), obj.getLevel());
        if (obj instanceof MJObj) {
            MJObj MJObj = (MJObj) obj;
            abs = MJObj.abs;
            access = MJObj.access;
        }
    }

    public MJObj(int kind, String name, Struct type) {
        super(kind, name, type);
    }

    public MJObj(int kind, String name, Struct type, int adr, int level) {
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

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String toString(String indentation) {
        MJObj.indentation = indentation;
        StringBuilder output = new StringBuilder();
        // kind
        switch (getKind()) {
            case Obj.Con:  output.append("Constant "); break;
            case Obj.Var:  output.append("Variable "); break;
            case Obj.Type: output.append("Type "); break;
            case Obj.Meth: output.append(abs ? "Abstract method " : "Method"); break;
            case Obj.Fld:  output.append("Field "); break;
            case Obj.Prog: output.append("Program "); break;
        }
        // name
        output.append(" '").append(getName()).append("': ");
        // type
        if (getKind() != Obj.Var  || !"this".equalsIgnoreCase(getName())) {
            // abstract class?
            if (abs && getKind() == Obj.Type && getType().getKind() == Struct.Class) output.append("abstract ");
            output.append(MJStruct.getBasicTypeName(getType()));
        }
        // adr
        output.append(", ").append(getAdr());
        // level
        output.append(", ").append(getLevel());
        // class access
        if (access != Access.DEFAULT) output.append(", ").append(getAccess());
        // locals
        if (getKind() == Obj.Prog || getKind() == Obj.Meth) {
            output.append('\n');
            MJObj.nextIndentationLevel();
            for (Obj o : getLocalSymbols()) output.append(currentIndentation.toString()).append(o).append('\n');
            if (getKind() == Obj.Prog || getKind() == Obj.Meth) MJObj.previousIndentationLevel();
        }
        return output.toString();
    }

    @Override
    public String toString() {
        return toString("  ");
    }
}