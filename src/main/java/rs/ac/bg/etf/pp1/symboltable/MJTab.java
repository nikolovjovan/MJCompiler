package rs.ac.bg.etf.pp1.symboltable;

import rs.ac.bg.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;

public class MJTab extends Tab {

    public enum ScopeId { UNIVERSE, PROGRAM, CLASS, GLOBAL_METHOD, CLASS_METHOD }

    public static final Struct boolType = new Struct(Struct.Bool);
    public static final Struct voidType = new Struct(Struct.None);

    public static final Obj intObj = new Obj(Obj.Type, "int", intType);
    public static final Obj charObj = new Obj(Obj.Type, "char", charType);
    public static final Obj boolObj = new Obj(Obj.Type, "bool", boolType);
    public static final Obj voidObj = new Obj(Obj.Var, "void", voidType);

    private static ScopeId currentScopeId = ScopeId.UNIVERSE;

    // had to shadow Tab.MJTab.currentLevel in order to implement insert method...
    // so now openScope() and closeScope() are shadowed also
    private static int currentLevel = -1;

    public static void init() {
        // TODO: Reimplement init, possibly replace all references to vanilla Tab with MJTab,
        //       replace all Obj, Struct with MJObj, MJStruct...
        Tab.init();
        Tab.currentScope.addToLocals(boolObj);
        Tab.currentScope.addToLocals(voidObj);
    }

    public static void openScope(ScopeId nextScopeId) {
        Tab.openScope();
        MJTab.currentScopeId = nextScopeId;
        MJTab.currentLevel++;
    }

    public static void closeScope(ScopeId nextScopeId) {
        Tab.closeScope();
        MJTab.currentScopeId = nextScopeId;
        MJTab.currentLevel--;
    }

    public static ScopeId getCurrentScopeId() { return currentScopeId; }

    public static int getCurrentLevel() { return currentLevel; }

    // shadow this method to create MJObj instead of regular Obj instances
    public static Obj insert(int kind, String name, Struct type) {
        Obj newObj = new MJObj(kind, name, type, 0, ((MJTab.currentLevel != 0)? 1 : 0));
        if (!Tab.currentScope.addToLocals(newObj)) {
            Obj res = Tab.currentScope.findSymbol(name);
            return (res != null) ? res : Tab.noObj;
        }
        return newObj;
    }
}