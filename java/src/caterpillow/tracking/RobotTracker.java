package caterpillow.tracking;

import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;
import static caterpillow.Game.rc;

import caterpillow.util.GameBinaryOperator;
import caterpillow.util.GamePredicate;

public class RobotTracker {

    public static int nearbyCnt;
    public static RobotInfo[] nearby;
    public static RobotInfo[][] exists;

    // VERY SUS OPTIMISATION
    public static RobotInfo bot00;
    public static RobotInfo bot01;
    public static RobotInfo bot02;
    public static RobotInfo bot03;
    public static RobotInfo bot04;
    public static RobotInfo bot05;
    public static RobotInfo bot06;
    public static RobotInfo bot07;
    public static RobotInfo bot08;
    public static RobotInfo bot10;
    public static RobotInfo bot11;
    public static RobotInfo bot12;
    public static RobotInfo bot13;
    public static RobotInfo bot14;
    public static RobotInfo bot15;
    public static RobotInfo bot16;
    public static RobotInfo bot17;
    public static RobotInfo bot18;
    public static RobotInfo bot20;
    public static RobotInfo bot21;
    public static RobotInfo bot22;
    public static RobotInfo bot23;
    public static RobotInfo bot24;
    public static RobotInfo bot25;
    public static RobotInfo bot26;
    public static RobotInfo bot27;
    public static RobotInfo bot28;
    public static RobotInfo bot30;
    public static RobotInfo bot31;
    public static RobotInfo bot32;
    public static RobotInfo bot33;
    public static RobotInfo bot34;
    public static RobotInfo bot35;
    public static RobotInfo bot36;
    public static RobotInfo bot37;
    public static RobotInfo bot38;
    public static RobotInfo bot40;
    public static RobotInfo bot41;
    public static RobotInfo bot42;
    public static RobotInfo bot43;
    public static RobotInfo bot44;
    public static RobotInfo bot45;
    public static RobotInfo bot46;
    public static RobotInfo bot47;
    public static RobotInfo bot48;
    public static RobotInfo bot50;
    public static RobotInfo bot51;
    public static RobotInfo bot52;
    public static RobotInfo bot53;
    public static RobotInfo bot54;
    public static RobotInfo bot55;
    public static RobotInfo bot56;
    public static RobotInfo bot57;
    public static RobotInfo bot58;
    public static RobotInfo bot60;
    public static RobotInfo bot61;
    public static RobotInfo bot62;
    public static RobotInfo bot63;
    public static RobotInfo bot64;
    public static RobotInfo bot65;
    public static RobotInfo bot66;
    public static RobotInfo bot67;
    public static RobotInfo bot68;
    public static RobotInfo bot70;
    public static RobotInfo bot71;
    public static RobotInfo bot72;
    public static RobotInfo bot73;
    public static RobotInfo bot74;
    public static RobotInfo bot75;
    public static RobotInfo bot76;
    public static RobotInfo bot77;
    public static RobotInfo bot78;
    public static RobotInfo bot80;
    public static RobotInfo bot81;
    public static RobotInfo bot82;
    public static RobotInfo bot83;
    public static RobotInfo bot84;
    public static RobotInfo bot85;
    public static RobotInfo bot86;
    public static RobotInfo bot87;
    public static RobotInfo bot88;


    public static void init() {
    }

    private static void lazyInit() {

    }

    public static int countNearbyFriendly(GamePredicate<RobotInfo> pred) throws GameActionException {
        int cnt = 0;
        for (int i = nearbyCnt - 1; i >= 0; i--) {
            if (pred.test(nearby[i])) {
                cnt++;
            }
        }
        return cnt;
    }

    /*

print("RobotInfo[] row;")
for i in range(9):
    print(f"row = exists[{i}];")
    for j in range(9):
        print(f"bot{i}{j} = row[{j}];")

     */

    /*

import math

def generate_sorted_offsets(max_distance):
    max_d = math.ceil(max_distance)
    offsets = []
    for dx in range(-max_d, max_d + 1):
        for dy in range(-max_d, max_d + 1):
            if dx**2 + dy**2 <= max_distance**2:
                offsets.append((dx, dy))
    offsets.sort(key=lambda offset: math.sqrt(offset[0]**2 + offset[1]**2))
    return offsets

max_distance = math.sqrt(20)
sorted_offsets = generate_sorted_offsets(max_distance)

sorted_offsets.reverse()
for x, y in sorted_offsets:
    x += 4
    y += 4
    print(f"if (bot{x}{y} != null) nearby[nearbyCnt++] = bot{x}{y};")

     */

