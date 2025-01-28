package caterpillow.util;

import battlecode.common.Clock;

import static caterpillow.Game.rc;
import static caterpillow.util.Util.*;

public class Profiler {
    private static int cnt = 0, turnNum = 0;
    private static int acc = 0;
    private static boolean running = false;
    public static void begin() {
        cnt = Clock.getBytecodesLeft();
        turnNum = rc.getRoundNum();
        running = true;
    }
    public static void end() {
        if(!running) return;
        pause();
        report();
    }
    public static void end(Object obj) {
        if(!running) return;
        pause();
        report(obj);
    }
    public static void pause() {
        if(!running) return;
        int res = cnt - Clock.getBytecodesLeft() + (rc.getRoundNum() - turnNum) * 17500;
        acc += res;
        running = false;
    }
    public static void report() {
        // System.out.println((acc - 11) + (acc >= 17500 ? " EXCEEDED" : ""));
        indicate(acc + (acc >= 17500 ? " EXCEEDED" : ""));
        acc = 0;
    }
    public static void report(Object obj) {
        // System.out.println(obj.toString() + ": " + (acc - 12) + (acc >= 17500 ? " EXCEEDED" : ""));
        indicate(obj.toString() + ": " + acc + (acc >= 17500 ? " EXCEEDED" : ""));
        acc = 0;
    }
}
