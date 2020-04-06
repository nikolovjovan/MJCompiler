package rs.ac.bg.etf.pp1.util;

import rs.ac.bg.etf.pp1.ast.SyntaxNode;
import rs.ac.bg.etf.pp1.symboltable.MJTable;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJSymbol;
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

    public static boolean isSymbolValid(MJSymbol sym) {
        return sym != null && sym != MJTable.noSym;
    }

    public static boolean isTypeBasic(MJType type) {
        return type == MJTable.intType || type == MJTable.charType || type == MJTable.boolType;
    }

    public static boolean isValueAssignableToSymbol(MJSymbol sym) {
        return sym.getKind() == MJSymbol.Var || sym.getKind() == MJSymbol.Fld || sym.getKind() == MJSymbol.Elem;
    }
}