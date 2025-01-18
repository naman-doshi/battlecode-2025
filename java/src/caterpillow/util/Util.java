package caterpillow.util;

import java.awt.image.DirectColorModel;

import battlecode.common.*;
import caterpillow.Game;
import static caterpillow.Config.genExplorationTarget;

import java.util.*;

import static caterpillow.Game.*;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Util {
    public static final int VISION_RAD = 20;
    public static final int ENC_LOC_SIZE = 12;
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

    public static final Direction[] orthDirections = {
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST
    };

    public static Direction[] relatedDirections(Direction dir) {
        if (dir == Direction.NORTH) {
            return new Direction[]{dir, Direction.NORTHEAST, Direction.NORTHWEST};
        } else if (dir == Direction.SOUTH) {
            return new Direction[]{dir, Direction.SOUTHEAST, Direction.SOUTHWEST};
        } else if (dir == Direction.EAST) {
            return new Direction[]{dir, Direction.NORTHEAST, Direction.SOUTHEAST};
        } else if (dir == Direction.WEST) {
            return new Direction[]{dir, Direction.NORTHWEST, Direction.SOUTHWEST};
        } else {
            return new Direction[]{dir};
        }
    }

    public static int writeBits(int bits, int start, int[] data, int[] lens) {
        int off = start;
        assert data.length == lens.length;
        for (int i = 0; i < data.length; i++) {
            bits = writeBits(bits, data[i], off, lens[i]);
            off += lens[i];
        }
        return bits;
    }

    public static int[] getBits(int bits, int[] segs) {
        assert segs.length > 1;
        int[] ret = new int[segs.length - 1];
        int off = segs[0];
        for (int i = 0; i < segs.length - 1; i++) {
            ret[i] = getBits(bits, off, segs[i + 1]);
            off += segs[i + 1];
        }
        return ret;
    }

    // inclusive exclusive
    public static int getBits(int bits, int l, int len) {
        int ret = 0;
        for (int i = l + len - 1; i >= l; i--) {
            ret <<= 1;
            ret += (1 & (bits >>> i));
        }
        return ret;
    }

    public static int writeBits(int bits, int data, int st, int len) {
        for (int i = 0; i < len; i++) {
            // clear
            bits &= ~(1 << (st + i));
            bits |= (1 & (data >>> i)) << (st + i);
        }
        return bits;
    }

    public final static int TOWER_COST = 1000;

    public static MapLocation flipHor(MapLocation loc) {
        return new MapLocation(rc.getMapWidth() - 1 - loc.x, loc.y);
    }

    public static MapLocation flipVer(MapLocation loc) {
        return new MapLocation(loc.x, rc.getMapHeight() - 1 - loc.y);
    }

    public static MapLocation rot180(MapLocation loc) {
        return new MapLocation(rc.getMapWidth() - 1 - loc.x, rc.getMapHeight() - 1 - loc.y);
    }

    // for debugging purposes
    public static void indicate(String str) {
        rc.setIndicatorString(str);
    }

    public static int manhattan(MapLocation a, MapLocation b) {
        return abs(a.x - b.x) + abs(a.y - b.y);
    }

    public static boolean getCellColour(MapLocation centre, MapLocation loc, UnitType type) throws GameActionException {
        MapLocation ind = add(subtract(loc, centre), new MapLocation(2, 2));
        return rc.getTowerPattern(type)[ind.x][ind.y];
    }

    final static int SIZE = 100;
    public static MapLocation decodeLoc(int code) {
        // assert code < rc.getMapWidth() * SIZE : code + " is not a valid map code " + rc.getID();
        return new MapLocation(code / SIZE, code % SIZE);
    }

    public static int encodeLoc(MapLocation loc) {
        // assert 0 <= loc.x && loc.x < SIZE && 0 <= loc.y && loc.y < SIZE;
        return loc.x * SIZE + loc.y;
    }

    public static void dead(Object msg) {
        println("Robot " + rc.getID() + " hit a dead end: " + msg);
        rc.disintegrate();
    }

    public static boolean isCellInTowerBounds(MapLocation tower, MapLocation loc) {
        return abs(tower.x - loc.x) <= 2 && abs(tower.y - loc.y) <= 2;
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

    public static List<MapLocation> guessEnemyLocs(MapLocation src) throws GameActionException {
        List<MapLocation> enemyLocs = new LinkedList<>();

        if (src == null) {
            // give 3 random locations
            for (int i = 0; i < 3; i++) {
                enemyLocs.addLast(genExplorationTarget(trng));
            }
            return enemyLocs;
        }


        // also make the path as easy as possible between targets
        int dist_hormiddle = Math.abs(src.x - Game.rc.getMapWidth() / 2);
        int dist_vertmiddle = Math.abs(src.y - Game.rc.getMapHeight() / 2);
        if (dist_hormiddle > dist_vertmiddle) {
            // first is hor
            enemyLocs.addLast(flipHor(src));

            int d1 = flipHor(src).distanceSquaredTo(rot180(src)) + rot180(src).distanceSquaredTo(flipVer(src));
            int d2 = flipHor(src).distanceSquaredTo(flipVer(src)) + flipVer(src).distanceSquaredTo(rot180(src));

            if (d1 < d2) {
                enemyLocs.addLast(rot180(src));
                enemyLocs.addLast(flipVer(src));
            } else {
                enemyLocs.addLast(flipVer(src));
                enemyLocs.addLast(rot180(src));
            }

        } else if (dist_hormiddle < dist_vertmiddle) {
            // first is vert ref
            enemyLocs.addLast(flipVer(src));

            int d1 = flipVer(src).distanceSquaredTo(rot180(src)) + rot180(src).distanceSquaredTo(flipHor(src));
            int d2 = flipVer(src).distanceSquaredTo(flipHor(src)) + flipHor(src).distanceSquaredTo(rot180(src));

            if (d1 < d2) {
                enemyLocs.addLast(rot180(src));
                enemyLocs.addLast(flipHor(src));
            } else {
                enemyLocs.addLast(flipHor(src));
                enemyLocs.addLast(rot180(src));
            }

        } else {
            // first is rot 180
            enemyLocs.addLast(rot180(src));

            int d1 = rot180(src).distanceSquaredTo(flipHor(src)) + flipHor(src).distanceSquaredTo(flipVer(src));
            int d2 = rot180(src).distanceSquaredTo(flipVer(src)) + flipVer(src).distanceSquaredTo(flipHor(src));

            if (d1 < d2) {
                enemyLocs.addLast(flipHor(src));
                enemyLocs.addLast(flipVer(src));
            } else {
                enemyLocs.addLast(flipVer(src));
                enemyLocs.addLast(flipHor(src));
            }
            
        }
        return enemyLocs;
    }

    public static int expectedRushDistance(MapLocation src) throws GameActionException {
        List<MapLocation> enemyLocs = guessEnemyLocs(src);
        MapLocation target = enemyLocs.get(0);
        return (int)Math.sqrt((double)src.distanceSquaredTo(target));
    }

    public static List<MapLocation> guessSpawnLocs() throws GameActionException {
        return guessEnemyLocs(origin);
    }

    public static UnitType downgrade(UnitType type) {
        if (type.isRobotType()) {
            assert false : "not a tower";
            return type;
        }
        return switch (type) {
            case LEVEL_ONE_PAINT_TOWER, LEVEL_TWO_PAINT_TOWER, LEVEL_THREE_PAINT_TOWER ->
                    UnitType.LEVEL_ONE_PAINT_TOWER;
            case LEVEL_ONE_MONEY_TOWER, LEVEL_TWO_MONEY_TOWER, LEVEL_THREE_MONEY_TOWER ->
                    UnitType.LEVEL_ONE_MONEY_TOWER;
            case LEVEL_ONE_DEFENSE_TOWER, LEVEL_TWO_DEFENSE_TOWER, LEVEL_THREE_DEFENSE_TOWER ->
                    UnitType.LEVEL_ONE_DEFENSE_TOWER;
            default -> null;
        };
    }

    public static MapInfo getBestCell(GameBinaryOperator<MapInfo> comp, GamePredicate<MapInfo> pred) throws GameActionException {
        MapInfo best = null;
        MapInfo[] infos = rc.senseNearbyMapInfos();
        for (int i = infos.length - 1; i >= 0; i--) {
            MapInfo cell = infos[i];
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
        RobotInfo[] bots = rc.senseNearbyRobots();
        for (int i = bots.length - 1; i >= 0; i--) {
            RobotInfo bot = bots[i];
            if (pred.test(bot)) {
                if (best == null || best.getLocation().distanceSquaredTo(rc.getLocation()) > bot.location.distanceSquaredTo(rc.getLocation())) {
                    best = bot;
                }
            }
        }
        return best;
    }

    public static int countNearbyMoppers(MapLocation loc) throws GameActionException {
        int cnt = 0;
        for (Direction dir : directions) {
            MapLocation nloc = loc.add(dir);
            if (rc.canSenseLocation(nloc)) {
                RobotInfo botInfo = rc.senseRobotAtLocation(nloc);
                if (botInfo != null) {
                    if (isFriendly(botInfo) && botInfo.getType().equals(UnitType.MOPPER)) {
                        cnt++;
                    }
                }
            }
        }
        return cnt;
    }

    public static boolean isPaintable(MapInfo info) {
        return info.getPaint().equals(PaintType.EMPTY) && info.isPassable();
    }

    public static boolean isBlockingPattern(MapInfo info) {
        return info.getPaint().isEnemy() || !info.isPassable();
    }

    public static MapInfo getNearestCell(GamePredicate<MapInfo> pred) throws GameActionException {
        MapInfo best = null;
        MapInfo[] cells = rc.senseNearbyMapInfos();
        for (int i = cells.length - 1; i >= 0; i--) {
            MapInfo cell = cells[i];
            if (pred.test(cell)) {
                if (best == null || best.getMapLocation().distanceSquaredTo(rc.getLocation()) > cell.getMapLocation().distanceSquaredTo(rc.getLocation())) {
                    best = cell;
                }
            }
        }
        return best;
    }

    public static MapInfo getNearestCell(GamePredicate<MapInfo> pred, int rad) throws GameActionException {
        MapInfo best = null;
        MapInfo[] cells = rc.senseNearbyMapInfos(rad);
        for (int i = cells.length - 1; i >= 0; i--) {
            MapInfo cell = cells[i];
            if (pred.test(cell)) {
                if (best == null || best.getMapLocation().distanceSquaredTo(rc.getLocation()) > cell.getMapLocation().distanceSquaredTo(rc.getLocation())) {
                    best = cell;
                }
            }
        }
        return best;
    }

    public static double getPaintLevel(RobotInfo bot) {
        return (double) bot.getPaintAmount() / (double) bot.getType().paintCapacity;
    }

    public static MapInfo cheapGetNearestCell(GamePredicate<MapInfo> pred) throws GameActionException {
        MapInfo best = null;
        MapInfo[] cells = rc.senseNearbyMapInfos();
        // sort by distance to rc.getLocation()
        Arrays.sort(cells, Comparator.comparingInt(a -> a.getMapLocation().distanceSquaredTo(rc.getLocation())));
        for (MapInfo cell : cells) {
            if (pred.test(cell)) {
                return cell;
            }
        }
        return null;
    }

    public static boolean isAllyAgent(RobotInfo bot) {
        return isFriendly(bot) && bot.getType().isRobotType();
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

    public static boolean canMove(MapLocation loc) throws GameActionException {
        Direction dir = getDiff(rc.getLocation(), loc);
        return rc.canMove(dir);
    }

    public static Direction getClosestDirTo(MapLocation dest, GamePredicate<MapInfo> pred) throws GameActionException {
        Direction best = null;
        for (Direction dir : directions) {
            if (dir == Direction.CENTER) continue;
            MapLocation newLoc = rc.getLocation().add(dir);
            if (rc.onTheMap(newLoc)) {
                MapInfo newInfo = rc.senseMapInfo(newLoc);
                if (pred.test(newInfo)) {
                    if (best == null || rc.getLocation().add(best).distanceSquaredTo(dest) > newLoc.distanceSquaredTo(dest)) {
                        best = dir;
                    }
                }
            }
        }
        return best;
    }

    public static int missingPaint(RobotInfo b) {
        return b.getType().paintCapacity - b.getPaintAmount();
    }
    public static int missingPaint() { return rc.getType().paintCapacity - rc.getPaint(); }

    public static Direction getDiff(MapLocation src, MapLocation dest) throws GameActionException {
        int dx = dest.x - src.x;
        int dy = dest.y - src.y;
        for (Direction dir : directions) {
            if (dir.getDeltaX() == dx && dir.getDeltaY() == dy) {
                return dir;
            }
        }
        return null;
    }

    public static boolean isInAttackRange(MapLocation loc) {
        return loc.distanceSquaredTo(rc.getLocation()) <= rc.getType().actionRadiusSquared;
    }

    public static boolean isEnemyAgent(RobotInfo info) {
        return info.getType().isRobotType() && !isFriendly(info);
    }

    public static boolean isVisiblyWithinRuin(MapLocation loc) {
        for (MapInfo ruin : rc.senseNearbyMapInfos()) {
            if (ruin.hasRuin()) {
                if (isCellInTowerBounds(ruin.getMapLocation(), loc)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int paintPriority(PaintType type) {
        if (type.isAlly()) return 2;
        if (type.equals(PaintType.EMPTY)) return 1;
        return 0;
    }

    public static MapInfo getSpawnLoc(UnitType type) throws GameActionException {
        return getBestCell((MapInfo c1, MapInfo c2) -> {
            int p1 = paintPriority(c1.getPaint());
            int p2 = paintPriority(c2.getPaint());
            if (p1 != p2) {
                if (p1 > p2) return c1;
                else return c2;
            }
            if (c1.getMapLocation().distanceSquaredTo(centre) < c2.getMapLocation().distanceSquaredTo(centre)) {
                return c1;
            } else {
                return c2;
            }
        }, c -> {
            return rc.canBuildRobot(type, c.getMapLocation());
        });
    }

    public static boolean maxedTowers() {
        return rc.getNumberTowers() == 25;
    }

    public static MapInfo getSafeSpawnLoc(UnitType type) throws GameActionException {
        return getBestCell((MapInfo c1, MapInfo c2) -> {
            int p1 = paintPriority(c1.getPaint());
            int p2 = paintPriority(c2.getPaint());
            if (p1 != p2) {
                if (p1 > p2) return c1;
                else return c2;
            }
            if (c1.getMapLocation().distanceSquaredTo(centre) < c2.getMapLocation().distanceSquaredTo(centre)) {
                return c1;
            } else {
                return c2;
            }
        }, c -> {
            return rc.canBuildRobot(type, c.getMapLocation()) && !c.getPaint().isEnemy();
        });
    }

    public static void println(Object obj) {
        if (time > 200) return;
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

    public static boolean isPatternComplete(MapLocation target, UnitType type) throws GameActionException {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx == 0 && dy == 0) continue;
                MapInfo info = rc.senseMapInfo(new MapLocation(target.x + dx, target.y + dy));
                if ((info.getPaint().equals(PaintType.EMPTY) || info.getPaint().isSecondary() != getCellColour(target, info.getMapLocation(), type)) && !info.getPaint().isEnemy()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isPaintBelowHalf(RobotInfo bot) {
        return bot.getPaintAmount() <= bot.getType().paintCapacity / 2;
    }

    public static boolean isPaintBelowHalf() {
        return rc.getPaint() <= rc.getType().paintCapacity / 2;
    }

    public static double paintLevel() {
        return (double) rc.getPaint() / (double) rc.getType().paintCapacity;
    }

    public static MapLocation project(MapLocation cur, MapLocation moveVec, double maxDist) {
        double dx = moveVec.x;
        double dy = moveVec.y;

        double magnitude = Math.sqrt(dx * dx + dy * dy);
        dx /= magnitude;
        dy /= magnitude;

        int mapWidth = rc.getMapWidth();
        int mapHeight = rc.getMapHeight();

        double tMin = maxDist;

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

    public static MapLocation project(MapLocation cur, MapLocation moveVec) {
        return project(cur, moveVec, Double.MAX_VALUE);
    }


    public static boolean isTowerBeingBuilt(MapLocation target) throws GameActionException {
        if (rc.canSenseLocation(target.add(Direction.NORTH))) {
            MapInfo info = rc.senseMapInfo(target.add(Direction.NORTH));
            return !info.getMark().equals(PaintType.EMPTY);
        } else return false;
    }

    public static MapLocation getOpposite(MapLocation cur) {
        MapLocation centre = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
        int dx = centre.x - cur.x;
        int dy = centre.y - cur.y;
        return project(cur, new MapLocation(dx, dy));
    }

    public static boolean isSRPCenter(MapLocation loc) {
        return (loc.x%4==2 && loc.y%4==2);
    }
    
    public static boolean isWithinRuin(MapLocation loc, MapLocation ruin) {
        return loc.isWithinDistanceSquared(ruin, 8) || (loc.distanceSquaredTo(ruin) == 9 && !(loc.x == ruin.x || loc.y == ruin.y));
    }

    public static List<MapLocation> findNearestSRPCenters(MapLocation loc) {
        // cancer wtf
        List<MapLocation> res = new ArrayList<>();

        // x = 4n + 2
        // y = 4m + 2
        int n = (loc.x - 2) / 4;
        for (int possibleN = n-1; possibleN <= n+1; possibleN++) {
            int m = (loc.y - 2) / 4;
            for (int possibleM = m-1; possibleM <= m+2; possibleM++) {
                int x = 4*n + 2;
                int y = 4*m + 2;
                if (isSRPCenter(new MapLocation(x, y))) {
                    res.add(new MapLocation(x, y));
                }
            }
        }

        // sort by distance
        res.sort(Comparator.comparingInt(a -> loc.distanceSquaredTo(a)));
        return res;
        
    }

    public static PaintType checkerboardPaint(MapLocation loc) {
        
        switch ((loc.x) % 4) {
            case (0):
                return (loc.y%4==2) ? PaintType.ALLY_PRIMARY : PaintType.ALLY_SECONDARY;
            case (1):
                return (loc.y%4!=0) ? PaintType.ALLY_PRIMARY : PaintType.ALLY_SECONDARY;
            case (2):
                return (loc.y%4!=2) ? PaintType.ALLY_PRIMARY : PaintType.ALLY_SECONDARY;
            case (3):
                return (loc.y%4!=0) ? PaintType.ALLY_PRIMARY : PaintType.ALLY_SECONDARY;
            default:
                return PaintType.ALLY_PRIMARY;
        }
    }

    public static List<Integer> getSRPIds(MapLocation loc) {
        // since one cell can belong to multiple SRPs, up to 3, we need to return a list
        List<MapLocation> centers = findNearestSRPCenters(loc);
        List<Integer> res = new ArrayList<>();
        for (MapLocation center : centers) {
            // this includes only the square, so it's very accurate
            if (isWithinRuin(loc, center)) {
                res.add(encodeLoc(center));
            }
        }
        return res;

    }

    public static MapInfo getNeighbourSpawnLoc(UnitType type) throws GameActionException {
        return getBestCell((MapInfo c1, MapInfo c2) -> {
            int p1 = paintPriority(c1.getPaint());
            int p2 = paintPriority(c2.getPaint());
            if (p1 != p2) {
                if (p1 > p2) return c1;
                else return c2;
            }
            if (c1.getMapLocation().distanceSquaredTo(centre) < c2.getMapLocation().distanceSquaredTo(centre)) {
                return c1;
            } else {
                return c2;
            }
        }, c -> {
            return rc.canBuildRobot(type, c.getMapLocation()) && !c.getPaint().isEnemy() && c.getMapLocation().distanceSquaredTo(rc.getLocation()) == 1;
        });
    }
}
