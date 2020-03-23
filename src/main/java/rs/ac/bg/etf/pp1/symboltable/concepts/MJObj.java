package rs.ac.bg.etf.pp1.symboltable.concepts;

import rs.etf.pp1.symboltable.concepts.*;

public class MJObj extends Obj {

    public enum Access { PUBLIC, PROTECTED, PRIVATE }

    public static String getKindName(int kind) {
        switch (kind) {
            case Obj.Con:   return "Constant";
            case Obj.Var:   return "Variable ";
            case Obj.Type:  return "Type ";
            case Obj.Meth:  return "Method ";
            case Obj.Fld:   return "Field ";
            case Obj.Elem:  return "Element ";
            case Obj.Prog:  return "Program ";
            default:        return "Unknown kind";
        }
    }

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
        output.append(getKindName(getKind()));
        // name
        output.append(" '").append(getName()).append("': ");
        // field access

        // type
        if (getKind() != Obj.Var  || !"this".equalsIgnoreCase(getName())) output.append(MJStruct.getTypeName(getType()));
        // adr
        output.append(", ").append(getAdr());
        // level
        output.append(", ").append(getLevel()).append(' ');
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