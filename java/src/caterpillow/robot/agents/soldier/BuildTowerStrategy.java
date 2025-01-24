package caterpillow.robot.agents.soldier;

import java.util.ArrayList;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import static battlecode.common.PaintType.EMPTY;
import battlecode.common.UnitType;
import caterpillow.Config;
import static caterpillow.Config.nextTowerType;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.RemoveMarkerStrategy;
import caterpillow.tracking.CellTracker;
import caterpillow.tracking.RobotTracker;
import caterpillow.util.Pair;
import caterpillow.util.Util;
import static caterpillow.util.Util.VISION_RAD;
import static caterpillow.util.Util.getCellColour;
import static caterpillow.util.Util.hasTowerCompletionDefinitelyBeenClaimed;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.isCellInTowerBounds;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.orthDirections;
import static caterpillow.util.Util.paintLevel;
import static caterpillow.util.Util.println;
import static caterpillow.util.Util.shouldBuildTowerHere;

public class BuildTowerStrategy extends Strategy {

    Soldier bot;
    MapLocation target;
    UnitType patternToFinish;
    int ticksDelayed = 0;
    public int cellsPlaced;

    final static UnitType[] poss = {UnitType.LEVEL_ONE_DEFENSE_TOWER, UnitType.LEVEL_ONE_MONEY_TOWER, UnitType.LEVEL_ONE_PAINT_TOWER};

    private BugnavPathfinder orbitPathfinder;
    public ArrayList<MapLocation> processedRuins;

    public boolean shouldTroll(MapLocation ruin) throws GameActionException {
        if (processedRuins.contains(ruin)) {
            return false;
        }
        for (Direction dir : orthDirections) {
            MapLocation bruh = ruin.add(dir);
            if (Game.pos.distanceSquaredTo(bruh) <= VISION_RAD) {
                if (rc.senseMapInfo(bruh).getMark() != PaintType.EMPTY) {
                    processedRuins.add(ruin);
                    return false;
                }
            }
        }
        final boolean enableLateTroll = false;
        if (enableLateTroll) {
            double empty = 0, visible = 0;
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    MapLocation loc = target.translate(dx, dy);
                    if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
                        visible++;
                        PaintType paint = rc.senseMapInfo(loc).getPaint();
                        if (paint.isAlly()) {
                            processedRuins.add(ruin);
                            return false;
                        } else if (paint == PaintType.EMPTY) {
                            empty++;
                        }
                    }
                }
            }
            return empty <= 9 && empty / visible < 0.5 && visible - empty >= 1;
        } else {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    MapLocation loc = target.translate(dx, dy);
                    if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
                        PaintType paint = rc.senseMapInfo(loc).getPaint();
                        if (paint.isAlly()) {
                            processedRuins.add(ruin);
                            return false;
                        } else if (paint == PaintType.EMPTY) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }


