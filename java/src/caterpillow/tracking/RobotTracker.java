package caterpillow.tracking;

import battlecode.common.RobotInfo;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import caterpillow.util.GameBinaryOperator;
import caterpillow.util.GamePredicate;
import caterpillow.util.Profiler;

import java.util.ArrayList;
import java.util.Arrays;

import static caterpillow.Game.*;

public class RobotTracker {

    public static int nearbyCnt;
    public static RobotInfo[] nearby;

    public static void init() {
    }

    private static void lazyInit() {

    }

    public static void updateTick() {
        lazyInit();
        nearby = rc.senseNearbyRobots();

        RobotInfo[][] exists = new RobotInfo[9][9];
        int x = rc.getLocation().x - 4;
        int y = rc.getLocation().y - 4;
        for (int i = nearby.length - 1; i >= 0; i--) {
            RobotInfo bot = nearby[i];
            exists[bot.location.x - x][bot.location.y - y] = bot;
        }
        nearbyCnt = 0;
        RobotInfo bruh;
        if ((bruh = exists[8][6]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[8][2]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[6][8]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[6][0]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[2][8]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[2][0]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[0][6]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[0][2]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[7][7]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[7][1]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[1][7]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[1][1]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[8][5]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[8][3]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[5][8]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[5][0]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[3][8]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[3][0]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[0][5]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[0][3]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[8][4]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[4][8]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[4][0]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[0][4]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[7][6]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[7][2]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[6][7]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[6][1]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[2][7]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[2][1]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[1][6]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[1][2]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[7][5]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[7][3]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[5][7]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[5][1]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[3][7]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[3][1]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[1][5]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[1][3]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[7][4]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[4][7]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[4][1]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[1][4]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[6][6]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[6][2]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[2][6]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[2][2]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[6][5]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[6][3]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[5][6]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[5][2]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[3][6]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[3][2]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[2][5]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[2][3]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[6][4]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[4][6]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[4][2]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[2][4]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[5][5]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[5][3]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[3][5]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[3][3]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[5][4]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[4][5]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[4][3]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[3][4]) != null) nearby[nearbyCnt++] = bruh;
        if ((bruh = exists[4][4]) != null) nearby[nearbyCnt++] = bruh;
    }

    public static RobotInfo getBestRobot(GameBinaryOperator<RobotInfo> comp, GamePredicate<RobotInfo> pred) throws GameActionException {
        RobotInfo best = null;
        for (int i = nearbyCnt - 1; i >= 0; i--) {
            RobotInfo bot = nearby[i];
            if (pred.test(bot)) {
                if (best == null) {
                    best = bot;
                } else {
                    best = comp.apply(bot, best);
                }
            }
        }
        return best;
    }

    public static RobotInfo getNearestRobot(GamePredicate<RobotInfo> pred) throws GameActionException {
        switch (nearbyCnt) {
            case 69: if (pred.test(nearby[68])) return nearby[68];
            case 68: if (pred.test(nearby[67])) return nearby[67];
            case 67: if (pred.test(nearby[66])) return nearby[66];
            case 66: if (pred.test(nearby[65])) return nearby[65];
            case 65: if (pred.test(nearby[64])) return nearby[64];
            case 64: if (pred.test(nearby[63])) return nearby[63];
            case 63: if (pred.test(nearby[62])) return nearby[62];
            case 62: if (pred.test(nearby[61])) return nearby[61];
            case 61: if (pred.test(nearby[60])) return nearby[60];
            case 60: if (pred.test(nearby[59])) return nearby[59];
            case 59: if (pred.test(nearby[58])) return nearby[58];
            case 58: if (pred.test(nearby[57])) return nearby[57];
            case 57: if (pred.test(nearby[56])) return nearby[56];
            case 56: if (pred.test(nearby[55])) return nearby[55];
            case 55: if (pred.test(nearby[54])) return nearby[54];
            case 54: if (pred.test(nearby[53])) return nearby[53];
            case 53: if (pred.test(nearby[52])) return nearby[52];
            case 52: if (pred.test(nearby[51])) return nearby[51];
            case 51: if (pred.test(nearby[50])) return nearby[50];
            case 50: if (pred.test(nearby[49])) return nearby[49];
            case 49: if (pred.test(nearby[48])) return nearby[48];
            case 48: if (pred.test(nearby[47])) return nearby[47];
            case 47: if (pred.test(nearby[46])) return nearby[46];
            case 46: if (pred.test(nearby[45])) return nearby[45];
            case 45: if (pred.test(nearby[44])) return nearby[44];
            case 44: if (pred.test(nearby[43])) return nearby[43];
            case 43: if (pred.test(nearby[42])) return nearby[42];
            case 42: if (pred.test(nearby[41])) return nearby[41];
            case 41: if (pred.test(nearby[40])) return nearby[40];
            case 40: if (pred.test(nearby[39])) return nearby[39];
            case 39: if (pred.test(nearby[38])) return nearby[38];
            case 38: if (pred.test(nearby[37])) return nearby[37];
            case 37: if (pred.test(nearby[36])) return nearby[36];
            case 36: if (pred.test(nearby[35])) return nearby[35];
            case 35: if (pred.test(nearby[34])) return nearby[34];
            case 34: if (pred.test(nearby[33])) return nearby[33];
            case 33: if (pred.test(nearby[32])) return nearby[32];
            case 32: if (pred.test(nearby[31])) return nearby[31];
            case 31: if (pred.test(nearby[30])) return nearby[30];
            case 30: if (pred.test(nearby[29])) return nearby[29];
            case 29: if (pred.test(nearby[28])) return nearby[28];
            case 28: if (pred.test(nearby[27])) return nearby[27];
            case 27: if (pred.test(nearby[26])) return nearby[26];
            case 26: if (pred.test(nearby[25])) return nearby[25];
            case 25: if (pred.test(nearby[24])) return nearby[24];
            case 24: if (pred.test(nearby[23])) return nearby[23];
            case 23: if (pred.test(nearby[22])) return nearby[22];
            case 22: if (pred.test(nearby[21])) return nearby[21];
            case 21: if (pred.test(nearby[20])) return nearby[20];
            case 20: if (pred.test(nearby[19])) return nearby[19];
            case 19: if (pred.test(nearby[18])) return nearby[18];
            case 18: if (pred.test(nearby[17])) return nearby[17];
            case 17: if (pred.test(nearby[16])) return nearby[16];
            case 16: if (pred.test(nearby[15])) return nearby[15];
            case 15: if (pred.test(nearby[14])) return nearby[14];
            case 14: if (pred.test(nearby[13])) return nearby[13];
            case 13: if (pred.test(nearby[12])) return nearby[12];
            case 12: if (pred.test(nearby[11])) return nearby[11];
            case 11: if (pred.test(nearby[10])) return nearby[10];
            case 10: if (pred.test(nearby[9])) return nearby[9];
            case 9: if (pred.test(nearby[8])) return nearby[8];
            case 8: if (pred.test(nearby[7])) return nearby[7];
            case 7: if (pred.test(nearby[6])) return nearby[6];
            case 6: if (pred.test(nearby[5])) return nearby[5];
            case 5: if (pred.test(nearby[4])) return nearby[4];
            case 4: if (pred.test(nearby[3])) return nearby[3];
            case 3: if (pred.test(nearby[2])) return nearby[2];
            case 2: if (pred.test(nearby[1])) return nearby[1];
            case 1: if (pred.test(nearby[0])) return nearby[0];
            case 0: return null;
            default: throw new IllegalArgumentException("nearbyCnt exceeds 69");
        }
    }
}
