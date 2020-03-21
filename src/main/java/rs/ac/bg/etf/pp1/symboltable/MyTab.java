package rs.ac.bg.etf.pp1.symboltable;

import rs.ac.bg.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;

public class MyTab extends Tab {

    public enum ScopeId { UNIVERSE, PROGRAM, CLASS, GLOBAL_METHOD, CLASS_METHOD }

    public static final Struct boolType = new MyStruct(Struct.Bool);
    public static final Struct voidType = new MyStruct(Struct.None);

    public static final Obj voidObject = new MyObj(MyObj.Var, "voidObject", voidType);

    private static ScopeId currentScopeId = ScopeId.UNIVERSE;

    // had to shadow Tab.MyTab.currentLevel in order to implement insert method...
    // so now openScope() and closeScope() are shadowed also
    private static int currentLevel = -1;

    public static void init() {
        Tab.init();
        Tab.currentScope.addToLocals(new Obj(Obj.Type, "bool", boolType));
        Tab.currentScope.addToLocals(new Obj(Obj.Type, "void", voidType));
    }

    public static void openScope(ScopeId nextScopeId) {
        Tab.openScope();
        MyTab.currentScopeId = nextScopeId;
        MyTab.currentLevel++;
    }

    public static void closeScope(ScopeId nextScopeId) {
        Tab.closeScope();
        MyTab.currentScopeId = nextScopeId;
        MyTab.currentLevel--;
    }

    public static ScopeId getCurrentScopeId() { return currentScopeId; }

    public static int getCurrentLevel() { return currentLevel; }

    // shadow this method to create MyObj instead of regular Obj instances
    public static Obj insert(int kind, String name, Struct type) {
        Obj newObj = new MyObj(kind, name, type, 0, ((MyTab.currentLevel != 0)? 1 : 0));
        if (!Tab.currentScope.addToLocals(newObj)) {
            Obj res = Tab.currentScope.findSymbol(name);
            return (res != null) ? res : Tab.noObj;
        }
        return newObj;
    }
}