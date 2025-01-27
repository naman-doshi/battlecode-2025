//package fix_atk_micro.util;
//
//import battlecode.common.Direction;
//import battlecode.common.GameActionException;
//import battlecode.common.MapInfo;
//import battlecode.common.MapLocation;
//
//import static fix_atk_micro.Game.*;
//
//public class Graveyard2 {
//    private static int maxX, maxY;
//
//    public static MapInfo[][] mapInfos;
//    public static int[][] lastUpdateTime;
//    public static boolean[][] isNearRuin;
//
//    public static int nearbyCnt = 0;
//    public static MapInfo[] nearby;
//
//    public static MapLocation[] nearbyRuins;
//
//
//    @SuppressWarnings("unchecked")
//    public static void init() throws GameActionException {
//        switch (ticksExisted) {
//            case 0:
//                mapInfos = new MapInfo[mapWidth][mapHeight];
//                lastUpdateTime = new int[mapWidth][mapHeight];
//                nearby = new MapInfo[69];
//                nearbyCnt = 0;
//                maxX = mapWidth;
//                maxY = mapHeight;
//                break;
//            case 1:
//                isNearRuin = new boolean[mapWidth][mapHeight];
//        }
//    }
//
//    public static MapInfo getBestCell(GameBinaryOperator<MapInfo> comp, GamePredicate<MapInfo> pred) throws GameActionException {
//        MapInfo best = null;
//        for (int i = nearbyCnt - 1; i >= 0; i--) {
//            MapInfo cell = nearby[i];
//            if (pred.test(cell)) {
//                if (best == null) {
//                    best = cell;
//                } else {
//                    best = comp.apply(cell, best);
//                }
//            }
//        }
//        return best;
//    }
//
//    public static MapInfo getNearestCell(GamePredicate<MapInfo> pred) throws GameActionException {
//        switch (nearbyCnt) {
//            case 69: if (pred.test(nearby[68])) return nearby[68];
//            case 68: if (pred.test(nearby[67])) return nearby[67];
//            case 67: if (pred.test(nearby[66])) return nearby[66];
//            case 66: if (pred.test(nearby[65])) return nearby[65];
//            case 65: if (pred.test(nearby[64])) return nearby[64];
//            case 64: if (pred.test(nearby[63])) return nearby[63];
//            case 63: if (pred.test(nearby[62])) return nearby[62];
//            case 62: if (pred.test(nearby[61])) return nearby[61];
//            case 61: if (pred.test(nearby[60])) return nearby[60];
//            case 60: if (pred.test(nearby[59])) return nearby[59];
//            case 59: if (pred.test(nearby[58])) return nearby[58];
//            case 58: if (pred.test(nearby[57])) return nearby[57];
//            case 57: if (pred.test(nearby[56])) return nearby[56];
//            case 56: if (pred.test(nearby[55])) return nearby[55];
//            case 55: if (pred.test(nearby[54])) return nearby[54];
//            case 54: if (pred.test(nearby[53])) return nearby[53];
//            case 53: if (pred.test(nearby[52])) return nearby[52];
//            case 52: if (pred.test(nearby[51])) return nearby[51];
//            case 51: if (pred.test(nearby[50])) return nearby[50];
//            case 50: if (pred.test(nearby[49])) return nearby[49];
//            case 49: if (pred.test(nearby[48])) return nearby[48];
//            case 48: if (pred.test(nearby[47])) return nearby[47];
//            case 47: if (pred.test(nearby[46])) return nearby[46];
//            case 46: if (pred.test(nearby[45])) return nearby[45];
//            case 45: if (pred.test(nearby[44])) return nearby[44];
//            case 44: if (pred.test(nearby[43])) return nearby[43];
//            case 43: if (pred.test(nearby[42])) return nearby[42];
//            case 42: if (pred.test(nearby[41])) return nearby[41];
//            case 41: if (pred.test(nearby[40])) return nearby[40];
//            case 40: if (pred.test(nearby[39])) return nearby[39];
//            case 39: if (pred.test(nearby[38])) return nearby[38];
//            case 38: if (pred.test(nearby[37])) return nearby[37];
//            case 37: if (pred.test(nearby[36])) return nearby[36];
//            case 36: if (pred.test(nearby[35])) return nearby[35];
//            case 35: if (pred.test(nearby[34])) return nearby[34];
//            case 34: if (pred.test(nearby[33])) return nearby[33];
//            case 33: if (pred.test(nearby[32])) return nearby[32];
//            case 32: if (pred.test(nearby[31])) return nearby[31];
//            case 31: if (pred.test(nearby[30])) return nearby[30];
//            case 30: if (pred.test(nearby[29])) return nearby[29];
//            case 29: if (pred.test(nearby[28])) return nearby[28];
//            case 28: if (pred.test(nearby[27])) return nearby[27];
//            case 27: if (pred.test(nearby[26])) return nearby[26];
//            case 26: if (pred.test(nearby[25])) return nearby[25];
//            case 25: if (pred.test(nearby[24])) return nearby[24];
//            case 24: if (pred.test(nearby[23])) return nearby[23];
//            case 23: if (pred.test(nearby[22])) return nearby[22];
//            case 22: if (pred.test(nearby[21])) return nearby[21];
//            case 21: if (pred.test(nearby[20])) return nearby[20];
//            case 20: if (pred.test(nearby[19])) return nearby[19];
//            case 19: if (pred.test(nearby[18])) return nearby[18];
//            case 18: if (pred.test(nearby[17])) return nearby[17];
//            case 17: if (pred.test(nearby[16])) return nearby[16];
//            case 16: if (pred.test(nearby[15])) return nearby[15];
//            case 15: if (pred.test(nearby[14])) return nearby[14];
//            case 14: if (pred.test(nearby[13])) return nearby[13];
//            case 13: if (pred.test(nearby[12])) return nearby[12];
//            case 12: if (pred.test(nearby[11])) return nearby[11];
//            case 11: if (pred.test(nearby[10])) return nearby[10];
//            case 10: if (pred.test(nearby[9])) return nearby[9];
//            case 9:  if (pred.test(nearby[8])) return nearby[8];
//            case 8:  if (pred.test(nearby[7])) return nearby[7];
//            case 7:  if (pred.test(nearby[6])) return nearby[6];
//            case 6:  if (pred.test(nearby[5])) return nearby[5];
//            case 5:  if (pred.test(nearby[4])) return nearby[4];
//            case 4:  if (pred.test(nearby[3])) return nearby[3];
//            case 3:  if (pred.test(nearby[2])) return nearby[2];
//            case 2:  if (pred.test(nearby[1])) return nearby[1];
//            case 1:  if (pred.test(nearby[0])) return nearby[0];
//            case 0:  return null;
//            default: throw new IllegalArgumentException("nearbyCnt exceeds 69");
//        }
//    }
//
//    public static void updateChange() throws GameActionException {
//        // run at the end of every turn loop
//        nearbyRuins = rc.senseNearbyRuins(20);
//        if (ticksExisted > 0) {
//            for (int k = nearbyRuins.length - 1; k >= 0; k--) {
//                MapLocation loc = nearbyRuins[k];
//                if (!isNearRuin[loc.x][loc.y]) {
//                    int minX = Math.max(0, loc.x - 2);
//                    int minY = Math.max(0, loc.y - 2);
//                    int maxX = Math.min(CellTracker.maxX, loc.x + 2);
//                    int maxY = Math.min(CellTracker.maxY, loc.y + 2);
//                    for (int i = minX; i < maxX; i++) {
//                        for (int j = minY; j < maxY; j++) {
//                            isNearRuin[i][j] = true;
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    public static void updateTick() throws GameActionException {
//        init();
//        // these are all copies, so this is safe
//        MapInfo[] infos = rc.senseNearbyMapInfos();
//        for (int i = infos.length - 1; i >= 0; i--) {
//            MapInfo cur = infos[i];
//            int x = cur.getMapLocation().x;
//            int y = cur.getMapLocation().y;
//            mapInfos[x][y] = cur;
//            lastUpdateTime[x][y] = time;
//        }
//        updateNearest(infos.length);
//        updateChange();
//    }
//
//    public static void postMove(Direction dir) throws GameActionException {
//        int x = rc.getLocation().x;
//        int y = rc.getLocation().y;
//        switch (dir) {
//            case EAST:
//                if (x + 4 < maxX) {
//                    int nx = x + 4;
//                    mapInfos[nx][y] = rc.senseMapInfo(new MapLocation(nx, y));
//                    lastUpdateTime[nx][y] = time;
//                }
//                if (x + 4 < maxX && y >= 1) {
//                    int nx = x + 4;
//                    int ny = y - 1;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 4 < maxX && y + 1 < maxY) {
//                    int nx = x + 4;
//                    int ny = y + 1;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 3 < maxX && y >= 3) {
//                    int nx = x + 3;
//                    int ny = y - 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 3 < maxX && y + 3 < maxY) {
//                    int nx = x + 3;
//                    int ny = y + 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 2 < maxX && y >= 4) {
//                    int nx = x + 2;
//                    int ny = y - 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 2 < maxX && y + 4 < maxY) {
//                    int nx = x + 2;
//                    int ny = y + 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 4 < maxX && y >= 2) {
//                    int nx = x + 4;
//                    int ny = y - 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 4 < maxX && y + 2 < maxY) {
//                    int nx = x + 4;
//                    int ny = y + 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                break;
//            case NORTHEAST:
//                if (x + 2 < maxX && y + 3 < maxY) {
//                    int nx = x + 2;
//                    int ny = y + 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 3 < maxX && y + 2 < maxY) {
//                    int nx = x + 3;
//                    int ny = y + 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (y + 4 < maxY) {
//                    int ny = y + 4;
//                    mapInfos[x][ny] = rc.senseMapInfo(new MapLocation(x, ny));
//                    lastUpdateTime[x][ny] = time;
//                }
//                if (x + 4 < maxX) {
//                    int nx = x + 4;
//                    mapInfos[nx][y] = rc.senseMapInfo(new MapLocation(nx, y));
//                    lastUpdateTime[nx][y] = time;
//                }
//                if (x >= 1 && y + 4 < maxY) {
//                    int nx = x - 1;
//                    int ny = y + 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 1 < maxX && y + 4 < maxY) {
//                    int nx = x + 1;
//                    int ny = y + 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 4 < maxX && y >= 1) {
//                    int nx = x + 4;
//                    int ny = y - 1;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 4 < maxX && y + 1 < maxY) {
//                    int nx = x + 4;
//                    int ny = y + 1;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 3 < maxX && y + 3 < maxY) {
//                    int nx = x + 3;
//                    int ny = y + 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 2 && y + 4 < maxY) {
//                    int nx = x - 2;
//                    int ny = y + 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 2 < maxX && y + 4 < maxY) {
//                    int nx = x + 2;
//                    int ny = y + 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 4 < maxX && y >= 2) {
//                    int nx = x + 4;
//                    int ny = y - 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 4 < maxX && y + 2 < maxY) {
//                    int nx = x + 4;
//                    int ny = y + 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                break;
//            case NORTH:
//                if (y + 4 < maxY) {
//                    int ny = y + 4;
//                    mapInfos[x][ny] = rc.senseMapInfo(new MapLocation(x, ny));
//                    lastUpdateTime[x][ny] = time;
//                }
//                if (x >= 1 && y + 4 < maxY) {
//                    int nx = x - 1;
//                    int ny = y + 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 1 < maxX && y + 4 < maxY) {
//                    int nx = x + 1;
//                    int ny = y + 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 3 && y + 3 < maxY) {
//                    int nx = x - 3;
//                    int ny = y + 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 3 < maxX && y + 3 < maxY) {
//                    int nx = x + 3;
//                    int ny = y + 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 4 && y + 2 < maxY) {
//                    int nx = x - 4;
//                    int ny = y + 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 2 && y + 4 < maxY) {
//                    int nx = x - 2;
//                    int ny = y + 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 2 < maxX && y + 4 < maxY) {
//                    int nx = x + 2;
//                    int ny = y + 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 4 < maxX && y + 2 < maxY) {
//                    int nx = x + 4;
//                    int ny = y + 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                break;
//            case NORTHWEST:
//                if (x >= 3 && y + 2 < maxY) {
//                    int nx = x - 3;
//                    int ny = y + 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 2 && y + 3 < maxY) {
//                    int nx = x - 2;
//                    int ny = y + 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 4) {
//                    int nx = x - 4;
//                    mapInfos[nx][y] = rc.senseMapInfo(new MapLocation(nx, y));
//                    lastUpdateTime[nx][y] = time;
//                }
//                if (y + 4 < maxY) {
//                    int ny = y + 4;
//                    mapInfos[x][ny] = rc.senseMapInfo(new MapLocation(x, ny));
//                    lastUpdateTime[x][ny] = time;
//                }
//                if (x >= 4 && y >= 1) {
//                    int nx = x - 4;
//                    int ny = y - 1;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 4 && y + 1 < maxY) {
//                    int nx = x - 4;
//                    int ny = y + 1;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 1 && y + 4 < maxY) {
//                    int nx = x - 1;
//                    int ny = y + 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 1 < maxX && y + 4 < maxY) {
//                    int nx = x + 1;
//                    int ny = y + 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 3 && y + 3 < maxY) {
//                    int nx = x - 3;
//                    int ny = y + 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 4 && y >= 2) {
//                    int nx = x - 4;
//                    int ny = y - 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 4 && y + 2 < maxY) {
//                    int nx = x - 4;
//                    int ny = y + 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 2 && y + 4 < maxY) {
//                    int nx = x - 2;
//                    int ny = y + 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 2 < maxX && y + 4 < maxY) {
//                    int nx = x + 2;
//                    int ny = y + 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                break;
//            case WEST:
//                if (x >= 4) {
//                    int nx = x - 4;
//                    mapInfos[nx][y] = rc.senseMapInfo(new MapLocation(nx, y));
//                    lastUpdateTime[nx][y] = time;
//                }
//                if (x >= 4 && y >= 1) {
//                    int nx = x - 4;
//                    int ny = y - 1;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 4 && y + 1 < maxY) {
//                    int nx = x - 4;
//                    int ny = y + 1;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 3 && y >= 3) {
//                    int nx = x - 3;
//                    int ny = y - 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 3 && y + 3 < maxY) {
//                    int nx = x - 3;
//                    int ny = y + 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 4 && y >= 2) {
//                    int nx = x - 4;
//                    int ny = y - 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 4 && y + 2 < maxY) {
//                    int nx = x - 4;
//                    int ny = y + 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 2 && y >= 4) {
//                    int nx = x - 2;
//                    int ny = y - 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 2 && y + 4 < maxY) {
//                    int nx = x - 2;
//                    int ny = y + 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                break;
//            case SOUTHWEST:
//                if (x >= 3 && y >= 2) {
//                    int nx = x - 3;
//                    int ny = y - 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 2 && y >= 3) {
//                    int nx = x - 2;
//                    int ny = y - 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 4) {
//                    int nx = x - 4;
//                    mapInfos[nx][y] = rc.senseMapInfo(new MapLocation(nx, y));
//                    lastUpdateTime[nx][y] = time;
//                }
//                if (y >= 4) {
//                    int ny = y - 4;
//                    mapInfos[x][ny] = rc.senseMapInfo(new MapLocation(x, ny));
//                    lastUpdateTime[x][ny] = time;
//                }
//                if (x >= 4 && y >= 1) {
//                    int nx = x - 4;
//                    int ny = y - 1;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 4 && y + 1 < maxY) {
//                    int nx = x - 4;
//                    int ny = y + 1;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 1 && y >= 4) {
//                    int nx = x - 1;
//                    int ny = y - 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 1 < maxX && y >= 4) {
//                    int nx = x + 1;
//                    int ny = y - 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 3 && y >= 3) {
//                    int nx = x - 3;
//                    int ny = y - 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 4 && y >= 2) {
//                    int nx = x - 4;
//                    int ny = y - 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 4 && y + 2 < maxY) {
//                    int nx = x - 4;
//                    int ny = y + 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 2 && y >= 4) {
//                    int nx = x - 2;
//                    int ny = y - 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 2 < maxX && y >= 4) {
//                    int nx = x + 2;
//                    int ny = y - 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                break;
//            case SOUTH:
//                if (y >= 4) {
//                    int ny = y - 4;
//                    mapInfos[x][ny] = rc.senseMapInfo(new MapLocation(x, ny));
//                    lastUpdateTime[x][ny] = time;
//                }
//                if (x >= 1 && y >= 4) {
//                    int nx = x - 1;
//                    int ny = y - 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 1 < maxX && y >= 4) {
//                    int nx = x + 1;
//                    int ny = y - 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 3 && y >= 3) {
//                    int nx = x - 3;
//                    int ny = y - 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 3 < maxX && y >= 3) {
//                    int nx = x + 3;
//                    int ny = y - 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 4 && y >= 2) {
//                    int nx = x - 4;
//                    int ny = y - 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 2 && y >= 4) {
//                    int nx = x - 2;
//                    int ny = y - 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 2 < maxX && y >= 4) {
//                    int nx = x + 2;
//                    int ny = y - 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 4 < maxX && y >= 2) {
//                    int nx = x + 4;
//                    int ny = y - 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                break;
//            case SOUTHEAST:
//                if (x + 2 < maxX && y >= 3) {
//                    int nx = x + 2;
//                    int ny = y - 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 3 < maxX && y >= 2) {
//                    int nx = x + 3;
//                    int ny = y - 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (y >= 4) {
//                    int ny = y - 4;
//                    mapInfos[x][ny] = rc.senseMapInfo(new MapLocation(x, ny));
//                    lastUpdateTime[x][ny] = time;
//                }
//                if (x + 4 < maxX) {
//                    int nx = x + 4;
//                    mapInfos[nx][y] = rc.senseMapInfo(new MapLocation(nx, y));
//                    lastUpdateTime[nx][y] = time;
//                }
//                if (x >= 1 && y >= 4) {
//                    int nx = x - 1;
//                    int ny = y - 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 1 < maxX && y >= 4) {
//                    int nx = x + 1;
//                    int ny = y - 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 4 < maxX && y >= 1) {
//                    int nx = x + 4;
//                    int ny = y - 1;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 4 < maxX && y + 1 < maxY) {
//                    int nx = x + 4;
//                    int ny = y + 1;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 3 < maxX && y >= 3) {
//                    int nx = x + 3;
//                    int ny = y - 3;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x >= 2 && y >= 4) {
//                    int nx = x - 2;
//                    int ny = y - 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 2 < maxX && y >= 4) {
//                    int nx = x + 2;
//                    int ny = y - 4;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 4 < maxX && y >= 2) {
//                    int nx = x + 4;
//                    int ny = y - 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                if (x + 4 < maxX && y + 2 < maxY) {
//                    int nx = x + 4;
//                    int ny = y + 2;
//                    mapInfos[nx][ny] = rc.senseMapInfo(new MapLocation(nx, ny));
//                    lastUpdateTime[nx][ny] = time;
//                }
//                break;
//        }
//        updateNearest(rc.senseNearbyMapInfos().length);
//        updateChange();
//    }
//
//    public static void updateNearest(int nxt) {
//        nearbyCnt = nxt;
//        int x = rc.getLocation().x;
//        int y = rc.getLocation().y;
//        if (x < 4 || y < 4 || x + 4 >= maxX || y + 4 >= maxY) {
//            nearby[--nxt] = mapInfos[x][y];
//            if (x >= 1)
//                nearby[--nxt] = mapInfos[x - 1][y];
//            if (y >= 1)
//                nearby[--nxt] = mapInfos[x][y - 1];
//            if (y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x][y + 1];
//            if (x + 1 < maxX)
//                nearby[--nxt] = mapInfos[x + 1][y];
//            if (x >= 1 && y >= 1)
//                nearby[--nxt] = mapInfos[x - 1][y - 1];
//            if (x >= 1 && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x - 1][y + 1];
//            if (x + 1 < maxX && y >= 1)
//                nearby[--nxt] = mapInfos[x + 1][y - 1];
//            if (x + 1 < maxX && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x + 1][y + 1];
//            if (x >= 2)
//                nearby[--nxt] = mapInfos[x - 2][y];
//            if (y >= 2)
//                nearby[--nxt] = mapInfos[x][y - 2];
//            if (y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x][y + 2];
//            if (x + 2 < maxX)
//                nearby[--nxt] = mapInfos[x + 2][y];
//            if (x >= 2 && y >= 1)
//                nearby[--nxt] = mapInfos[x - 2][y - 1];
//            if (x >= 2 && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x - 2][y + 1];
//            if (x >= 1 && y >= 2)
//                nearby[--nxt] = mapInfos[x - 1][y - 2];
//            if (x >= 1 && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x - 1][y + 2];
//            if (x + 1 < maxX && y >= 2)
//                nearby[--nxt] = mapInfos[x + 1][y - 2];
//            if (x + 1 < maxX && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x + 1][y + 2];
//            if (x + 2 < maxX && y >= 1)
//                nearby[--nxt] = mapInfos[x + 2][y - 1];
//            if (x + 2 < maxX && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x + 2][y + 1];
//            if (x >= 2 && y >= 2)
//                nearby[--nxt] = mapInfos[x - 2][y - 2];
//            if (x >= 2 && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x - 2][y + 2];
//            if (x + 2 < maxX && y >= 2)
//                nearby[--nxt] = mapInfos[x + 2][y - 2];
//            if (x + 2 < maxX && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x + 2][y + 2];
//            if (x >= 3)
//                nearby[--nxt] = mapInfos[x - 3][y];
//            if (y >= 3)
//                nearby[--nxt] = mapInfos[x][y - 3];
//            if (y + 3 < maxY)
//                nearby[--nxt] = mapInfos[x][y + 3];
//            if (x + 3 < maxX)
//                nearby[--nxt] = mapInfos[x + 3][y];
//            if (x >= 3 && y >= 1)
//                nearby[--nxt] = mapInfos[x - 3][y - 1];
//            if (x >= 3 && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x - 3][y + 1];
//            if (x >= 1 && y >= 3)
//                nearby[--nxt] = mapInfos[x - 1][y - 3];
//            if (x >= 1 && y + 3 < maxY)
//                nearby[--nxt] = mapInfos[x - 1][y + 3];
//            if (x + 1 < maxX && y >= 3)
//                nearby[--nxt] = mapInfos[x + 1][y - 3];
//            if (x + 1 < maxX && y + 3 < maxY)
//                nearby[--nxt] = mapInfos[x + 1][y + 3];
//            if (x + 3 < maxX && y >= 1)
//                nearby[--nxt] = mapInfos[x + 3][y - 1];
//            if (x + 3 < maxX && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x + 3][y + 1];
//            if (x >= 3 && y >= 2)
//                nearby[--nxt] = mapInfos[x - 3][y - 2];
//            if (x >= 3 && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x - 3][y + 2];
//            if (x >= 2 && y >= 3)
//                nearby[--nxt] = mapInfos[x - 2][y - 3];
//            if (x >= 2 && y + 3 < maxY)
//                nearby[--nxt] = mapInfos[x - 2][y + 3];
//            if (x + 2 < maxX && y >= 3)
//                nearby[--nxt] = mapInfos[x + 2][y - 3];
//            if (x + 2 < maxX && y + 3 < maxY)
//                nearby[--nxt] = mapInfos[x + 2][y + 3];
//            if (x + 3 < maxX && y >= 2)
//                nearby[--nxt] = mapInfos[x + 3][y - 2];
//            if (x + 3 < maxX && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x + 3][y + 2];
//            if (x >= 4)
//                nearby[--nxt] = mapInfos[x - 4][y];
//            if (y >= 4)
//                nearby[--nxt] = mapInfos[x][y - 4];
//            if (y + 4 < maxY)
//                nearby[--nxt] = mapInfos[x][y + 4];
//            if (x + 4 < maxX)
//                nearby[--nxt] = mapInfos[x + 4][y];
//            if (x >= 4 && y >= 1)
//                nearby[--nxt] = mapInfos[x - 4][y - 1];
//            if (x >= 4 && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x - 4][y + 1];
//            if (x >= 1 && y >= 4)
//                nearby[--nxt] = mapInfos[x - 1][y - 4];
//            if (x >= 1 && y + 4 < maxY)
//                nearby[--nxt] = mapInfos[x - 1][y + 4];
//            if (x + 1 < maxX && y >= 4)
//                nearby[--nxt] = mapInfos[x + 1][y - 4];
//            if (x + 1 < maxX && y + 4 < maxY)
//                nearby[--nxt] = mapInfos[x + 1][y + 4];
//            if (x + 4 < maxX && y >= 1)
//                nearby[--nxt] = mapInfos[x + 4][y - 1];
//            if (x + 4 < maxX && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x + 4][y + 1];
//            if (x >= 3 && y >= 3)
//                nearby[--nxt] = mapInfos[x - 3][y - 3];
//            if (x >= 3 && y + 3 < maxY)
//                nearby[--nxt] = mapInfos[x - 3][y + 3];
//            if (x + 3 < maxX && y >= 3)
//                nearby[--nxt] = mapInfos[x + 3][y - 3];
//            if (x + 3 < maxX && y + 3 < maxY)
//                nearby[--nxt] = mapInfos[x + 3][y + 3];
//            if (x >= 4 && y >= 2)
//                nearby[--nxt] = mapInfos[x - 4][y - 2];
//            if (x >= 4 && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x - 4][y + 2];
//            if (x >= 2 && y >= 4)
//                nearby[--nxt] = mapInfos[x - 2][y - 4];
//            if (x >= 2 && y + 4 < maxY)
//                nearby[--nxt] = mapInfos[x - 2][y + 4];
//            if (x + 2 < maxX && y >= 4)
//                nearby[--nxt] = mapInfos[x + 2][y - 4];
//            if (x + 2 < maxX && y + 4 < maxY)
//                nearby[--nxt] = mapInfos[x + 2][y + 4];
//            if (x + 4 < maxX && y >= 2)
//                nearby[--nxt] = mapInfos[x + 4][y - 2];
//            if (x + 4 < maxX && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x + 4][y + 2];
//        } else {
//            nearby[--nxt] = mapInfos[x][y];
//            nearby[--nxt] = mapInfos[x - 1][y];
//            nearby[--nxt] = mapInfos[x][y - 1];
//            nearby[--nxt] = mapInfos[x][y + 1];
//            nearby[--nxt] = mapInfos[x + 1][y];
//            nearby[--nxt] = mapInfos[x - 1][y - 1];
//            nearby[--nxt] = mapInfos[x - 1][y + 1];
//            nearby[--nxt] = mapInfos[x + 1][y - 1];
//            nearby[--nxt] = mapInfos[x + 1][y + 1];
//            nearby[--nxt] = mapInfos[x - 2][y];
//            nearby[--nxt] = mapInfos[x][y - 2];
//            nearby[--nxt] = mapInfos[x][y + 2];
//            nearby[--nxt] = mapInfos[x + 2][y];
//            nearby[--nxt] = mapInfos[x - 2][y - 1];
//            nearby[--nxt] = mapInfos[x - 2][y + 1];
//            nearby[--nxt] = mapInfos[x - 1][y - 2];
//            nearby[--nxt] = mapInfos[x - 1][y + 2];
//            nearby[--nxt] = mapInfos[x + 1][y - 2];
//            nearby[--nxt] = mapInfos[x + 1][y + 2];
//            nearby[--nxt] = mapInfos[x + 2][y - 1];
//            nearby[--nxt] = mapInfos[x + 2][y + 1];
//            nearby[--nxt] = mapInfos[x - 2][y - 2];
//            nearby[--nxt] = mapInfos[x - 2][y + 2];
//            nearby[--nxt] = mapInfos[x + 2][y - 2];
//            nearby[--nxt] = mapInfos[x + 2][y + 2];
//            nearby[--nxt] = mapInfos[x - 3][y];
//            nearby[--nxt] = mapInfos[x][y - 3];
//            nearby[--nxt] = mapInfos[x][y + 3];
//            nearby[--nxt] = mapInfos[x + 3][y];
//            nearby[--nxt] = mapInfos[x - 3][y - 1];
//            nearby[--nxt] = mapInfos[x - 3][y + 1];
//            nearby[--nxt] = mapInfos[x - 1][y - 3];
//            nearby[--nxt] = mapInfos[x - 1][y + 3];
//            nearby[--nxt] = mapInfos[x + 1][y - 3];
//            nearby[--nxt] = mapInfos[x + 1][y + 3];
//            nearby[--nxt] = mapInfos[x + 3][y - 1];
//            nearby[--nxt] = mapInfos[x + 3][y + 1];
//            nearby[--nxt] = mapInfos[x - 3][y - 2];
//            nearby[--nxt] = mapInfos[x - 3][y + 2];
//            nearby[--nxt] = mapInfos[x - 2][y - 3];
//            nearby[--nxt] = mapInfos[x - 2][y + 3];
//            nearby[--nxt] = mapInfos[x + 2][y - 3];
//            nearby[--nxt] = mapInfos[x + 2][y + 3];
//            nearby[--nxt] = mapInfos[x + 3][y - 2];
//            nearby[--nxt] = mapInfos[x + 3][y + 2];
//            nearby[--nxt] = mapInfos[x - 4][y];
//            nearby[--nxt] = mapInfos[x][y - 4];
//            nearby[--nxt] = mapInfos[x][y + 4];
//            nearby[--nxt] = mapInfos[x + 4][y];
//            nearby[--nxt] = mapInfos[x - 4][y - 1];
//            nearby[--nxt] = mapInfos[x - 4][y + 1];
//            nearby[--nxt] = mapInfos[x - 1][y - 4];
//            nearby[--nxt] = mapInfos[x - 1][y + 4];
//            nearby[--nxt] = mapInfos[x + 1][y - 4];
//            nearby[--nxt] = mapInfos[x + 1][y + 4];
//            nearby[--nxt] = mapInfos[x + 4][y - 1];
//            nearby[--nxt] = mapInfos[x + 4][y + 1];
//            nearby[--nxt] = mapInfos[x - 3][y - 3];
//            nearby[--nxt] = mapInfos[x - 3][y + 3];
//            nearby[--nxt] = mapInfos[x + 3][y - 3];
//            nearby[--nxt] = mapInfos[x + 3][y + 3];
//            nearby[--nxt] = mapInfos[x - 4][y - 2];
//            nearby[--nxt] = mapInfos[x - 4][y + 2];
//            nearby[--nxt] = mapInfos[x - 2][y - 4];
//            nearby[--nxt] = mapInfos[x - 2][y + 4];
//            nearby[--nxt] = mapInfos[x + 2][y - 4];
//            nearby[--nxt] = mapInfos[x + 2][y + 4];
//            nearby[--nxt] = mapInfos[x + 4][y - 2];
//            nearby[--nxt] = mapInfos[x + 4][y + 2];
//        }
//    }
//}
