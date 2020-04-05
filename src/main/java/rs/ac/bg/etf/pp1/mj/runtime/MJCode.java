package rs.ac.bg.etf.pp1.mj.runtime;

import rs.etf.pp1.mj.runtime.Code;

public class MJCode extends Code {

    public static int jeq = 43;
    public static int jne = 44;
    public static int jlt = 45;
    public static int jle = 46;
    public static int jgt = 47;
    public static int jge = 48;

    public static void putCall(int address) {
        put(call);
        put2(address - pc + 1);
    }

    public static void putTrueJump(int condition, int address) {
        put(jcc + condition);
        put2(address - pc + 1);
    }
}