package rs.ac.bg.etf.pp1.symboltable;

import rs.ac.bg.etf.pp1.symboltable.concepts.*;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJScope.ScopeID;
import rs.ac.bg.etf.pp1.symboltable.visitors.MJDumpSymbolTableVisitor;
import rs.etf.pp1.symboltable.visitors.SymbolTableVisitor;
import java.io.PrintStream;

/**
 * This is a redefinition of the symbol table used in this compiler. It retains the same methods and serves the same
 * function only now using extended classes instead. Also adds some useful public members and methods.
 */
public class MJTable {

    // Standard types
    public static final MJType noType   = new MJType(MJType.None);

    public static final MJType intType  = new MJType(MJType.Int);
    public static final MJType charType = new MJType(MJType.Char);
    public static final MJType boolType = new MJType(MJType.Bool);
    public static final MJType voidType = new MJType(MJType.Void);

    public static final MJType nullType = new MJType(MJType.Class);

    // Standard symbols
    public static final MJSymbol noSym          = new MJSymbol(MJSymbol.NO_VALUE, "none", noType);

    public static final MJSymbol intTypeSym     = new MJSymbol(MJSymbol.Type, "int", intType);
    public static final MJSymbol charTypeSym    = new MJSymbol(MJSymbol.Type, "char", charType);
    public static final MJSymbol boolTypeSym    = new MJSymbol(MJSymbol.Type, "bool", boolType);
    public static final MJSymbol voidTypeSym    = new MJSymbol(MJSymbol.Type, "void", voidType);

    public static final MJSymbol nullSym        = new MJSymbol(MJSymbol.Con, "null", nullType, 0, 0);
    public static final MJSymbol eolSym         = new MJSymbol(MJSymbol.Con, "eol", charType, 10, 0);

    // Built-in methods
    public static MJSymbol chrMethodSym, ordMethodSym, lenMethodSym;

    // Reference to current symbol scope
    private static MJScope currentScope;
    private static int currentLevel;

    /**
     * Initializes the universe scope by adding built-in symbol nodes.
     */
    public static void init() {
        MJScope universe = currentScope = new MJScope(null, ScopeID.UNIVERSE);

        universe.addToLocals(intTypeSym);
        universe.addToLocals(charTypeSym);
        universe.addToLocals(boolTypeSym);

        universe.addToLocals(nullSym);
        universe.addToLocals(eolSym);

        universe.addToLocals(chrMethodSym = new MJSymbol(MJSymbol.Meth, "chr", charType, 0, 1));
        {
            openScope(ScopeID.GLOBAL_METHOD);
            currentScope.addToLocals(new MJSymbol(MJSymbol.Var, "i", intType, 0, 1, 0));
            chrMethodSym.setLocals(currentScope.getLocals());
            closeScope();
        }

        universe.addToLocals(ordMethodSym = new MJSymbol(MJSymbol.Meth, "ord", intType, 0, 1));
        {
            openScope(ScopeID.GLOBAL_METHOD);
            currentScope.addToLocals(new MJSymbol(MJSymbol.Var, "ch", charType, 0, 1, 0));
            ordMethodSym.setLocals(currentScope.getLocals());
            closeScope();
        }

        universe.addToLocals(lenMethodSym = new MJSymbol(MJSymbol.Meth, "len", intType, 0, 1));
        {
            openScope(ScopeID.GLOBAL_METHOD);
            currentScope.addToLocals(new MJSymbol(MJSymbol.Var, "arr", new MJType(MJType.Array, noType), 0, 1, 0));
            lenMethodSym.setLocals(currentScope.getLocals());
            closeScope();
        }

        currentLevel = -1;
    }

    /**
     * Adds symbols from current scope as local symbols.
     * @param outerScopeSymbol Symbol to which current scope symbols will be added as locals.
     */
    public static void chainLocalSymbols(MJSymbol outerScopeSymbol) {
        outerScopeSymbol.setLocals(currentScope.getLocals());
    }

