package rs.ac.bg.etf.pp1.util;

import rs.ac.bg.etf.pp1.symboltable.MJTab;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class MJUtils {

    public static boolean is_type_basic(Struct type) {
        return type == Tab.intType || type == Tab.charType || type == MJTab.boolType;
    }

    public static boolean is_assignable(Obj obj) {
        return obj.getKind() == Obj.Var || obj.getKind() == Obj.Fld || obj.getKind() == Obj.Elem;
    }
}