    public static void updateTick() {
        lazyInit();
        nearby = rc.senseNearbyRobots();

        exists = new RobotInfo[9][9];
        int x = rc.getLocation().x - 4;
        int y = rc.getLocation().y - 4;
        for (int i = nearby.length - 1; i >= 0; i--) {
            RobotInfo bot = nearby[i];
            exists[bot.location.x - x][bot.location.y - y] = bot;
        }
        RobotInfo[] row;
        row = exists[0];
        bot00 = row[0];
        bot01 = row[1];
        bot02 = row[2];
        bot03 = row[3];
        bot04 = row[4];
        bot05 = row[5];
        bot06 = row[6];
        bot07 = row[7];
        bot08 = row[8];
        row = exists[1];
        bot10 = row[0];
        bot11 = row[1];
        bot12 = row[2];
        bot13 = row[3];
        bot14 = row[4];
        bot15 = row[5];
        bot16 = row[6];
        bot17 = row[7];
        bot18 = row[8];
        row = exists[2];
        bot20 = row[0];
        bot21 = row[1];
        bot22 = row[2];
        bot23 = row[3];
        bot24 = row[4];
        bot25 = row[5];
        bot26 = row[6];
        bot27 = row[7];
        bot28 = row[8];
        row = exists[3];
        bot30 = row[0];
        bot31 = row[1];
        bot32 = row[2];
        bot33 = row[3];
        bot34 = row[4];
        bot35 = row[5];
        bot36 = row[6];
        bot37 = row[7];
        bot38 = row[8];
        row = exists[4];
        bot40 = row[0];
        bot41 = row[1];
        bot42 = row[2];
        bot43 = row[3];
        bot44 = row[4];
        bot45 = row[5];
        bot46 = row[6];
        bot47 = row[7];
        bot48 = row[8];
        row = exists[5];
        bot50 = row[0];
        bot51 = row[1];
        bot52 = row[2];
        bot53 = row[3];
        bot54 = row[4];
        bot55 = row[5];
        bot56 = row[6];
        bot57 = row[7];
        bot58 = row[8];
        row = exists[6];
        bot60 = row[0];
        bot61 = row[1];
        bot62 = row[2];
        bot63 = row[3];
        bot64 = row[4];
        bot65 = row[5];
        bot66 = row[6];
        bot67 = row[7];
        bot68 = row[8];
        row = exists[7];
        bot70 = row[0];
        bot71 = row[1];
        bot72 = row[2];
        bot73 = row[3];
        bot74 = row[4];
        bot75 = row[5];
        bot76 = row[6];
        bot77 = row[7];
        bot78 = row[8];
        row = exists[8];
        bot80 = row[0];
        bot81 = row[1];
        bot82 = row[2];
        bot83 = row[3];
        bot84 = row[4];
        bot85 = row[5];
        bot86 = row[6];
        bot87 = row[7];
        bot88 = row[8];

        nearbyCnt = 0;
        if (bot86 != null) nearby[nearbyCnt++] = bot86;
        if (bot82 != null) nearby[nearbyCnt++] = bot82;
        if (bot68 != null) nearby[nearbyCnt++] = bot68;
        if (bot60 != null) nearby[nearbyCnt++] = bot60;
        if (bot28 != null) nearby[nearbyCnt++] = bot28;
        if (bot20 != null) nearby[nearbyCnt++] = bot20;
        if (bot06 != null) nearby[nearbyCnt++] = bot06;
        if (bot02 != null) nearby[nearbyCnt++] = bot02;
        if (bot77 != null) nearby[nearbyCnt++] = bot77;
        if (bot71 != null) nearby[nearbyCnt++] = bot71;
        if (bot17 != null) nearby[nearbyCnt++] = bot17;
        if (bot11 != null) nearby[nearbyCnt++] = bot11;
        if (bot85 != null) nearby[nearbyCnt++] = bot85;
        if (bot83 != null) nearby[nearbyCnt++] = bot83;
        if (bot58 != null) nearby[nearbyCnt++] = bot58;
        if (bot50 != null) nearby[nearbyCnt++] = bot50;
        if (bot38 != null) nearby[nearbyCnt++] = bot38;
        if (bot30 != null) nearby[nearbyCnt++] = bot30;
        if (bot05 != null) nearby[nearbyCnt++] = bot05;
        if (bot03 != null) nearby[nearbyCnt++] = bot03;
        if (bot84 != null) nearby[nearbyCnt++] = bot84;
        if (bot48 != null) nearby[nearbyCnt++] = bot48;
        if (bot40 != null) nearby[nearbyCnt++] = bot40;
        if (bot04 != null) nearby[nearbyCnt++] = bot04;
        if (bot76 != null) nearby[nearbyCnt++] = bot76;
        if (bot72 != null) nearby[nearbyCnt++] = bot72;
        if (bot67 != null) nearby[nearbyCnt++] = bot67;
        if (bot61 != null) nearby[nearbyCnt++] = bot61;
        if (bot27 != null) nearby[nearbyCnt++] = bot27;
        if (bot21 != null) nearby[nearbyCnt++] = bot21;
        if (bot16 != null) nearby[nearbyCnt++] = bot16;
        if (bot12 != null) nearby[nearbyCnt++] = bot12;
        if (bot75 != null) nearby[nearbyCnt++] = bot75;
        if (bot73 != null) nearby[nearbyCnt++] = bot73;
        if (bot57 != null) nearby[nearbyCnt++] = bot57;
        if (bot51 != null) nearby[nearbyCnt++] = bot51;
        if (bot37 != null) nearby[nearbyCnt++] = bot37;
        if (bot31 != null) nearby[nearbyCnt++] = bot31;
        if (bot15 != null) nearby[nearbyCnt++] = bot15;
        if (bot13 != null) nearby[nearbyCnt++] = bot13;
        if (bot74 != null) nearby[nearbyCnt++] = bot74;
        if (bot47 != null) nearby[nearbyCnt++] = bot47;
        if (bot41 != null) nearby[nearbyCnt++] = bot41;
        if (bot14 != null) nearby[nearbyCnt++] = bot14;
        if (bot66 != null) nearby[nearbyCnt++] = bot66;
        if (bot62 != null) nearby[nearbyCnt++] = bot62;
        if (bot26 != null) nearby[nearbyCnt++] = bot26;
        if (bot22 != null) nearby[nearbyCnt++] = bot22;
        if (bot65 != null) nearby[nearbyCnt++] = bot65;
        if (bot63 != null) nearby[nearbyCnt++] = bot63;
        if (bot56 != null) nearby[nearbyCnt++] = bot56;
        if (bot52 != null) nearby[nearbyCnt++] = bot52;
        if (bot36 != null) nearby[nearbyCnt++] = bot36;
        if (bot32 != null) nearby[nearbyCnt++] = bot32;
        if (bot25 != null) nearby[nearbyCnt++] = bot25;
        if (bot23 != null) nearby[nearbyCnt++] = bot23;
        if (bot64 != null) nearby[nearbyCnt++] = bot64;
        if (bot46 != null) nearby[nearbyCnt++] = bot46;
        if (bot42 != null) nearby[nearbyCnt++] = bot42;
        if (bot24 != null) nearby[nearbyCnt++] = bot24;
        if (bot55 != null) nearby[nearbyCnt++] = bot55;
        if (bot53 != null) nearby[nearbyCnt++] = bot53;
        if (bot35 != null) nearby[nearbyCnt++] = bot35;
        if (bot33 != null) nearby[nearbyCnt++] = bot33;
        if (bot54 != null) nearby[nearbyCnt++] = bot54;
        if (bot45 != null) nearby[nearbyCnt++] = bot45;
        if (bot43 != null) nearby[nearbyCnt++] = bot43;
        if (bot34 != null) nearby[nearbyCnt++] = bot34;
        if (bot44 != null) nearby[nearbyCnt++] = bot44;
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

    /*

print("RobotInfo best = null, info;")
print("x += 3;")
print("y += 3;")
dx = -1
dy = -1
for i in range(3):
    for j in range(3):
        if dx != 0 or dy != 0:
            print(f"info = exists[x][y];")
            print("if (info != null && pred.test(info)) {")
            print("\tif (best == null) best = info;")
            print("\telse best = comp.apply(best, info);")
            print("}")

        if j < 2:
            if i % 2 == 0:
                print("y++;")
                dy += 1
            else:
                print("y--;")
                dy -= 1
        else:
            print("x++;")
            dx += 1

     */


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
