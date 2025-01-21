package caterpillow.tracking;

import battlecode.common.*;
import caterpillow.Game;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.soldier.SRPStrategy;
import caterpillow.util.GameBinaryOperator;
import caterpillow.util.GamePredicate;

import static battlecode.common.UnitType.SOLDIER;
import static caterpillow.Game.*;
import static caterpillow.util.Util.downgrade;
import static java.lang.Math.*;

public class CellTracker {
    public static final int SRP_DELAY = 1;

    public static final int infCooldown = 10000000;
    private static int cooldownValue;
    public static final int ignoreCooldownReset = 30;
    public static int[][] ignoreCooldown; // the time until which we want to treat this cell as not a valid centre
    public static int[][] processed;

    public static MapInfo[][] mapInfos;
    public static boolean[][] isNearRuin;

    public static int nearbyCnt = 0;
    public static MapInfo[] nearby;

    public static MapLocation[] nearbyRuins;

    public static int enemyPaintCnt;
    public static MapLocation[] enemyPaints;

    public static boolean hasInitSRP = false;

    @SuppressWarnings("unchecked")
    public static void init() throws GameActionException {
        if (rc.getType().isRobotType()) {
            mapInfos = new MapInfo[rc.getMapWidth()][rc.getMapHeight()];
        }
        nearby = new MapInfo[69];
        enemyPaints = new MapLocation[69];
        nearbyCnt = 0;
    }

    private static void lazyInit() {
        if (ignoreCooldown == null && ticksExisted >= 1 && rc.getType() == SOLDIER && bot != null && ((Agent) bot).primaryStrategy instanceof SRPStrategy) {
            ignoreCooldown = new int[mapWidth][mapHeight];
            processed = new int[mapWidth][mapHeight];
        }
        switch (ticksExisted) {
            case 1:
                break;
            case 2:
                isNearRuin = new boolean[mapWidth][mapHeight];

        }
    }
    public static MapInfo getBestCell(GameBinaryOperator<MapInfo> comp, GamePredicate<MapInfo> pred) throws GameActionException {
        MapInfo best = null;
        for (int i = nearbyCnt - 1; i >= 0; i--) {
            MapInfo cell = nearby[i];
            if (pred.test(cell)) {
                if (best == null) {
                    best = cell;
                } else {
                    best = comp.apply(cell, best);
                }
            }
        }
        return best;
    }

    public static MapLocation getNearestRuin(GamePredicate<MapLocation> pred) throws GameActionException {
        MapLocation best = null;
        for (int i = nearbyRuins.length - 1; i >= 0; i--) {
            MapLocation loc = nearbyRuins[i];
            if (pred.test(loc)) {
                if (best == null || Game.pos.distanceSquaredTo(loc) < Game.pos.distanceSquaredTo(best)) {
                    best = loc;
                }
            }
        }
        return best;
    }

    public static MapLocation findRuin(GamePredicate<MapLocation> pred) throws GameActionException {
        switch (nearbyRuins.length) {
            case 5: if (pred.test(nearbyRuins[4])) return nearbyRuins[4];
            case 4: if (pred.test(nearbyRuins[3])) return nearbyRuins[3];
            case 3: if (pred.test(nearbyRuins[2])) return nearbyRuins[2];
            case 2: if (pred.test(nearbyRuins[1])) return nearbyRuins[1];
            case 1: if (pred.test(nearbyRuins[0])) return nearbyRuins[0];
            case 0: return null;
            default: throw new IllegalArgumentException("nearbyRuins exceeds 5");
        }
    }

    private static void staticCheck(int x, int y, MapInfo info) throws GameActionException {
        if (!info.isPassable()) {
            // can be optimised
            int[] arr;
            if (x >= 2) {
                arr = ignoreCooldown[x - 2];
                if (y >= 2) arr[y - 2] = infCooldown;
                if (y >= 1) arr[y - 1] = infCooldown;
                arr[y] = infCooldown;
                if (y + 1 < mapHeight) arr[y + 1] = infCooldown;
                if (y + 2 < mapHeight) arr[y + 2] = infCooldown;
            }
            if (x >= 1) {
                arr = ignoreCooldown[x - 1];
                if (y >= 2) arr[y - 2] = infCooldown;
                if (y >= 1) arr[y - 1] = infCooldown;
                arr[y] = infCooldown;
                if (y + 1 < mapHeight) arr[y + 1] = infCooldown;
                if (y + 2 < mapHeight) arr[y + 2] = infCooldown;
            }
            arr = ignoreCooldown[x];
            if (y >= 2) arr[y - 2] = infCooldown;
            if (y >= 1) arr[y - 1] = infCooldown;
            arr[y] = infCooldown;
            if (y + 1 < mapHeight) arr[y + 1] = infCooldown;
            if (y + 2 < mapHeight) arr[y + 2] = infCooldown;
            if (x + 1 < mapWidth) {
                arr = ignoreCooldown[x + 1];
                if (y >= 2) arr[y - 2] = infCooldown;
                if (y >= 1) arr[y - 1] = infCooldown;
                arr[y] = infCooldown;
                if (y + 1 < mapHeight) arr[y + 1] = infCooldown;
                if (y + 2 < mapHeight) arr[y + 2] = infCooldown;
            }
            if (x + 2 < mapWidth) {
                arr = ignoreCooldown[x + 2];
                if (y >= 2) arr[y - 2] = infCooldown;
                if (y >= 1) arr[y - 1] = infCooldown;
                arr[y] = infCooldown;
                if (y + 1 < mapHeight) arr[y + 1] = infCooldown;
                if (y + 2 < mapHeight) arr[y + 2] = infCooldown;
            }
            processed[x][y] = 1;
        }
    }

