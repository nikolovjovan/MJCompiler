package rs.ac.bg.etf.pp1.symboltable.concepts;

import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.structure.*;

public class MJStruct extends Struct {

    public static String getBasicTypeName(Struct type) {
        switch (type.getKind()) {
            case Struct.None: return "void";
            case Struct.Int: return "int";
            case Struct.Char: return "char";
            case Struct.Bool: return "bool";
            case Struct.Array: return "array of " + getBasicTypeName(type.getElemType());
            case Struct.Class: return "class";
            default: return "unknown type";
        }
    }

    public static String getClassTypeName(Struct classType) {
        if (classType.getKind() != Struct.Class) return "";
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

    public static String getTypeName(Struct type) {
        if (type.getKind() == Struct.Array) return getTypeName(type.getElemType()) + " array";
        if (type.getKind() == Struct.Class) return getClassTypeName(type);
        return getBasicTypeName(type);
    }

    public MJStruct(int kind) {
        super(kind);
    }

    public MJStruct(int kind, Struct elemType) {
        super(kind, elemType);
        if (kind == Class) setElementType(elemType);
    }

    public MJStruct(int kind, SymbolDataStructure members) {
        super(kind, members);
    }

    @Override
    public void setMembers(SymbolDataStructure symbols) {
        if (symbols == null) {
            super.setMembers(null);
            return;
        }
        SymbolDataStructure newMembers = new HashTableDataStructure();
        for (Obj o : symbols.symbols()) if (o.getKind() == Obj.Fld) newMembers.insertKey(o); // first add fields
        for (Obj o : symbols.symbols()) if (o.getKind() != Obj.Fld) newMembers.insertKey(o); // then add methods
        super.setMembers(newMembers); // set members
    }

    @Override
    public String toString() {
        if (getKind() != Struct.Class) return MJStruct.getTypeName(this);
        StringBuilder output = new StringBuilder();
        output.append("Class ").append(MJStruct.getClassTypeName(this)).append(" [");
        for (Obj o : getMembers()) output.append(o);
        output.append("]");
        return output.toString();
    }
}