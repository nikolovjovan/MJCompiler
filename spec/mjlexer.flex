package rs.ac.bg.etf.pp1;

import java_cup.runtime.*;

%%

%cup
%line
%column

%{
    private int errorLine, errorColumn;
    StringBuilder errorSymbol = null;

    private Symbol new_symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    private Symbol new_symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
%}

%init{
    errorSymbol = new StringBuilder();
%init}

%eofval{
    return new_symbol(sym.EOF);
%eofval}

%xstate ERROR

newLine         = \n|\r\n
blank           = \040|\b|\t|\f|{newLine}

digit           = [0-9]
letter          = [a-zA-Z]

printableChar   = [\040-\176]
safeChar        = {blank}|";"

comment         = "//" [^\r\n]* {newLine}?
ident           = {letter}({letter}|{digit}|_)*
num             = {digit}+
char            = '{printableChar}'
bool            = true|false

%%

<YYINITIAL> {

    {blank}     { /* ignore */ }
    {comment}   { /* ignore */ }

    {ident}     { return new_symbol(sym.IDENT, yytext()); }
    {num}       { return new_symbol(sym.NUM, new Integer(yytext())); }
    {char}      { return new_symbol(sym.CHAR, new Character(yytext().charAt(1))); }
    {bool}      { return new_symbol(sym.BOOL, new Boolean(yytext())); }

    "program"   { return new_symbol(sym.PROGRAM); }
    "break"     { return new_symbol(sym.BREAK); }
    "class"     { return new_symbol(sym.CLASS); }
    "abstract"  { return new_symbol(sym.ABSTRACT); }
    "else"      { return new_symbol(sym.ELSE); }
    "const"     { return new_symbol(sym.CONST); }
    "if"        { return new_symbol(sym.IF); }
    "new"       { return new_symbol(sym.NEW); }
    "print"     { return new_symbol(sym.PRINT); }
    "read"      { return new_symbol(sym.READ); }
    "return"    { return new_symbol(sym.RETURN); }
    "void"      { return new_symbol(sym.VOID); }
    "for"       { return new_symbol(sym.FOR); }
    "extends"   { return new_symbol(sym.EXTENDS); }
    "continue"  { return new_symbol(sym.CONTINUE); }
    "foreach"   { return new_symbol(sym.FOREACH); }
    "public"    { return new_symbol(sym.PUBLIC); }
    "protected" { return new_symbol(sym.PROTECTED); }
    "private"   { return new_symbol(sym.PRIVATE); }

    "+"         { return new_symbol(sym.ADD); }
    "-"         { return new_symbol(sym.SUB); }
    "*"         { return new_symbol(sym.MUL); }
    "/"         { return new_symbol(sym.DIV); }
    "%"         { return new_symbol(sym.MOD); }
    "=="        { return new_symbol(sym.EQL); }
    "!="        { return new_symbol(sym.NEQ); }
    ">"         { return new_symbol(sym.GRT); }
    ">="        { return new_symbol(sym.GEQ); }
    "<"         { return new_symbol(sym.LSS); }
    "<="        { return new_symbol(sym.LEQ); }
    "&&"        { return new_symbol(sym.AND); }
    "||"        { return new_symbol(sym.OR); }
    "="         { return new_symbol(sym.ASSIGN); }
    "++"        { return new_symbol(sym.INC); }
    "--"        { return new_symbol(sym.DEC); }
    "+="        { return new_symbol(sym.ADD_ASSIGN); }
    "-="        { return new_symbol(sym.SUB_ASSIGN); }
    "*="        { return new_symbol(sym.MUL_ASSIGN); }
    "/="        { return new_symbol(sym.DIV_ASSIGN); }
    "%="        { return new_symbol(sym.MOD_ASSIGN); }
    "("         { return new_symbol(sym.LPAREN); }
    ")"         { return new_symbol(sym.RPAREN); }
    "["         { return new_symbol(sym.LBRACK); }
    "]"         { return new_symbol(sym.RBRACK); }
    "{"         { return new_symbol(sym.LBRACE); }
    "}"         { return new_symbol(sym.RBRACE); }
    ";"         { return new_symbol(sym.SEMICOLON); }
    ","         { return new_symbol(sym.COMMA); }
    "."         { return new_symbol(sym.DOT); }

    .   {
        errorLine = yyline + 1;
        errorColumn = yycolumn + 1;
        errorSymbol.setLength(0);
        errorSymbol.append(yytext());
        yybegin(ERROR);
    }

}

<ERROR> {
    {safeChar}    {
        yybegin(YYINITIAL);
        System.err.println("Invalid symbol: '" + errorSymbol + "' at " + errorColumn + ":" + errorLine + "!");
    }
    .   {
        errorSymbol.append(yytext());
    }
}