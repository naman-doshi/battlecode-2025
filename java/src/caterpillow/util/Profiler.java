package caterpillow.util;

import battlecode.common.Clock;

import java.util.ArrayList;

import static caterpillow.Game.rc;

public class Profiler {
    private static ArrayList<Pair<Integer, Integer>> stk = new ArrayList<>();
    public static void begin() {
        int cnt = Clock.getBytecodesLeft();
        int turnNum = rc.getRoundNum();
        stk.add(new Pair<>(cnt, turnNum));
    }
    public static void end() {
        int cnt = stk.getLast().first;
        int turnNum = stk.getLast().second;
        stk.removeLast();
        int res = cnt - Clock.getBytecodesLeft() + (rc.getRoundNum() - turnNum) * 17500;
        System.out.println("Bytecodes used: " + res);
    }
    public static void end(Object obj) {
        int cnt = stk.getLast().first;
        int turnNum = stk.getLast().second;
        stk.removeLast();
        int res = cnt - Clock.getBytecodesLeft() + (rc.getRoundNum() - turnNum) * 17500;
        System.out.println(obj.toString() + ": " + res);
    }
}
