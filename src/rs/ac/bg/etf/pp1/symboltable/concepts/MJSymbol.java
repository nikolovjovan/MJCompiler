package rs.ac.bg.etf.pp1.symboltable.concepts;

import rs.ac.bg.etf.pp1.util.MJUtils;
import rs.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.structure.HashTableDataStructure;
import rs.etf.pp1.symboltable.structure.SymbolDataStructure;

import java.util.List;

public class MJSymbol extends Obj {

    public enum Access { DEFAULT, PRIVATE, PROTECTED, PUBLIC }

    /******************** Static members **************************************************************/

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

    /******************** Private fields **************************************************************/

    // Inherited fields:
    //   String name                - Used in symbol table hash
    //   int kind                   - Possible values: Con, Var, Type, Meth, Fld, Elem, Prog
    //   Struct type                - Used to represent the type of this symbol
    //     NOTE: In this implementation it will always be an instance of MJType
    //   int adr                    - kind == Con:              Constant value
    //                                kind == Meth, Var, Fld:   Memory offset
    //   int level                  - kind == Var:              Nesting level
    //                                kind == Meth:             Formal parameter count
    //   int fpPos                  - kind == Var:              Formal parameter position (only locals)
    //   SymbolDataStructure locals - kind == Meth:             Local variable collection
    //                                                          Includes formal parameter as locals
    //                                kind == Prog:             Program symbol collection

    // kind == Fld, Meth:   Used to check for access level inside subclasses
    private MJSymbol parent = null;

    // kind == Type:        Used to declare type as abstract (only for type.kind == MJType.Class)
    // kind == Meth:        Used to declare method as abstract (used only for abstract class members)
    private boolean abstract_ = false;

    // kind == Fld, Meth:   Member access level (used only for class members)
    private Access access = Access.DEFAULT;

    // kind == Var:         Used in foreach loops to lock a variable preventing assignment inside loop
    private boolean readOnly = false;

    /******************** Constructors ****************************************************************/

    public MJSymbol(int kind, String name, MJType type, int adr, int level, int fpPos) {
        super(kind, name, type, adr, level);
        setFpPos(fpPos);
    }

    public MJSymbol(int kind, String name, MJType type, int adr, int level) {
        super(kind, name, type, adr, level);
        setFpPos(-1);
    }

    public MJSymbol(int kind, String name, MJType type) {
        super(kind, name, type);
        setFpPos(-1);
    }

    public MJSymbol(MJSymbol obj, int kind) {
        super(kind, obj.getName(), obj.getType(), obj.getAdr(), obj.getLevel());
        setFpPos(obj.getFpPos());
        SymbolDataStructure locals = new HashTableDataStructure();
        for (MJSymbol sym : obj.getLocalSymbolsList()) {
            locals.insertKey(new MJSymbol(sym));
        }
        setLocals(locals);
        parent = obj.parent;
        abstract_ = obj.abstract_;
        access = obj.access;
        readOnly = obj.readOnly;
    }

    public MJSymbol(MJSymbol obj) { this(obj, obj.getKind()); }

    /******************** Method overrides ************************************************************/

    @Override
    public MJType getType() {
        // Explicitly cast Struct to MJType and return that to remove all outside casts
        return (MJType) super.getType();
    }

    @Override
    public boolean equals(Object o) {
        // Reference equality check
        if (this == o) return true;

        // Check for super class equality
        if (!super.equals(o)) return false;

        // Only allow this equality check with MJSymbol instance
        if (!(o instanceof MJSymbol)) return false;

        // Check equality for added fields
        MJSymbol other = (MJSymbol) o;
        return parent == other.parent && abstract_ == other.abstract_ && access == other.access && readOnly == other.readOnly;
    }

    @Override
    public String toString() {
        return toString("  ");
    }

    /******************** Public methods **************************************************************/

    public MJSymbol getParent() {
        return parent;
    }

    public void setParent(MJSymbol parent) {
        this.parent = parent;
    }

    public boolean isAbstract() {
        return abstract_;
    }

    public void setAbstract(boolean abstract_) {
        this.abstract_ = abstract_;
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

    public List<MJSymbol> getLocalSymbolsList() { return MJUtils.collectionToList(getLocalSymbols()); }

    public String getKindName() {
        switch (getKind()) {
            case Con:   return "Constant";
            case Var:   return getFpPos() == -1 ? "Variable" : "Formal parameter";
            case Type:  return "Type";
            case Meth:  return abstract_ ? "Abstract method" : "Method";
            case Fld:   return "Field";
            case Elem:  return "Array element";
            case Prog:  return "Program";
            default:    return "Unknown kind";
        }
    }

    public String toString(String indentation) {
        MJSymbol.indentation = indentation;
        boolean inline = indentation.length() == 0;
        StringBuilder output = new StringBuilder();
        // kind
        output.append(getKindName());
        // name
        output.append(" '").append(getName()).append("': ");
        // type
        if (getKind() != Var  || !"this".equalsIgnoreCase(getName())) {
            output.append("type = '");
            // abstract class?
            if (abstract_ && getKind() == Type && getType().getKind() == Struct.Class) output.append("abstract ");
            output.append(MJType.getBasicTypeName(getType())).append('\'');
        }
        // adr
        output.append(", adr = ").append(getAdr());
        // level
        output.append(", level = ").append(getLevel());
        // parent
        if (MJUtils.isSymbolValid(parent)) output.append(", parent = '").append(parent.getName()).append('\'');
        // class access
        if (access != Access.DEFAULT) output.append(", access modifier = ").append(access.toString().toLowerCase());
        // locals
        if (!getLocalSymbols().isEmpty()) {
            output.append(inline ? ' ' : '\n');
            if (!inline) {
                MJSymbol.nextIndentationLevel();
            }
            List<MJSymbol> locals = getLocalSymbolsList();
            for (int i = 0; i < locals.size(); i++) {
                output.append(currentIndentation.toString()).append(locals.get(i));
                if (i < locals.size() - 1) output.append(inline ? ' ' : '\n');
            }
            if (!inline) {
                MJSymbol.previousIndentationLevel();
            }
        }
        return output.toString();
    }
}