package rs.ac.bg.etf.pp1.symboltable.concepts;

import rs.ac.bg.etf.pp1.util.MJUtils;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.structure.*;

import java.util.List;

public class MJType extends Struct {

    /******************** Static members **************************************************************/

    // Type codes
    public static final int Void = 8;

    public static String getBasicTypeName(MJType type) {
        switch (type.getKind()) {
            case None: return "none";
            case Int: return "int";
            case Char: return "char";
            case Bool: return "bool";
            case Array: return "array of " + getBasicTypeName(type.getElemType());
            case Class: return "class";
            case Void: return "void";
            default: return "unknown type";
        }
    }

    public static String getClassTypeName(MJType classType) {
        if (classType.getKind() != Class) return "";
        for (Scope scope = Tab.currentScope; scope != null; scope = scope.getOuter()) {
            for (Obj obj : scope.values()) {
                if (obj.getType() == classType && obj.getKind() == Obj.Type) {
                    return obj.getName();
                }
                if (obj.getLocalSymbols().size() > 0) {
                    for (Obj localObj : obj.getLocalSymbols()) {
                        if (localObj.getType() == classType && localObj.getKind() == Obj.Type) {
                            return localObj.getName();
                        }
                    }
                }
            }
        }
        return "";
    }

    public static String getTypeName(MJType type) {
        if (type.getKind() == Array) return getTypeName(type.getElemType()) + " array";
        if (type.getKind() == Class) return getClassTypeName(type);
        return getBasicTypeName(type);
    }

    /********************* Private fields *************************************************************/

    // Inherited fields:
    //   int kind                       - Possible values: None, Int, Char, Array, Class, Bool, Void
    //   Struct elemType                - kind == Array:    Array element type
    //                                    kind == Class:    Superclass type
    //     NOTE: In this implementation it will always be an instance of MJType
    //   int numOfFields                - kind == Class:    Number of class fields
    //   SymbolDataStructure members    - kind == Class:    Hash table containing class fields

    /******************** Constructors ****************************************************************/

    public MJType(int kind) {
        super(kind);
    }

    public MJType(int kind, MJType elemType) {
        super(kind, elemType);
        if (kind == Class) setElementType(elemType);
    }

    public MJType(int kind, SymbolDataStructure members) {
        super(kind, members);
    }

    /******************** Method overrides ************************************************************/

    @Override
    public void setMembers(SymbolDataStructure symbols) {
        if (symbols == null) {
            super.setMembers(null);
            return;
        }
        SymbolDataStructure newMembers = new HashTableDataStructure();
        for (Obj o : symbols.symbols()) if (o.getKind() == MJSymbol.Fld) newMembers.insertKey(o); // first add fields
        for (Obj o : symbols.symbols()) if (o.getKind() == MJSymbol.Meth) newMembers.insertKey(o); // then add methods
        super.setMembers(newMembers); // set members
    }

    @Override
    public MJType getElemType() {
        return (MJType) super.getElemType();
    }

    @Override
    public String toString() {
        if (getKind() != Class) return MJType.getTypeName(this);
        StringBuilder output = new StringBuilder();
        output.append("Class ").append(MJType.getClassTypeName(this)).append(" [");
        for (Obj o : getMembers()) output.append(o);
        output.append("]");
        return output.toString();
    }

    /******************** Public methods **************************************************************/

    public List<MJSymbol> getMembersList() { return MJUtils.collectionToList(getMembers()); }
}