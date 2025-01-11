package caterpillow.util;

import battlecode.common.*;

import java.util.*;

import static caterpillow.Game.*;
import static java.lang.Math.abs;

public class Util {
    public static final int VISION_RAD = 20;
    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    public static MapLocation flipHor(MapLocation loc) {
        return new MapLocation(rc.getMapWidth() - 1 - loc.x, loc.y);
    }

    public static MapLocation flipVer(MapLocation loc) {
        return new MapLocation(loc.x, rc.getMapHeight() - 1 - loc.y);
    }

    public static MapLocation rot180(MapLocation loc) {
        return new MapLocation(rc.getMapWidth() - 1 - loc.x, rc.getMapHeight() - 1 - loc.y);
    }

    public static boolean getMoneyTowerColour(MapLocation centre, MapLocation loc) throws GameActionException {
        MapLocation ind = add(subtract(loc, centre), new MapLocation(2, 2));
        return rc.getTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER)[ind.x][ind.y];
    }

    public static int manhattan(MapLocation a, MapLocation b) {
        return abs(a.x - b.x) + abs(a.y - b.y);
    }

    public static boolean getPaintTowerColour(MapLocation centre, MapLocation loc) throws GameActionException {
        MapLocation ind = add(subtract(loc, centre), new MapLocation(2, 2));
        return rc.getTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER)[ind.x][ind.y];
    }

    public static boolean getDefenseTowerColour(MapLocation centre, MapLocation loc) throws GameActionException {
        MapLocation ind = add(subtract(loc, centre), new MapLocation(2, 2));
        return rc.getTowerPattern(UnitType.LEVEL_ONE_DEFENSE_TOWER)[ind.x][ind.y];
    }

    public static boolean getCellColour(MapLocation centre, MapLocation loc, UnitType type) throws GameActionException {
        switch (type) {
            case LEVEL_ONE_MONEY_TOWER:
                return getMoneyTowerColour(centre, loc);
            case LEVEL_ONE_PAINT_TOWER:
                return getPaintTowerColour(centre, loc);
            case LEVEL_ONE_DEFENSE_TOWER:
                return getDefenseTowerColour(centre, loc);
        }
        assert false;
        return false;
    }

    final static int SIZE = 60;
    public static MapLocation decodeLoc(int code) {
        assert code < rc.getMapWidth() * SIZE : code + " is not a valid map code " + rc.getID();
        return new MapLocation(code / SIZE, code % SIZE);
    }

    public static int encodeLoc(MapLocation loc) {
        assert 0 <= loc.x && loc.x < SIZE && 0 <= loc.y && loc.y < SIZE;
        return loc.x * SIZE + loc.y;
    }

    public static void dead(Object msg) {
        println("Robot " + rc.getID() + " hit a dead end: " + msg);
        rc.disintegrate();
    }

    public static Pair<Double, Double> relativeDistsToCentre(MapLocation loc) {
        double relX = (double) loc.x / (double) rc.getMapWidth();
        double relY = (double) loc.y / (double) rc.getMapHeight();
        return new Pair(abs(relX - 0.5), abs(relY - 0.5));
    }

    public static RobotInfo getBestRobot(GameBinaryOperator<RobotInfo> comp, GamePredicate<RobotInfo> pred) throws GameActionException {
        RobotInfo best = null;
        for (RobotInfo bot : rc.senseNearbyRobots()) {
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

    public static MapInfo getBestCell(GameBinaryOperator<MapInfo> comp, GamePredicate<MapInfo> pred) throws GameActionException {
        MapInfo best = null;
        for (MapInfo cell : rc.senseNearbyMapInfos()) {
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

    public static RobotInfo getNearestRobot(GamePredicate<RobotInfo> pred) throws GameActionException {
        RobotInfo best = null;
        for (RobotInfo bot : rc.senseNearbyRobots()) {
            if (pred.test(bot)) {
                if (best == null || best.getLocation().distanceSquaredTo(rc.getLocation()) > bot.location.distanceSquaredTo(rc.getLocation())) {
                    best = bot;
                }
            }
        }
        return best;
    }

    public static MapInfo getNearestCell(GamePredicate<MapInfo> pred) throws GameActionException {
        MapInfo best = null;
        for (MapInfo cell : rc.senseNearbyMapInfos()) {
            if (pred.test(cell)) {
                if (best == null || best.getMapLocation().distanceSquaredTo(rc.getLocation()) > cell.getMapLocation().distanceSquaredTo(rc.getLocation())) {
                    best = cell;
                }
            }
        }
        return best;
    }

    public static MapInfo getClosestNeighbourTo(MapLocation dest, GamePredicate<MapInfo> pred) throws GameActionException {
        MapInfo best = null;
        for (Direction dir : directions) {
            if (dir == Direction.CENTER) continue;
            MapLocation newLoc = rc.getLocation().add(dir);
            MapInfo newInfo = rc.senseMapInfo(newLoc);
            if (pred.test(newInfo)) {
                if (best == null || best.getMapLocation().distanceSquaredTo(dest) > newLoc.distanceSquaredTo(dest)) {
                    best = newInfo;
                }
            }
        }
        return best;
    }

    public static void println(Object obj) {
        if (time > 100) return;
        System.out.println(obj);
    }

    public static MapInfo getClosestCellTo(MapLocation dest, GamePredicate<MapInfo> pred) throws GameActionException {
        MapInfo best = null;
        for (MapInfo newLoc : rc.senseNearbyMapInfos()) {
            if (pred.test(newLoc)) {
                if (best == null || best.getMapLocation().distanceSquaredTo(dest) > newLoc.getMapLocation().distanceSquaredTo(dest)) {
                    best = newLoc;
                }
            }
        }
        return best;
    }

    public static int countNearbyBots(GamePredicate<RobotInfo> pred) throws GameActionException {
        int res = 0;
        for (RobotInfo bot : rc.senseNearbyRobots()) {
            if (pred.test(bot)) {
                res++;
            }
        }
        return res;
    }

    public static MapLocation add(MapLocation a, MapLocation b) {
        return new MapLocation(a.x + b.x, a.y + b.y);
    }

    public static MapLocation subtract(MapLocation a, MapLocation b) {
        return new MapLocation(a.x - b.x, a.y - b.y);
    }

    public static MapLocation rotr(MapLocation vec) {
        return new MapLocation(vec.y, -vec.x);
    }

    public static MapLocation rotl(MapLocation vec) {
        return new MapLocation(-vec.y, vec.x);
    }

    public static MapLocation scale(MapLocation vec, double amt) {
        return new MapLocation((int) (vec.x * amt), (int) (vec.y * amt));
    }

    public static boolean isFriendly(RobotInfo info) {
        return rc.getTeam().equals(info.getTeam());
    }

    public static boolean connectedByPaint(MapLocation src, MapLocation dest, boolean includeDest) throws GameActionException {
        if (!rc.senseMapInfo(src).getPaint().isAlly()) return false;
        Queue<MapLocation> q = new LinkedList<MapLocation>();
        Set<MapLocation> seen = new HashSet<MapLocation>();
        q.add(src);
        MapLocation cur;
        int[] dx = {1, 0, -1, 0}, dy = {0, 1, 0, -1};
        while (!q.isEmpty()) {
            cur = q.remove();
            if (!rc.canSenseLocation(cur)) continue;
            if (seen.contains(cur)) continue;
            if (!includeDest && cur.equals(dest)) return true;
            if (!rc.senseMapInfo(cur).getPaint().isAlly()) continue;
            if (cur.equals(dest)) return true;
            seen.add(cur);
            for (int i = 0; i < 4; i++) {
                MapLocation nxt = new MapLocation(cur.x + dx[i], cur.y + dy[i]);
                if (rc.onTheMap(nxt)) {
                    q.add(nxt);
                }
            }
        }
        return false;
    }

    public static MapLocation project(MapLocation cur, MapLocation moveVec) {
        double dx = moveVec.x;
        double dy = moveVec.y;

        int mapWidth = rc.getMapWidth();
        int mapHeight = rc.getMapHeight();

        double tMin = Double.MAX_VALUE;

        if (dx < 0) {
            double t = -cur.x / dx;
            if (t >= 0) tMin = Math.min(tMin, t);
        }

        if (dx > 0) {
            double t = (mapWidth - 1 - cur.x) / dx;
            if (t >= 0) tMin = Math.min(tMin, t);
        }

        if (dy < 0) {
            double t = -cur.y / dy;
            if (t >= 0) tMin = Math.min(tMin, t);
        }

        if (dy > 0) {
            double t = (mapHeight - 1 - cur.y) / dy;
            if (t >= 0) tMin = Math.min(tMin, t);
        }

        int x = (int) Math.round(cur.x + tMin * dx);
        int y = (int) Math.round(cur.y + tMin * dy);

        x = Math.max(0, Math.min(mapWidth - 1, x));
        y = Math.max(0, Math.min(mapHeight - 1, y));

        return new MapLocation(x, y);
    }

    public static MapLocation getOpposite(MapLocation cur) {
        MapLocation centre = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
        int dx = centre.x - cur.x;
        int dy = centre.y - cur.y;
        return project(cur, new MapLocation(dx, dy));
    }
}