//    public static final int[][] visitOrd = {
//            {2, 0},
//            {1, 1},
//            {0, 2},
//            {-1, 1},
//            {-2, 0},
//            {-1, -1},
//            {0, -2},
//            {1, -1}
//    };
//
//    public int getNearestCellInVisitOrd() {
//        int curX = Game.pos.x;
//        int curY = Game.pos.y;
//        int best = -1;
//        MapLocation bestLoc = null;
//        for (int i = visitOrd.length - 1; i >= 0; i--) {
//            int x = target.x + visitOrd[i][0];
//            int y = target.y + visitOrd[i][1];
//            MapLocation loc = new MapLocation(x, y);
//            if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD && rc.senseRobotAtLocation(new MapLocation(x, y)) == null) {
//                if (best == -1 && bestLoc.distanceSquaredTo(Game.pos) > loc.distanceSquaredTo(Game.pos)) {
//                    best = i;
//                    bestLoc = loc;
//                }
//            }
//        }
//        return best;
//    }

    BugnavPathfinder pathfinder;

    Direction getOffset(UnitType type) {
        switch (type) {
            case LEVEL_ONE_DEFENSE_TOWER:
                return Direction.WEST;
            case LEVEL_ONE_PAINT_TOWER:
                return Direction.EAST;
            case LEVEL_ONE_MONEY_TOWER:
                return Direction.SOUTH;
        }
        return null;
    }

    public boolean isInView() {
        int x = target.x - 2;
        int y = target.y - 2;
        if (Game.pos.distanceSquaredTo(new MapLocation(x, y)) > VISION_RAD) return false;
        y += 4;
        if (Game.pos.distanceSquaredTo(new MapLocation(x, y)) > VISION_RAD) return false;
        x += 4;
        if (Game.pos.distanceSquaredTo(new MapLocation(x, y)) > VISION_RAD) return false;
        y -= 4;
        if (Game.pos.distanceSquaredTo(new MapLocation(x, y)) > VISION_RAD) return false;
        return true;
    }

    /*

print("int x = target.x;\nx -= 2;")
print("int y = target.y;\ny -= 2;")
for i in range(-2, 3):
    for j in range(-2, 3):

        if j == -2:
            print("// row " + str(i))
        print("loc = new MapLocation(x, y);")
        print("if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {")
        print("\tMapInfo info = CellTracker.mapInfos[x][y];")
        print("\tif (info.getPaint().isEnemy() || !info.isPassable()) return true;")
        print("}")

        if j < 2:
            if i % 2 == 0:
                print("x++;")
            else:
                print("x--;")
        else:
            print("y++;")

     */

    boolean shouldGiveUp() throws GameActionException {
        MapLocation loc;
        int x = target.x;
        x -= 2;
        int y = target.y;
        y -= 2;
// row -2
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x++;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x++;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x++;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x++;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        y++;
// row -1
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x--;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x--;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x--;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x--;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        y++;
// row 0
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x++;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x++;
        x++;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x++;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        y++;
// row 1
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x--;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x--;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x--;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x--;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        y++;
// row 2
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x++;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x++;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x++;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        x++;
        loc = new MapLocation(x, y);
        if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
            MapInfo info = CellTracker.mapInfos[x][y];
            if (info.getPaint().isEnemy() || !info.isPassable()) return true;
        }
        y++;
        return false;
    }

    boolean isInDanger() throws GameActionException {
        if (rc.getPaint() == 0) return true; // wtfff
        return rc.getPaint() <= 10 && RobotTracker.getNearestRobot(c -> !isFriendly(c) && c.type.isRobotType()) != null;
    }

    MapLocation getMarkLocation() throws GameActionException {
        for (UnitType type : poss) {
            Direction dir = getOffset(type);
            MapLocation loc = target.add(dir);
            if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
                MapInfo info = rc.senseMapInfo(loc);
                if (info.getMark().equals(PaintType.ALLY_SECONDARY)) {
                    return target.add(dir);
                }
            } else {
                return null;
            }
        }
        return null;
    }

    UnitType getShownPattern() throws GameActionException {
        for (UnitType type : poss) {
            Direction dir = getOffset(type);
            MapLocation loc = target.add(dir);
            if (Game.pos.distanceSquaredTo(loc) <= VISION_RAD) {
                MapInfo info = rc.senseMapInfo(loc);
                if (info.getMark().equals(PaintType.ALLY_SECONDARY)) {
                    return type;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    /*

def add(x, dx):
    if dx > 0:
        return f"{x} + {dx}"
    elif dx < 0:
        return f"{x} - {-dx}"
    else:
        return x

print("""        MapInfo best = null, info;
        int x = target.x - 2;
        int y = target.y - 2;
        boolean[][] pattern = rc.getTowerPattern(type);""")
for dy in range(-2, 3):
    for dx in range(-2, 3):

        if dx == -2:
            print("// row " + str(dy))

        out = f"""info = CellTracker.mapInfos[x][y];
if (info.getPaint().equals(PaintType.EMPTY) || info.getPaint().isSecondary() != pattern[{dx + 2}][{dy + 2}]) {{
    if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {{
        best = info;
    }}
}}"""
        if dx != 0 or dy != 0:
            print(out)

        if dx < 2:
            if dy % 2 == 0:
                print("x++;")
            else:
                print("x--;")
        else:
            print("y++;")
print("""        if (best == null) {
            return null;
        } else {
            return new Pair<>(best.getMapLocation(), getCellColour(target, best.getMapLocation(), type));
        }""")

     */



    Pair<MapLocation, Boolean> getNextTile(UnitType type) throws GameActionException {
        MapInfo best = null, info;
        int x = target.x - 2;
        int y = target.y - 2;
        boolean[][] pattern = rc.getTowerPattern(type);
// row -2
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[0][0]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x++;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[1][0]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x++;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[2][0]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x++;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[3][0]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x++;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[4][0]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        y++;
// row -1
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[0][1]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x--;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[1][1]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x--;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[2][1]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x--;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[3][1]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x--;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[4][1]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        y++;
// row 0
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[0][2]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x++;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[1][2]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x++;
        x++;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[3][2]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x++;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[4][2]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        y++;
// row 1
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[0][3]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x--;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[1][3]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x--;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[2][3]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x--;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[3][3]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x--;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[4][3]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        y++;
// row 2
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[0][4]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x++;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[1][4]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x++;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[2][4]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x++;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[3][4]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        x++;
        info = CellTracker.mapInfos[x][y];
        if (info.getPaint().equals(EMPTY) || info.getPaint().isSecondary() != pattern[4][4]) {
            if (best == null || best.getMapLocation().distanceSquaredTo(Game.pos) > info.getMapLocation().distanceSquaredTo(Game.pos)) {
                best = info;
            }
        }
        if (best == null) {
            return null;
        } else {
            return new Pair<>(best.getMapLocation(), getCellColour(target, best.getMapLocation(), type));
        }
    }

    /*

// deprecated function for isComplete (which isnt being used)

def add(x, dx):
    if dx > 0:
        return f"{x} + {dx}"
    elif dx < 0:
        return f"{x} - {-dx}"
    else:
        return x

print("""        MapInfo info;
        int x = target.x - 2;
        int y = target.y - 2;
        boolean[][] pattern = rc.getTowerPattern(type);""")
for dy in range(-2, 3):
    for dx in range(-2, 3):

        if dx == -2:
            print("// row " + str(dy))

        out = f"""info = CellTracker.mapInfos[x][y];
if (info.getPaint().equals(PaintType.EMPTY) || info.getPaint().isSecondary() != pattern[{dx + 2}][{dy + 2}]) {{
    return false;
}}"""
        if dx != 0 or dy != 0:
            print(out)

        if dx < 2:
            if dy % 2 == 0:
                print("x++;")
            else:
                print("x--;")
        else:
            print("y++;")
print("return true;")

     */

    public BuildTowerStrategy(MapLocation target) {
        bot = (Soldier) Game.bot;
        this.target = target;
        patternToFinish = null;
        pathfinder = new BugnavPathfinder(c -> rc.getHealth() <= 25 && Util.isInDanger(c.getMapLocation()));
        orbitPathfinder = new BugnavPathfinder(c -> c.getMapLocation().distanceSquaredTo(target) <= 1);
        orbitPathfinder.alwaysLeftTurn = true;
        processedRuins = new ArrayList<>();
        processedRuins.add(target);
        cellsPlaced = 0;
    }

    // ceebs moving into constructor
    ArrayList<RemoveMarkerStrategy> marksToRemove = new ArrayList<>();
    TrollRuinStrategy troll = null;

    private boolean areMarksComplete() throws GameActionException {
        while (!marksToRemove.isEmpty()) {
            if (marksToRemove.getFirst().isComplete()) {
                marksToRemove.removeFirst();
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean tryRemoveMark() throws GameActionException {
        while (!marksToRemove.isEmpty()) {
            marksToRemove.getFirst().runTick();
            if (marksToRemove.getFirst().isComplete()) {
                marksToRemove.removeFirst();
            } else {
                break;
            }
        }
        return !areMarksComplete();
    }

    private boolean areMarkersGone() throws GameActionException {
        for (Direction dir : orthDirections) {
            if (rc.senseMapInfo(target.add(dir)).getMark() != EMPTY) {
                return false;
            }
        }
        return true;
    }

    Pair<MapLocation, Boolean> todo;

    @Override
    public boolean isComplete() throws GameActionException {
        if (patternToFinish != null) {
            indicate("COMPLETING TOWER");
        }
        if (!areMarksComplete()) {
            return false;
        }
        if (troll != null) {
            if (!troll.isComplete()) {
                return false;
            }
        }
        if (Game.pos.distanceSquaredTo(target) > VISION_RAD) {
            return false;
        }
        if (!shouldBuildTowerHere(target)) {
            println("dont build here\n");
            return true;
        }
        if (patternToFinish == null && hasTowerCompletionDefinitelyBeenClaimed(target)) {
            return true;
        }
        if (patternToFinish != null && isInDanger()) {
            return areMarkersGone();
        }
        if (shouldGiveUp() && RobotTracker.countNearbyBots(c -> isFriendly(c) && c.getType() == UnitType.MOPPER) < 2) {
            println("ff");
            if (patternToFinish != null) {
                // clean up
                marksToRemove.add(new RemoveMarkerStrategy(target.add(Direction.NORTH)));
                marksToRemove.add(new RemoveMarkerStrategy(target.add(getOffset(patternToFinish))));
            } else {
                UnitType t = getShownPattern();
                if (t != null) {
                    marksToRemove.add(new RemoveMarkerStrategy(target.add(getOffset(t))));
                }
            }
            if (!areMarksComplete()) {
                return false;
            }
            return true;
        }
//        if (patternToFinish != null) {
//            return rc.senseRobotAtLocation(target) != null;
//        }
        if (!isInView()) {
            return false;
        }
        // can be slightly optimised to start building the moment we see the pattern
        UnitType shown = getShownPattern();
        if (shown != null) {
            // there is a marker
            todo = getNextTile(shown);
            if (todo == null) {
                // already completed
                if (isInDanger()) {
                    System.out.println("ABANDONED\n");
                    return true;
                }
                // try to claim completion privileges
                MapLocation markLoc = target.add(Direction.NORTH);
                if (rc.canMark(markLoc)) {
                    rc.mark(markLoc, true);
                    patternToFinish = shown;
                }
                return false;
            } else {
                // keep building
                return false;
            }
        } else {
            // set the pattern
            UnitType nextType = nextTowerType(target);
            MapLocation markLoc = target.add(getOffset(nextType));
            if (rc.canMark(markLoc)) {
                rc.mark(markLoc, true);
                todo = getNextTile(nextType);
            }
            return false;
        }
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("BUILDER");

        if (tryRemoveMark()) return;

        // im putting this up here idc anymore
        if (patternToFinish != null) {
            if (isInDanger()) {
                marksToRemove.add(new RemoveMarkerStrategy(target.add(Direction.NORTH)));
                marksToRemove.add(new RemoveMarkerStrategy(target.add(getOffset(patternToFinish))));
                tryRemoveMark();
                return;
            }
            // how does this even happen
            if (!isInView()) {
                pathfinder.makeMove(target.add(Direction.NORTH));
                return;
            }
            Pair<MapLocation, Boolean> res = getNextTile(patternToFinish);
            if (res != null) {
                if (rc.canAttack(res.first)) {
                    rc.attack(res.first, res.second);
                    cellsPlaced++;
                    pathfinder.makeMove(target.add(Direction.NORTH));
                } else {
                    pathfinder.makeMove(res.first);
                    if (rc.canAttack(res.first)) {
                        rc.attack(res.first, res.second);
                        cellsPlaced++;
                    }
                }
            } else {
                pathfinder.makeMove(target.add(Direction.NORTH));
            }
            if (rc.canCompleteTowerPattern(patternToFinish, target)) {
                if (patternToFinish == Config.nextResourceType(true) || ticksDelayed > 0) {
                    bot.build(patternToFinish, target);
                    marksToRemove.add(new RemoveMarkerStrategy(target.add(Direction.NORTH)));
                    marksToRemove.add(new RemoveMarkerStrategy(target.add(getOffset(patternToFinish))));
                    tryRemoveMark();
                    return;
                }
                ticksDelayed++;
            } else {
                ticksDelayed = 0;
            }
            return;
        }

        // ruin trolling
        if (troll == null) {
            for (int i = CellTracker.nearbyRuins.length - 1; i >= 0; i--) {
                MapLocation ruin = CellTracker.nearbyRuins[i];
                if (shouldTroll(ruin)) {
                    troll = new TrollRuinStrategy(ruin);
                    processedRuins.add(ruin);
                    break;
                }
            }
        }
        if (tryStrategy(troll)) return;
        troll = null;

        if (!isInView()) {
            pathfinder.makeMove(target);
            if (paintLevel() > 0.7) {
                MapInfo nearest = CellTracker.getNearestCell(c -> c.getPaint().equals(EMPTY) && rc.canAttack(c.getMapLocation()));
                if (nearest != null) {
                    bot.checkerboardAttack(nearest.getMapLocation());
                }
            }
            return;
        }

        UnitType pattern = getShownPattern();
        if (pattern == null) {
            UnitType nextType = nextTowerType(target);
            pathfinder.makeMove(target.add(getOffset(nextType)));
            if (paintLevel() > 0.7) {
                MapInfo nearest = CellTracker.getNearestCell(c -> c.getPaint().equals(EMPTY) && rc.canAttack(c.getMapLocation()) && isCellInTowerBounds(target, c.getMapLocation()));
                if (nearest != null) {
                    rc.attack(nearest.getMapLocation(), getCellColour(target, nearest.getMapLocation(), nextType));
                    cellsPlaced++;
                }
            }
            return;
        }

        if (todo != null) {
//            if (rc.canAttack(todo.first)) {
//                rc.attack(todo.first, todo.second);
//                cellsPlaced++;
//            } else {
//                pathfinder.makeMove(todo.first);
//                if (rc.canAttack(todo.first)) {
//                    rc.attack(todo.first, todo.second);
//                    cellsPlaced++;
//                }
//            }
            orbitPathfinder.makeMove(target);
            todo = getNextTile(getShownPattern()); // rip optimisation
            if (rc.canAttack(todo.first)) {
                rc.attack(todo.first, todo.second);
                cellsPlaced++;
            }
        } else {
            pathfinder.makeMove(target.add(Direction.NORTH));
        }
    }
}
