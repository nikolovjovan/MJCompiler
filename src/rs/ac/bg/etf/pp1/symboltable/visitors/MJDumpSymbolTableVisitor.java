package rs.ac.bg.etf.pp1.symboltable.visitors;

import rs.ac.bg.etf.pp1.symboltable.concepts.MJSymbol;
import rs.ac.bg.etf.pp1.symboltable.concepts.MJType;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.visitors.*;

public class MJDumpSymbolTableVisitor extends DumpSymbolTableVisitor {

    @Override
    public void visitObjNode(Obj obj) {
        if (obj instanceof MJSymbol) output.append(((MJSymbol) obj).toString("  "));
    }

    @Override
    public void visitStructNode(Struct struct) {
        if (struct instanceof MJType) {
            MJType type = (MJType) struct;
            if (struct.getKind() != Struct.Class) {
                output.append(MJType.getTypeName(type));
            } else {
                output.append("Class ").append(MJType.getClassTypeName(type)).append(" [");
                for (MJSymbol sym : type.getMembersList()) {
                    sym.accept(this);
                }
                output.append("]");
            }
        }
    }
}