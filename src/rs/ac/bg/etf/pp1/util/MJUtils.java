package rs.ac.bg.etf.pp1.util;

import rs.ac.bg.etf.pp1.ast.SyntaxNode;
import rs.ac.bg.etf.pp1.symboltable.MJTable;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJSymbol;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJSymbol.Access;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJType;
import rs.etf.pp1.symboltable.concepts.Obj;

import java.util.*;

public class MJUtils {

    public static List<MJSymbol> collectionToList(Collection<Obj> objCollection) {
        if (objCollection.isEmpty()) return Collections.emptyList();
        List<MJSymbol> symbolList = new ArrayList<>();
        for (Obj o : objCollection) {
            if (o instanceof MJSymbol) {
                symbolList.add((MJSymbol) o);
            }
        }
        return symbolList;
    }

    public static int getLineNumber(SyntaxNode info) {
        if (info == null || info.getLine() <= 0) return -1;
        return info.getLine();
    }

    public static boolean isReservedName(String name) {
        if (name == null || name.isEmpty()) return false;
        return name.equals(MJTable.intTypeSym.getName()) || name.equals(MJTable.charTypeSym.getName()) ||
                name.equals(MJTable.boolTypeSym.getName()) || name.equals(MJTable.voidTypeSym.getName()) ||
                name.equals(MJTable.nullSym.getName()) || name.equals(MJTable.eolSym.getName()) ||
                name.equals(MJTable.chrMethodSym.getName()) || name.equals(MJTable.ordMethodSym.getName()) ||
                name.equals(MJTable.lenMethodSym.getName());
    }

    public static boolean isTypeValid(MJType type) {
        return type != null && type != MJTable.noType;
    }

    public static boolean isSymbolValid(MJSymbol sym) {
        return sym != null && sym != MJTable.noSym;
    }

    public static boolean isTypeBasic(MJType type) {
        return type == MJTable.intType || type == MJTable.charType || type == MJTable.boolType;
    }

    public static boolean isValueAssignableToSymbol(MJSymbol sym) {
        return sym.getKind() == MJSymbol.Var || sym.getKind() == MJSymbol.Fld || sym.getKind() == MJSymbol.Elem;
    }

    public static boolean isSymbolAccessibleInGlobalMethod(MJSymbol sym) {
        return sym.getAccess() == Access.DEFAULT || sym.getAccess() == Access.PUBLIC;
    }

    public static boolean isSymbolAccessibleInClassMethod(MJSymbol classSym, MJSymbol sym) {
        return isSymbolAccessibleInGlobalMethod(sym) ||
                sym.getParent() == classSym ||
                sym.getAccess() == Access.PROTECTED && classSym.getType().isDescendantOf(sym.getParent().getType());
    }

    public static MJSymbol findLocalSymbol(MJSymbol parentSym, String name) {
        if (!isSymbolValid(parentSym)) return MJTable.noSym;
        List<MJSymbol> locals = parentSym.getKind() == MJSymbol.Type ?
                parentSym.getType().getMembersList() : parentSym.getLocalSymbolsList();
        for (MJSymbol localSym : locals) {
            if (localSym.getName().equals(name)) return localSym;
        }
        return MJTable.noSym;
    }
}