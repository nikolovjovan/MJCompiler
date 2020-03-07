package rs.ac.bg.etf.pp1;

import java_cup.runtime.*;
import org.apache.log4j.*;

%%

%public
%class MJLexer

%cup
%line
%column

%{
    String fileName;
    private Logger log = Logger.getLogger(getClass());

    private int errorLine, errorColumn;
    private StringBuilder errorSymbol = null;

    private Symbol new_symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }

    private Symbol new_symbol(int type) {
        return new_symbol(type, yytext());
    }

    private void print_error(int line, int column, String message) {
        System.err.println(fileName + ":" + line + ":" + column + ": " + message);
        log.error(fileName + ":" + line + ":" + column + ": " + message);
    }
%}

%ctorarg String fileName

%init{
    this.fileName = fileName;
    errorSymbol = new StringBuilder();
%init}

%eofval{
    return new_symbol(sym.EOF);
%eofval}

%xstate ERROR

NewLine         = \n|\r\n
Blank           = \040|\b|\t|\f|{NewLine}

Digit           = [0-9]
Letter          = [a-zA-Z]

PrintableChar   = [\040-\176]
SafeChar        = {Blank}|";"

Comment         = "//" [^\r\n]* {NewLine}?
Ident           = {Letter}({Letter}|{Digit}|_)*
Int             = {Digit}+
Char            = '{PrintableChar}'
Bool            = true|false

%%

<YYINITIAL> {

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

    "+"         { return new_symbol(sym.PLUS); }
    "-"         { return new_symbol(sym.MINUS); }
    "*"         { return new_symbol(sym.ASTERISK); }
    "/"         { return new_symbol(sym.SLASH); }
    "%"         { return new_symbol(sym.PERCENT); }
    "=="        { return new_symbol(sym.EQL); }
    "!="        { return new_symbol(sym.NEQ); }
    ">"         { return new_symbol(sym.GRT); }
    ">="        { return new_symbol(sym.GEQ); }
    "<"         { return new_symbol(sym.LSS); }
    "<="        { return new_symbol(sym.LEQ); }
    "&&"        { return new_symbol(sym.AND); }
    "||"        { return new_symbol(sym.OR); }
    "="         { return new_symbol(sym.ASSIGN); }
    "++"        { return new_symbol(sym.PLUSPLUS); }
    "--"        { return new_symbol(sym.MINUSMINUS); }
    "+="        { return new_symbol(sym.PLUS_ASSIGN); }
    "-="        { return new_symbol(sym.MINUS_ASSIGN); }
    "*="        { return new_symbol(sym.ASTERISK_ASSIGN); }
    "/="        { return new_symbol(sym.SLASH_ASSIGN); }
    "%="        { return new_symbol(sym.PERCENT_ASSIGN); }

    "("         { return new_symbol(sym.LPAREN); }
    ")"         { return new_symbol(sym.RPAREN); }
    "["         { return new_symbol(sym.LBRACK); }
    "]"         { return new_symbol(sym.RBRACK); }
    "{"         { return new_symbol(sym.LBRACE); }
    "}"         { return new_symbol(sym.RBRACE); }
    ":"         { return new_symbol(sym.COLON); }
    ";"         { return new_symbol(sym.SEMICOLON); }
    ","         { return new_symbol(sym.COMMA); }
    "."         { return new_symbol(sym.DOT); }

    {Blank}     { /* ignore */ }
    {Comment}   { /* ignore */ }

    {Int}       {
        try {
            Integer value = new Integer(yytext());
            return new_symbol(sym.INT, value);
        } catch (NumberFormatException e) {
            print_error(yyline + 1, yycolumn + 1, "Failed to parse integer: '" + yytext() + "'");
        }
    }
    {Char}      { return new_symbol(sym.CHAR, new Character(yytext().charAt(1))); }
    {Bool}      { return new_symbol(sym.BOOL, new Boolean(yytext())); }
    {Ident}     { return new_symbol(sym.IDENT, yytext()); }

    .   {
        errorLine = yyline + 1;
        errorColumn = yycolumn + 1;
        errorSymbol.setLength(0);
        errorSymbol.append(yytext());
        yybegin(ERROR);
    }

}

<ERROR> {
    {SafeChar}    {
        yybegin(YYINITIAL);
        print_error(errorLine, errorColumn, "Invalid symbol: '" + errorSymbol + "'");
    }
    .   {
        errorSymbol.append(yytext());
    }
}