package caterpillow.util;

import battlecode.common.Clock;

import static caterpillow.Game.rc;

public class Profiler {
    private static int cnt = 0, turnNum = 0;
    public static void begin() {
        cnt = Clock.getBytecodesLeft();
        turnNum = rc.getRoundNum();
    }
    public static void end() {
        int res = cnt - Clock.getBytecodesLeft() + (rc.getRoundNum() - turnNum) * 17500;
        System.out.println("Bytecodes used: " + res);
    }
    public static void end(Object obj) {
        int res = cnt - Clock.getBytecodesLeft() + (rc.getRoundNum() - turnNum) * 17500;
        System.out.println(obj.toString() + ": " + res);
    }
}
