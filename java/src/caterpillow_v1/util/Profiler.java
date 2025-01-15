package caterpillow_v1.util;

import battlecode.common.Clock;

import static caterpillow_v1.Game.rc;

public class Profiler {
    private static int cnt = 0, turnNum = 0;
    public static void begin() {
        cnt = Clock.getBytecodesLeft();
        turnNum = rc.getRoundNum();
    }
    public static void end() {
        int res = cnt - Clock.getBytecodesLeft() + (rc.getRoundNum() - turnNum) * 15000;
        System.out.println("Bytecodes used: " + res);
    }
}