    /**
     * Adds symbols from current scope as class members.
     * @param innerClass Class type to which current scope symbols will be added as members.
     */
    public static void chainLocalSymbols(MJType innerClass) {
        innerClass.setMembers(currentScope.getLocals());
    }

    /**
     * Returns current scope.
     * @return Current scope.
     */
    public static MJScope getCurrentScope() { return currentScope; }

    public static int getCurrentLevel() { return currentLevel; }

    /**
     * Opens a scope with specified id.
     * @param nextScopeId Id of the newly opened scope.
     */
    public static void openScope(ScopeID nextScopeId) {
        currentScope = new MJScope(currentScope, nextScopeId);
        currentLevel++;
    }

    /**
     * Closes current scope and goes to the outer one.
     */
    public static void closeScope() {
        if (currentScope == null) return;
        currentScope = currentScope.getOuter();
        currentLevel--;
    }

    /**
     * Tries to insert the specified symbol into the table.
     * @param symbol Symbol to insert into the table.
     * @return Specified symbol if a symbol with the same name did not already exist.
     * Otherwise, returns the already existing symbol.
     */
    public static MJSymbol insert(MJSymbol symbol) {
        // Try to insert the node to the end of the symbol list
        if (!currentScope.addToLocals(symbol)) {
            MJSymbol sym = currentScope.findSymbol(symbol.getName());
            return sym != null ? sym : noSym;
        }
        return symbol;
    }

    /**
     * Creates a new symbol node with specified attributes: kind, name and type and tries to inserts it into the table.
     * @param kind Symbol kind.
     * @param name Symbol name.
     * @param type Symbol type.
     * @return Newly created symbol if a symbol with the same name did not already exist.
     * Otherwise, returns the already existing symbol.
     */
    public static MJSymbol insert(int kind, String name, MJType type) {
        // Create a new MJSymbol node with specified kind, name and type
        MJSymbol sym = new MJSymbol(kind, name, type, 0,  currentLevel != 0 ? 1 : 0);
        // Try to insert the node to the end of the symbol list
        return insert(sym);
    }

    /**
     * Tries to find a MJSymbol node with the specified name, starting with current scope outwards.
     * @param name Name of the symbol to search for.
     * @return Found symbol if successful, noSymbol otherwise.
     */
    public static MJSymbol findSymbolInAnyScope(String name) {
        MJSymbol sym;
        for (MJScope s = currentScope; s != null; s = s.getOuter()) {
            sym = s.findSymbol(name);
            if (sym != null) return sym;
        }
        return noSym;
    }

    /**
     * Tries to find a MJSymbol node with the specified name in current scope.
     * @param name Name of the symbol to search for.
     * @return Found symbol if successful, noSymbol otherwise.
     */
    public static MJSymbol findSymbolInCurrentScope(String name) {
        MJSymbol sym = currentScope.findSymbol(name);
        return sym != null ? sym : noSym;
    }

    /**
     * Prints symbol table to specified print stream.
     * @param stv Symbol table visitor.
     * @param out Output stream.
     */
    public static void dump(SymbolTableVisitor stv, PrintStream out) {
        out.println("========================= SYMBOL TABLE DUMP =========================");
        if (stv == null) {
            stv = new MJDumpSymbolTableVisitor();
        }
        for (MJScope s = currentScope; s != null; s = s.getOuter()) {
            s.accept(stv);
        }
        out.println(stv.getOutput());
    }

    /**
     * Prints symbol table to standard output.
     * @param stv Symbol table visitor.
     */
    public static void dump(SymbolTableVisitor stv) {
        dump(stv, System.out);
    }

    /**
     * Prints symbol table to specified print stream.
     * @param out Output stream.
     */
    public static void dump(PrintStream out) {
        dump(new MJDumpSymbolTableVisitor(), out);
    }

    /**
     * Prints symbol table to standard output.
     */
    public static void dump() {
        dump(System.out);
    }
}