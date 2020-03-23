package rs.ac.bg.etf.pp1.util;

import rs.ac.bg.etf.pp1.symboltable.MJTab;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Struct;

public class MJUtils {

    public static boolean is_type_basic(Struct type) {
        return type != Tab.intType && type != Tab.charType && type != MJTab.boolType;
    }
}