    public static MapInfo getNearestCell(GamePredicate<MapInfo> pred) throws GameActionException {
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
            case 9:  if (pred.test(nearby[8])) return nearby[8];
            case 8:  if (pred.test(nearby[7])) return nearby[7];
            case 7:  if (pred.test(nearby[6])) return nearby[6];
            case 6:  if (pred.test(nearby[5])) return nearby[5];
            case 5:  if (pred.test(nearby[4])) return nearby[4];
            case 4:  if (pred.test(nearby[3])) return nearby[3];
            case 3:  if (pred.test(nearby[2])) return nearby[2];
            case 2:  if (pred.test(nearby[1])) return nearby[1];
            case 1:  if (pred.test(nearby[0])) return nearby[0];
            case 0:  return null;
            default: throw new IllegalArgumentException("nearbyCnt exceeds 69");
        }
    }

    public static MapLocation getNearestLocation(GamePredicate<MapLocation> pred) throws GameActionException {
        switch (nearbyCnt) {
        case 69: if (pred.test(nearby[68].getMapLocation())) return nearby[68].getMapLocation();
        case 68: if (pred.test(nearby[67].getMapLocation())) return nearby[67].getMapLocation();
        case 67: if (pred.test(nearby[66].getMapLocation())) return nearby[66].getMapLocation();
        case 66: if (pred.test(nearby[65].getMapLocation())) return nearby[65].getMapLocation();
        case 65: if (pred.test(nearby[64].getMapLocation())) return nearby[64].getMapLocation();
        case 64: if (pred.test(nearby[63].getMapLocation())) return nearby[63].getMapLocation();
        case 63: if (pred.test(nearby[62].getMapLocation())) return nearby[62].getMapLocation();
        case 62: if (pred.test(nearby[61].getMapLocation())) return nearby[61].getMapLocation();
        case 61: if (pred.test(nearby[60].getMapLocation())) return nearby[60].getMapLocation();
        case 60: if (pred.test(nearby[59].getMapLocation())) return nearby[59].getMapLocation();
        case 59: if (pred.test(nearby[58].getMapLocation())) return nearby[58].getMapLocation();
        case 58: if (pred.test(nearby[57].getMapLocation())) return nearby[57].getMapLocation();
        case 57: if (pred.test(nearby[56].getMapLocation())) return nearby[56].getMapLocation();
        case 56: if (pred.test(nearby[55].getMapLocation())) return nearby[55].getMapLocation();
        case 55: if (pred.test(nearby[54].getMapLocation())) return nearby[54].getMapLocation();
        case 54: if (pred.test(nearby[53].getMapLocation())) return nearby[53].getMapLocation();
        case 53: if (pred.test(nearby[52].getMapLocation())) return nearby[52].getMapLocation();
        case 52: if (pred.test(nearby[51].getMapLocation())) return nearby[51].getMapLocation();
        case 51: if (pred.test(nearby[50].getMapLocation())) return nearby[50].getMapLocation();
        case 50: if (pred.test(nearby[49].getMapLocation())) return nearby[49].getMapLocation();
        case 49: if (pred.test(nearby[48].getMapLocation())) return nearby[48].getMapLocation();
        case 48: if (pred.test(nearby[47].getMapLocation())) return nearby[47].getMapLocation();
        case 47: if (pred.test(nearby[46].getMapLocation())) return nearby[46].getMapLocation();
        case 46: if (pred.test(nearby[45].getMapLocation())) return nearby[45].getMapLocation();
        case 45: if (pred.test(nearby[44].getMapLocation())) return nearby[44].getMapLocation();
        case 44: if (pred.test(nearby[43].getMapLocation())) return nearby[43].getMapLocation();
        case 43: if (pred.test(nearby[42].getMapLocation())) return nearby[42].getMapLocation();
        case 42: if (pred.test(nearby[41].getMapLocation())) return nearby[41].getMapLocation();
        case 41: if (pred.test(nearby[40].getMapLocation())) return nearby[40].getMapLocation();
        case 40: if (pred.test(nearby[39].getMapLocation())) return nearby[39].getMapLocation();
        case 39: if (pred.test(nearby[38].getMapLocation())) return nearby[38].getMapLocation();
        case 38: if (pred.test(nearby[37].getMapLocation())) return nearby[37].getMapLocation();
        case 37: if (pred.test(nearby[36].getMapLocation())) return nearby[36].getMapLocation();
        case 36: if (pred.test(nearby[35].getMapLocation())) return nearby[35].getMapLocation();
        case 35: if (pred.test(nearby[34].getMapLocation())) return nearby[34].getMapLocation();
        case 34: if (pred.test(nearby[33].getMapLocation())) return nearby[33].getMapLocation();
        case 33: if (pred.test(nearby[32].getMapLocation())) return nearby[32].getMapLocation();
        case 32: if (pred.test(nearby[31].getMapLocation())) return nearby[31].getMapLocation();
        case 31: if (pred.test(nearby[30].getMapLocation())) return nearby[30].getMapLocation();
        case 30: if (pred.test(nearby[29].getMapLocation())) return nearby[29].getMapLocation();
        case 29: if (pred.test(nearby[28].getMapLocation())) return nearby[28].getMapLocation();
        case 28: if (pred.test(nearby[27].getMapLocation())) return nearby[27].getMapLocation();
        case 27: if (pred.test(nearby[26].getMapLocation())) return nearby[26].getMapLocation();
        case 26: if (pred.test(nearby[25].getMapLocation())) return nearby[25].getMapLocation();
        case 25: if (pred.test(nearby[24].getMapLocation())) return nearby[24].getMapLocation();
        case 24: if (pred.test(nearby[23].getMapLocation())) return nearby[23].getMapLocation();
        case 23: if (pred.test(nearby[22].getMapLocation())) return nearby[22].getMapLocation();
        case 22: if (pred.test(nearby[21].getMapLocation())) return nearby[21].getMapLocation();
        case 21: if (pred.test(nearby[20].getMapLocation())) return nearby[20].getMapLocation();
        case 20: if (pred.test(nearby[19].getMapLocation())) return nearby[19].getMapLocation();
        case 19: if (pred.test(nearby[18].getMapLocation())) return nearby[18].getMapLocation();
        case 18: if (pred.test(nearby[17].getMapLocation())) return nearby[17].getMapLocation();
        case 17: if (pred.test(nearby[16].getMapLocation())) return nearby[16].getMapLocation();
        case 16: if (pred.test(nearby[15].getMapLocation())) return nearby[15].getMapLocation();
        case 15: if (pred.test(nearby[14].getMapLocation())) return nearby[14].getMapLocation();
        case 14: if (pred.test(nearby[13].getMapLocation())) return nearby[13].getMapLocation();
        case 13: if (pred.test(nearby[12].getMapLocation())) return nearby[12].getMapLocation();
        case 12: if (pred.test(nearby[11].getMapLocation())) return nearby[11].getMapLocation();
        case 11: if (pred.test(nearby[10].getMapLocation())) return nearby[10].getMapLocation();
        case 10: if (pred.test(nearby[9].getMapLocation())) return nearby[9].getMapLocation();
        case 9:  if (pred.test(nearby[8].getMapLocation())) return nearby[8].getMapLocation();
        case 8:  if (pred.test(nearby[7].getMapLocation())) return nearby[7].getMapLocation();
        case 7:  if (pred.test(nearby[6].getMapLocation())) return nearby[6].getMapLocation();
        case 6:  if (pred.test(nearby[5].getMapLocation())) return nearby[5].getMapLocation();
        case 5:  if (pred.test(nearby[4].getMapLocation())) return nearby[4].getMapLocation();
        case 4:  if (pred.test(nearby[3].getMapLocation())) return nearby[3].getMapLocation();
        case 3:  if (pred.test(nearby[2].getMapLocation())) return nearby[2].getMapLocation();
        case 2:  if (pred.test(nearby[1].getMapLocation())) return nearby[1].getMapLocation();
        case 1:  if (pred.test(nearby[0].getMapLocation())) return nearby[0].getMapLocation();
        case 0:  return null;
        default: throw new IllegalArgumentException("nearbyCnt exceeds 69");
        }
    }

    public static void updateChange() throws GameActionException {
        // run at the end of every turn loop
        if (rc.getType().isRobotType()) {
            nearbyRuins = rc.senseNearbyRuins(20);
            if (ticksExisted >= 2) {
                for (int k = nearbyRuins.length - 1; k >= 0; k--) {
                    MapLocation loc = nearbyRuins[k];

                    // special

                    RobotInfo bot = rc.senseRobotAtLocation(loc);
                    if (bot == null) {
                        TowerTracker.paintLocs.remove(loc);
                        TowerTracker.nonPaintLocs.remove(loc);
                        TowerTracker.enemyLocs.remove(loc);
                    } else if (rc.getTeam() != bot.getTeam()) {
                        TowerTracker.paintLocs.remove(loc);
                        TowerTracker.nonPaintLocs.remove(loc);
                        if (!TowerTracker.enemyLocs.contains(loc)) {
                            TowerTracker.enemyLocs.add(loc);
                        }
                    } else {
                        TowerTracker.enemyLocs.remove(loc);
                        if (downgrade(bot.getType()).equals(UnitType.LEVEL_ONE_PAINT_TOWER)) {
                            TowerTracker.nonPaintLocs.remove(loc);
                            if (!TowerTracker.paintLocs.contains(loc)) {
                                TowerTracker.paintLocs.add(loc);
                            }
                        } else {
                            TowerTracker.paintLocs.remove(loc);
                            if (!TowerTracker.nonPaintLocs.contains(loc)) {
                                TowerTracker.nonPaintLocs.add(loc);
                            }
                        }
                    }

                    // special

                    if (!isNearRuin[loc.x][loc.y]) {
                        int minX = Math.max(0, loc.x - 2);
                        int minY = Math.max(0, loc.y - 2);
                        int maxX = Math.min(mapWidth, loc.x + 2);
                        int maxY = Math.min(mapHeight, loc.y + 2);
                        for (int i = minX; i < maxX; i++) {
                            for (int j = minY; j < maxY; j++) {
                                isNearRuin[i][j] = true;
                            }
                        }
                    }
                }
            }
            if (rc.getType() == SOLDIER && bot != null && ((Agent) bot).primaryStrategy instanceof SRPStrategy && ticksExisted >= SRP_DELAY) {
                // cheapish
                for (int i = nearbyRuins.length - 1; i >= 0; i--) {
                    MapLocation ruin = nearbyRuins[i];
                    if (rc.senseRobotAtLocation(ruin) != null) {
                        continue;
                    }

                    if (time - ignoreCooldown[ruin.x][ruin.y] < 10) continue;
                    int minx = max(0, ruin.x - 4);
                    int miny = max(0, ruin.y - 4);
                    int maxx = min(rc.getMapWidth() - 1, ruin.x + 4);
                    int maxy = min(rc.getMapHeight() - 1, ruin.y + 4);
                    for (int x = minx; x <= maxx; x++) {
                        for (int y = miny; y <= maxy; y++) {
                            if (ignoreCooldown[x][y] != infCooldown) ignoreCooldown[x][y] = cooldownValue;
                        }
                    }
                }
            }
        }
    }

    public static void updateTick() throws GameActionException {
        cooldownValue = time + ignoreCooldownReset;
        lazyInit();
        // these are all copies, so this is safe
        if (rc.getType().isRobotType()) {
            MapInfo[] infos;
            if (rc.getType() == SOLDIER && bot != null && ((Agent) bot).primaryStrategy instanceof SRPStrategy && ticksExisted >= SRP_DELAY) {
                infos = rc.senseNearbyMapInfos();
                for (int i = infos.length - 1; i >= 0; i--) {
                    MapInfo cur = infos[i];
                    if (cur.getMark() == PaintType.ALLY_PRIMARY) {
                        int x = cur.getMapLocation().x;
                        int y = cur.getMapLocation().y;
                        switch (processed[x][y]) {
                            case 0:
                                rc.setIndicatorDot(new MapLocation(x, y), 0, 255, 255);

                                int minx = max(0, x - 4);
                                int miny = max(0, y - 4);
                                int maxx = min(rc.getMapWidth() - 1, x + 4);
                                int maxy = min(rc.getMapHeight() - 1, y + 4);

                                for (int k = minx; k <= maxx; k++) {
                                    for (int j = miny; j <= maxy; j++) {
                                        if ((k - x) % 4 == 0 && (j - y) % 4 == 0 || abs(k - x) + abs(j - y) == 7)
                                            continue; // tiling
                                        ignoreCooldown[k][j] = infCooldown;
                                    }
                                }

                                processed[x][y] = 1;
                        }
                        mapInfos[x][y] = cur;
                    } else {
                        mapInfos[cur.getMapLocation().x][cur.getMapLocation().y] = cur;
                    }
                }
            } else {
                infos = rc.senseNearbyMapInfos();
                for (int i = infos.length - 1; i >= 0; i--) {
                    MapInfo cur = infos[i];
                    mapInfos[cur.getMapLocation().x][cur.getMapLocation().y] = cur;
                }
            }
            updateNearest();
        } else {
            updateNearestNoob();
        }
        updateChange();
        if (rc.getType() == SOLDIER && bot != null && ((Agent) bot).primaryStrategy instanceof SRPStrategy && ticksExisted >= SRP_DELAY) {
            for (int ei = Math.min(3, enemyPaintCnt); ei > 0; ei--) {
                MapLocation loc = enemyPaints[trng.nextInt(enemyPaintCnt)];
                int x = loc.x;
                int y = loc.y;
                int[] arr;
                if (x >= 2) {
                    arr = ignoreCooldown[x - 2];
                    if (y >= 2 && arr[y - 2] < infCooldown) arr[y - 2] = cooldownValue;
                    if (y >= 1 && arr[y - 1] < infCooldown) arr[y - 1] = cooldownValue;
                    if (arr[y] < infCooldown) arr[y] = cooldownValue;
                    if (y + 1 < mapHeight && arr[y + 1] < infCooldown) arr[y + 1] = cooldownValue;
                    if (y + 2 < mapHeight && arr[y + 2] < infCooldown) arr[y + 2] = cooldownValue;
                }
                if (x >= 1) {
                    arr = ignoreCooldown[x - 1];
                    if (y >= 2 && arr[y - 2] < infCooldown) arr[y - 2] = cooldownValue;
                    if (y >= 1 && arr[y - 1] < infCooldown) arr[y - 1] = cooldownValue;
                    if (arr[y] < infCooldown) arr[y] = cooldownValue;
                    if (y + 1 < mapHeight && arr[y + 1] < infCooldown) arr[y + 1] = cooldownValue;
                    if (y + 2 < mapHeight && arr[y + 2] < infCooldown) arr[y + 2] = cooldownValue;
                }
                arr = ignoreCooldown[x];
                if (y >= 2 && arr[y - 2] < infCooldown) arr[y - 2] = cooldownValue;
                if (y >= 1) arr[y - 1] = infCooldown;
                if (arr[y] < infCooldown) arr[y] = cooldownValue;
                if (y + 1 < mapHeight && arr[y + 1] < infCooldown) arr[y + 1] = cooldownValue;
                if (y + 2 < mapHeight && arr[y + 2] < infCooldown) arr[y + 2] = cooldownValue;
                if (x + 1 < mapWidth) {
                    arr = ignoreCooldown[x + 1];
                    if (y >= 2 && arr[y - 2] < infCooldown) arr[y - 2] = cooldownValue;
                    if (y >= 1 && arr[y - 1] < infCooldown) arr[y - 1] = cooldownValue;
                    if (arr[y] < infCooldown) arr[y] = cooldownValue;
                    if (y + 1 < mapHeight && arr[y + 1] < infCooldown) arr[y + 1] = cooldownValue;
                    if (y + 2 < mapHeight && arr[y + 2] < infCooldown) arr[y + 2] = cooldownValue;
                }
                if (x + 2 < mapWidth) {
                    arr = ignoreCooldown[x + 2];
                    if (y >= 2 && arr[y - 2] < infCooldown) arr[y - 2] = cooldownValue;
                    if (y >= 1 && arr[y - 1] < infCooldown) arr[y - 1] = cooldownValue;
                    if (arr[y] < infCooldown) arr[y] = cooldownValue;
                    if (y + 1 < mapHeight && arr[y + 1] < infCooldown) arr[y + 1] = cooldownValue;
                    if (y + 2 < mapHeight && arr[y + 2] < infCooldown) arr[y + 2] = cooldownValue;
                }
            }
        }
        if (!hasInitSRP && ticksExisted >= SRP_DELAY && rc.getType() == SOLDIER && bot != null && ((Agent) bot).primaryStrategy instanceof SRPStrategy) {
            for (int i = nearbyCnt - 1; i >= 0; i--) {
                MapLocation loc = nearby[i].getMapLocation();
                int x = loc.x;
                int y = loc.y;
                staticCheck(x, y, nearby[i]);
            }
            hasInitSRP = true;
        }
    }

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

