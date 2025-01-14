package caterpillow_v1.util;

import battlecode.common.Clock;
import battlecode.common.GameActionException;

public class Profiler {
    private static int cnt = 0;
    public static void begin() {
        cnt = Clock.getBytecodesLeft();
    }
    public static void end() {
        System.out.println("Bytecodes used: " + (cnt - Clock.getBytecodesLeft()));
    }
}
