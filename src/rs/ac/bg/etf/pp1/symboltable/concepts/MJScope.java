package rs.ac.bg.etf.pp1.symboltable.concepts;

import rs.ac.bg.etf.pp1.util.MJUtils;
import rs.etf.pp1.symboltable.concepts.Scope;

import java.util.List;

public class MJScope extends Scope {

    public enum ScopeID { UNIVERSE, PROGRAM, CLASS, GLOBAL_METHOD, CLASS_METHOD }

    /******************** Private fields **************************************************************/

    // Inherited fields:
    //   Scope outer                - Reference to the surrounding scope
    //   SymbolDataStructure locals - Symbol table for this scope
    //   int nVars                  - Number of declared symbols in this scope

    // Scope identifier used for easier symbol management
    private ScopeID id;

    /******************** Constructors ****************************************************************/

    public MJScope(MJScope outer, ScopeID id) {
        super(outer);
        this.id = id;
    }

    /******************** Method overrides ************************************************************/

    @Override
    public MJSymbol findSymbol(String name) {
        return (MJSymbol) super.findSymbol(name);
    }

    @Override
    public MJScope getOuter() {
        return (MJScope) super.getOuter();
    }

    /******************** Public methods **************************************************************/

    public ScopeID getId() {
        return id;
    }

    public List<MJSymbol> valuesList() { return MJUtils.collectionToList(values()); }
}