print("""        nearbyCnt = 0;
        int x = Game.pos.x;
        int y = Game.pos.y;""")
sorted_offsets.reverse()
lastx = 0
lasty = 0
for x, y in sorted_offsets:
    if lastx != x:
        print(f"x += {x - lastx};")
    if lasty != y:
        print(f"y += {y - lasty};")

    checks = []
    if x > 0:
        checks.append(f"x < maxX")
    elif x < 0:
        checks.append(f"x >= 0")

    if y > 0:
        checks.append(f"y < maxY")
    elif y < 0:
        checks.append(f"y >= 0")

    # checks.append(f"pred.test(rc.senseMapInfo(new MapLocation({bruh1}, {bruh2})))")
    # checks.append(f"pred.test(new MapLocation({bruh1}, {bruh2}))")
    if len(checks) > 0:
        print(f"if ({" && ".join(checks)})")
    print(f"\tnearby[nearbyCnt++] = mapInfos[x][y];")
    # print(f"\tnearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));")
    lastx = x
    lasty = y
# print("return null;")

     */

    private static void updateNearestNoob() throws GameActionException {
        nearbyCnt = 0;
        int x = Game.pos.x;
        int y = Game.pos.y;
        x += 4;
        y += 2;
        if (x < mapWidth && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -4;
        if (x < mapWidth && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -2;
        y += 6;
        if (x < mapWidth && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -8;
        if (x < mapWidth && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -4;
        y += 8;
        if (x >= 0 && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -8;
        if (x >= 0 && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -2;
        y += 6;
        if (x >= 0 && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -4;
        if (x >= 0 && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += 7;
        y += 5;
        if (x < mapWidth && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -6;
        if (x < mapWidth && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -6;
        y += 6;
        if (x >= 0 && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -6;
        if (x >= 0 && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += 7;
        y += 4;
        if (x < mapWidth && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -2;
        if (x < mapWidth && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -3;
        y += 5;
        if (x < mapWidth && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -8;
        if (x < mapWidth && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -2;
        y += 8;
        if (x >= 0 && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -8;
        if (x >= 0 && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -3;
        y += 5;
        if (x >= 0 && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -2;
        if (x >= 0 && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += 8;
        y += 1;
        if (x < mapWidth)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -4;
        y += 4;
        if (y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -8;
        if (y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -4;
        y += 4;
        if (x >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += 7;
        y += 2;
        if (x < mapWidth && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -4;
        if (x < mapWidth && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -1;
        y += 5;
        if (x < mapWidth && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -6;
        if (x < mapWidth && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -4;
        y += 6;
        if (x >= 0 && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -6;
        if (x >= 0 && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -1;
        y += 5;
        if (x >= 0 && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -4;
        if (x >= 0 && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += 6;
        y += 3;
        if (x < mapWidth && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -2;
        if (x < mapWidth && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -2;
        y += 4;
        if (x < mapWidth && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -6;
        if (x < mapWidth && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -2;
        y += 6;
        if (x >= 0 && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -6;
        if (x >= 0 && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -2;
        y += 4;
        if (x >= 0 && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -2;
        if (x >= 0 && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += 6;
        y += 1;
        if (x < mapWidth)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -3;
        y += 3;
        if (y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -6;
        if (y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -3;
        y += 3;
        if (x >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += 5;
        y += 2;
        if (x < mapWidth && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -4;
        if (x < mapWidth && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -4;
        y += 4;
        if (x >= 0 && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -4;
        if (x >= 0 && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += 4;
        y += 3;
        if (x < mapWidth && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -2;
        if (x < mapWidth && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -1;
        y += 3;
        if (x < mapWidth && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -4;
        if (x < mapWidth && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -2;
        y += 4;
        if (x >= 0 && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -4;
        if (x >= 0 && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -1;
        y += 3;
        if (x >= 0 && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -2;
        if (x >= 0 && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += 4;
        y += 1;
        if (x < mapWidth)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -2;
        y += 2;
        if (y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -4;
        if (y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -2;
        y += 2;
        if (x >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += 3;
        y += 1;
        if (x < mapWidth && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -2;
        if (x < mapWidth && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -2;
        y += 2;
        if (x >= 0 && y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -2;
        if (x >= 0 && y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += 2;
        y += 1;
        if (x < mapWidth)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -1;
        y += 1;
        if (y < mapHeight)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        y += -2;
        if (y >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += -1;
        y += 1;
        if (x >= 0)
            nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));
        x += 1;
        nearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation(x, y));

    }

    private static void updateNearest() {
        nearbyCnt = 0;
        int x = Game.pos.x;
        int y = Game.pos.y;
        if (x < 4 || y < 4 || x + 4 >= mapWidth || y + 4 >= mapHeight) {
            x += 4;
            y += 2;
            if (x < mapWidth && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            if (x < mapWidth && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 6;
            if (x < mapWidth && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -8;
            if (x < mapWidth && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -4;
            y += 8;
            if (x >= 0 && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -8;
            if (x >= 0 && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 6;
            if (x >= 0 && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            if (x >= 0 && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += 7;
            y += 5;
            if (x < mapWidth && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -6;
            if (x < mapWidth && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -6;
            y += 6;
            if (x >= 0 && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -6;
            if (x >= 0 && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += 7;
            y += 4;
            if (x < mapWidth && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            if (x < mapWidth && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -3;
            y += 5;
            if (x < mapWidth && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -8;
            if (x < mapWidth && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 8;
            if (x >= 0 && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -8;
            if (x >= 0 && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -3;
            y += 5;
            if (x >= 0 && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            if (x >= 0 && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += 8;
            y += 1;
            if (x < mapWidth)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -4;
            y += 4;
            if (y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -8;
            if (y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -4;
            y += 4;
            if (x >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += 7;
            y += 2;
            if (x < mapWidth && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            if (x < mapWidth && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -1;
            y += 5;
            if (x < mapWidth && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -6;
            if (x < mapWidth && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -4;
            y += 6;
            if (x >= 0 && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -6;
            if (x >= 0 && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -1;
            y += 5;
            if (x >= 0 && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            if (x >= 0 && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += 6;
            y += 3;
            if (x < mapWidth && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            if (x < mapWidth && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 4;
            if (x < mapWidth && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -6;
            if (x < mapWidth && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 6;
            if (x >= 0 && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -6;
            if (x >= 0 && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 4;
            if (x >= 0 && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            if (x >= 0 && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += 6;
            y += 1;
            if (x < mapWidth)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -3;
            y += 3;
            if (y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -6;
            if (y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -3;
            y += 3;
            if (x >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += 5;
            y += 2;
            if (x < mapWidth && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            if (x < mapWidth && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -4;
            y += 4;
            if (x >= 0 && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            if (x >= 0 && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += 4;
            y += 3;
            if (x < mapWidth && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            if (x < mapWidth && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -1;
            y += 3;
            if (x < mapWidth && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            if (x < mapWidth && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 4;
            if (x >= 0 && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            if (x >= 0 && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -1;
            y += 3;
            if (x >= 0 && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            if (x >= 0 && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += 4;
            y += 1;
            if (x < mapWidth)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 2;
            if (y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            if (y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 2;
            if (x >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += 3;
            y += 1;
            if (x < mapWidth && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            if (x < mapWidth && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 2;
            if (x >= 0 && y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            if (x >= 0 && y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += 2;
            y += 1;
            if (x < mapWidth)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -1;
            y += 1;
            if (y < mapHeight)
                nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            if (y >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += -1;
            y += 1;
            if (x >= 0)
                nearby[nearbyCnt++] = mapInfos[x][y];
            x += 1;
            nearby[nearbyCnt++] = mapInfos[x][y];

        } else {
            x += 4;
            y += 2;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 6;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -8;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -4;
            y += 8;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -8;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 6;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += 7;
            y += 5;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -6;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -6;
            y += 6;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -6;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += 7;
            y += 4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -3;
            y += 5;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -8;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 8;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -8;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -3;
            y += 5;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += 8;
            y += 1;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -4;
            y += 4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -8;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -4;
            y += 4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += 7;
            y += 2;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -1;
            y += 5;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -6;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -4;
            y += 6;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -6;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -1;
            y += 5;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += 6;
            y += 3;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -6;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 6;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -6;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += 6;
            y += 1;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -3;
            y += 3;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -6;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -3;
            y += 3;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += 5;
            y += 2;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -4;
            y += 4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += 4;
            y += 3;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -1;
            y += 3;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -1;
            y += 3;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += 4;
            y += 1;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 2;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -4;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 2;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += 3;
            y += 1;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -2;
            y += 2;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += 2;
            y += 1;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -1;
            y += 1;
            nearby[nearbyCnt++] = mapInfos[x][y];
            y += -2;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += -1;
            y += 1;
            nearby[nearbyCnt++] = mapInfos[x][y];
            x += 1;
            nearby[nearbyCnt++] = mapInfos[x][y];
        }
        enemyPaintCnt = 0;
        for (int i = nearbyCnt - 1; i >= 0; i--) {
            if (nearby[i].getPaint().isEnemy()) {
                enemyPaints[enemyPaintCnt++] = nearby[i].getMapLocation();
            }
        }
    }

    /*

import math
import functools

def cross_product(v1, v2):
    return v1[0] * v2[1] - v1[1] * v2[0]

def comparator(a, b):
    dist_a = a[0]**2 + a[1]**2
    dist_b = b[0]**2 + b[1]**2
    if dist_a != dist_b:
        return -1 if dist_a < dist_b else 1

    cross = cross_product(a, b)
    if cross > 0:
        return -1  # `a` is counterclockwise to `b`
    elif cross < 0:
        return 1   # `a` is clockwise to `b`
    else:
        return 0   # `a` and `b` are collinear

def generate_sorted_offsets(max_distance):
    max_d = math.ceil(max_distance)  # Maximum absolute value for dx and dy
    offsets = []
    for dx in range(-max_d, max_d + 1):
        for dy in range(-max_d, max_d + 1):
            if dx**2 + dy**2 <= max_distance**2:
                offsets.append((dx, dy))
    offsets.sort(key=lambda offset: (math.sqrt(offset[0]**2 + offset[1]**2), offset[0], offset[1]))
    return offsets

max_distance = math.sqrt(20)
sorted_offsets = generate_sorted_offsets(max_distance)

dirs = [
    ("EAST", 1, 0),
    ("NORTHEAST", 1, 1),
    ("NORTH", 0, 1),
    ("NORTHWEST", -1, 1),
    ("WEST", -1, 0),
    ("SOUTHWEST", -1, -1),
    ("SOUTH", 0, -1),
    ("SOUTHEAST", 1, -1)
]

print("int x = rc.getLocation().x;")
print("int y = rc.getLocation().y;")
print("switch (dir) {")
for dir, dx, dy in dirs:
    print(f"\tcase {dir}:")
    new_offsets = []
    for x, y in sorted_offsets:
        new_offsets.append((x + dx, y + dy))

    lastx = 0
    lasty = 0
    actual_offsets = []
    for x, y in new_offsets:
        if sorted_offsets.count((x, y)) > 0:
            continue
        actual_offsets.append((x - dx, y - dy))
    actual_offsets.sort(key=functools.cmp_to_key(comparator))
    for x, y in actual_offsets:
        if x != lastx:
            change = x - lastx
            if change > 0:
                print(f"\t\tx += {change};")
            elif change < 0:
                print(f"\t\tx -= {-change};")
        if (y != lasty):
            change = y - lasty
            if change > 0:
                print(f"\t\ty += {change};")
            elif change < 0:
                print(f"\t\ty -= {-change};")

        checks = []
        if x > 0:
            checks.append(f"x < mapWidth")
        elif x < 0:
            checks.append(f"x >= 0")

        if y > 0:
            checks.append(f"y < mapHeight")
        elif y < 0:
            checks.append(f"y >= 0")

        print(f"\t\tif ({" && ".join(checks)}) {'{'}")
        print(f"\t\t\tmapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));")
        print(f"\t\t\tswitch (processed[x][y]) {{ case 0: staticCheck(x, y, mapInfos[x][y]); }}")
        print(f"\t\t{'}'}")
        lastx = x
        lasty = y
    print("\t\tbreak;")
print("}")

     */

    public static void postMove(Direction dir, boolean isLast) throws GameActionException {
        int x = Game.pos.x;
        int y = Game.pos.y;
        if (rc.getType() == SOLDIER && bot != null && ((Agent) bot).primaryStrategy instanceof SRPStrategy && ticksExisted >= SRP_DELAY) {
            switch (dir) {
                case EAST:
                    x += 4;
                    if (x < mapWidth) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y -= 1;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y += 2;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 1;
                    y -= 4;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y += 6;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 1;
                    y -= 7;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 2;
                    y += 2;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y += 4;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 2;
                    y += 2;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    break;
                case NORTHEAST:
                    x += 3;
                    y += 2;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 1;
                    y += 1;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 2;
                    y -= 3;
                    if (x < mapWidth) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 4;
                    y += 4;
                    if (y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 4;
                    y -= 5;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y += 2;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 3;
                    y += 3;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 2;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 4;
                    y -= 1;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 1;
                    y -= 5;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y += 4;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 2;
                    y += 2;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 4;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    break;
                case NORTH:
                    y += 4;
                    if (y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 1;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 2;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 4;
                    y -= 1;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 6;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 7;
                    y -= 1;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 2;
                    y += 2;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 4;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 2;
                    y -= 2;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    break;
                case NORTHWEST:
                    x -= 2;
                    y += 3;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 1;
                    y -= 1;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 3;
                    y += 2;
                    if (y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 4;
                    y -= 4;
                    if (x >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 5;
                    y += 4;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 2;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 3;
                    y -= 3;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y -= 2;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 1;
                    y += 4;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 5;
                    y += 1;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 4;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 2;
                    y -= 2;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y -= 4;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    break;
                case WEST:
                    x -= 4;
                    if (x >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y += 1;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y -= 2;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 1;
                    y += 4;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y -= 6;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 1;
                    y += 7;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 2;
                    y -= 2;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y -= 4;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 2;
                    y -= 2;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    break;
                case SOUTHWEST:
                    x -= 3;
                    y -= 2;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 1;
                    y -= 1;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 2;
                    y += 3;
                    if (x >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 4;
                    y -= 4;
                    if (y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 4;
                    y += 5;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y -= 2;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 3;
                    y -= 3;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 2;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 4;
                    y += 1;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 1;
                    y += 5;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y -= 4;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 2;
                    y -= 2;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 4;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    break;
                case SOUTH:
                    y -= 4;
                    if (y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 1;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 2;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 4;
                    y += 1;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 6;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 7;
                    y += 1;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 2;
                    y -= 2;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 4;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 2;
                    y += 2;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    break;
                case SOUTHEAST:
                    x += 2;
                    y -= 3;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 1;
                    y += 1;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 3;
                    y -= 2;
                    if (y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 4;
                    y += 4;
                    if (x < mapWidth) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 5;
                    y -= 4;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 2;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 3;
                    y += 3;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y += 2;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 1;
                    y -= 4;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x -= 5;
                    y -= 1;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 4;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    x += 2;
                    y += 2;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    y += 4;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                        switch (processed[x][y]) { case 0: staticCheck(x, y, mapInfos[x][y]); }
                    }
                    break;
            }

        } else if (!isLast) {
            switch (dir) {
                case EAST:
                    x += 4;
                    if (x < mapWidth) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y -= 1;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y += 2;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 1;
                    y -= 4;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y += 6;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 1;
                    y -= 7;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 2;
                    y += 2;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y += 4;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 2;
                    y += 2;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    break;
                case NORTHEAST:
                    x += 3;
                    y += 2;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 1;
                    y += 1;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 2;
                    y -= 3;
                    if (x < mapWidth) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 4;
                    y += 4;
                    if (y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 4;
                    y -= 5;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y += 2;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 3;
                    y += 3;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 2;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 4;
                    y -= 1;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 1;
                    y -= 5;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y += 4;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 2;
                    y += 2;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 4;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    break;
                case NORTH:
                    y += 4;
                    if (y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 1;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 2;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 4;
                    y -= 1;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 6;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 7;
                    y -= 1;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 2;
                    y += 2;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 4;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 2;
                    y -= 2;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    break;
                case NORTHWEST:
                    x -= 2;
                    y += 3;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 1;
                    y -= 1;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 3;
                    y += 2;
                    if (y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 4;
                    y -= 4;
                    if (x >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 5;
                    y += 4;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 2;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 3;
                    y -= 3;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y -= 2;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 1;
                    y += 4;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 5;
                    y += 1;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 4;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 2;
                    y -= 2;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y -= 4;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    break;
                case WEST:
                    x -= 4;
                    if (x >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y += 1;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y -= 2;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 1;
                    y += 4;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y -= 6;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 1;
                    y += 7;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 2;
                    y -= 2;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y -= 4;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 2;
                    y -= 2;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    break;
                case SOUTHWEST:
                    x -= 3;
                    y -= 2;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 1;
                    y -= 1;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 2;
                    y += 3;
                    if (x >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 4;
                    y -= 4;
                    if (y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 4;
                    y += 5;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y -= 2;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 3;
                    y -= 3;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 2;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 4;
                    y += 1;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 1;
                    y += 5;
                    if (x >= 0 && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y -= 4;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 2;
                    y -= 2;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 4;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    break;
                case SOUTH:
                    y -= 4;
                    if (y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 1;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 2;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 4;
                    y += 1;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 6;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 7;
                    y += 1;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 2;
                    y -= 2;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 4;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 2;
                    y += 2;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    break;
                case SOUTHEAST:
                    x += 2;
                    y -= 3;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 1;
                    y += 1;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 3;
                    y -= 2;
                    if (y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 4;
                    y += 4;
                    if (x < mapWidth) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 5;
                    y -= 4;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 2;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 3;
                    y += 3;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y += 2;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 1;
                    y -= 4;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x -= 5;
                    y -= 1;
                    if (x >= 0 && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 4;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    x += 2;
                    y += 2;
                    if (x < mapWidth && y >= 0) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    y += 4;
                    if (x < mapWidth && y < mapHeight) {
                        mapInfos[x][y] = rc.senseMapInfo(new MapLocation(x, y));
                    }
                    break;
            }
        }
        if (!isLast) {
            updateNearest();
            updateChange();
        }
    }
}