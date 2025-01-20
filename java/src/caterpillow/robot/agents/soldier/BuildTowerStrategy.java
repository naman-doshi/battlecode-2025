package caterpillow.robot.agents.soldier;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import battlecode.common.UnitType;
import caterpillow.Config;
import static caterpillow.Config.nextTowerType;
import caterpillow.Game;
import caterpillow.pathfinding.*;

import static caterpillow.Game.rc;
import caterpillow.robot.agents.RemoveMarkerStrategy;
import caterpillow.robot.troll.QueueStrategy;
import caterpillow.tracking.CellTracker;
import caterpillow.util.Pair;
import static caterpillow.util.Util.*;
import caterpillow.util.Util;


public class BuildTowerStrategy extends QueueStrategy {

    Soldier bot;
    MapLocation target;
    UnitType readType;
    UnitType patternToFinish;
    int ticksDelayed = 0;

    final static UnitType[] poss = {UnitType.LEVEL_ONE_DEFENSE_TOWER, UnitType.LEVEL_ONE_MONEY_TOWER, UnitType.LEVEL_ONE_PAINT_TOWER};

    AbstractPathfinder pathfinder;

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

    boolean isInView() {
        if (!rc.canSenseLocation(new MapLocation(target.x - 2, target.y - 2))) return false;
        if (!rc.canSenseLocation(new MapLocation(target.x - 2, target.y + 2))) return false;
        if (!rc.canSenseLocation(new MapLocation(target.x + 2, target.y - 2))) return false;
        if (!rc.canSenseLocation(new MapLocation(target.x + 2, target.y + 2))) return false;
        return true;
    }

    boolean shouldGiveUp() throws GameActionException {
        MapLocation loc;

        loc = target.translate(-2, -2);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(-2, -1);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(-2, 0);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(-2, 1);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(-2, 2);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(-1, -2);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(-1, -1);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(-1, 0);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(-1, 1);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(-1, 2);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(0, -2);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(0, -1);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        // Skip the center location (dx == 0 && dy == 0)

        loc = target.translate(0, 1);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(0, 2);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(1, -2);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(1, -1);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(1, 0);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(1, 1);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(1, 2);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(2, -2);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(2, -1);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(2, 0);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(2, 1);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        loc = target.translate(2, 2);
        if (rc.canSenseLocation(loc) && isBlockingPattern(rc.senseMapInfo(loc))) return true;

        return false;
    }

    boolean isInDanger() {
        return rc.getPaint() <= 10;
    }

    MapLocation getMarkLocation() throws GameActionException {
        for (UnitType type : poss) {
            Direction dir = getOffset(type);
            MapLocation loc = target.add(dir);
            if (rc.canSenseLocation(loc)) {
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
            if (rc.canSenseLocation(loc)) {
                MapInfo info = rc.senseMapInfo(loc);
                if (info.getMark().equals(PaintType.ALLY_SECONDARY)) {
                    return readType = type;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    Pair<MapLocation, Boolean> getNextTile() throws GameActionException {
        UnitType type = getShownPattern();
        MapInfo best = null;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx == 0 && dy == 0) continue;
                MapInfo info = rc.senseMapInfo(new MapLocation(target.x + dx, target.y + dy));
                if ((info.getPaint().equals(PaintType.EMPTY) || info.getPaint().isSecondary() != getCellColour(target, info.getMapLocation(), type)) && !info.getPaint().isEnemy()) {
                    if (best == null || best.getMapLocation().distanceSquaredTo(rc.getLocation()) > info.getMapLocation().distanceSquaredTo(rc.getLocation())) {
                        best = info;
                    }
                }
            }
        }
        if (best == null) {
            return null;
        } else {
            return new Pair<>(best.getMapLocation(), getCellColour(target, best.getMapLocation(), type));
        }
    }

    Pair<MapLocation, Boolean> getNextTile(UnitType type) throws GameActionException {
        MapInfo best = null;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx == 0 && dy == 0) continue;
                MapInfo info = rc.senseMapInfo(new MapLocation(target.x + dx, target.y + dy));
                if ((info.getPaint().equals(PaintType.EMPTY) || info.getPaint().isSecondary() != getCellColour(target, info.getMapLocation(), type)) && !info.getPaint().isEnemy()) {
                    if (best == null || best.getMapLocation().distanceSquaredTo(rc.getLocation()) > info.getMapLocation().distanceSquaredTo(rc.getLocation())) {
                        best = info;
                    }
                }
            }
        }
        if (best == null) {
            return null;
        } else {
            return new Pair<>(best.getMapLocation(), getCellColour(target, best.getMapLocation(), type));
        }
    }

    public boolean isComplete(UnitType type) throws GameActionException {
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

    public BuildTowerStrategy(MapLocation target) {
        super();
        bot = (Soldier) Game.bot;
        this.target = target;
        readType = null;
        patternToFinish = null;
        pathfinder = new BugnavPathfinder(c -> rc.getHealth() <= 25 && Util.isInDanger(c.getMapLocation()));
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (!super.isComplete()) return false;
        if (!rc.canSenseLocation(target)) {
            return false;
        }
        if (maxedTowers() && rc.senseRobotAtLocation(target) == null) {
            return true;
        }
        if (shouldGiveUp()) {
            return true;
        }
        if (!isRuin(target)) {
            return true;
        }
        if (patternToFinish != null) {
            return shouldGiveUp();
        }
        if (!isInView()) {
            return false;
        }
        if (isTowerBeingBuilt(target)) {
            return true;
        }
        UnitType shown;
        if ((shown = getShownPattern()) != null) {
            // there is a marker
            if (isComplete(shown)) {
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
            // there is no marker
            if (readType != null) {
                return true;
            }

            // set the pattern
            MapLocation markLoc = target.add(getOffset(nextTowerType()));
            if (rc.canMark(markLoc)) {
                rc.mark(markLoc, true);
            }
            return false;
        }
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("BUILDER");
        if (!super.isComplete()) {
            super.runTick();
            return;
        }

        // im putting this up here idc anymore
        if (patternToFinish != null) {
            if (isInDanger()) {
                pathfinder.makeMove(target.add(Direction.NORTH));
                if (rc.canRemoveMark(target.add(Direction.NORTH))) {
                    rc.removeMark(target.add(Direction.NORTH));
                }
            }
            if (!isInView()) {
                pathfinder.makeMove(target);
                return;
            }
            Pair<MapLocation, Boolean> res = getNextTile(patternToFinish);
            if (res != null) {
                if (rc.canAttack(res.first)) {
                    rc.attack(res.first, res.second);
                } else {
                    pathfinder.makeMove(res.first);
                    if (rc.canAttack(res.first)) {
                        rc.attack(res.first, res.second);
                    }
                }
            } else {
                pathfinder.makeMove(target.add(Direction.NORTH));
            }
            if (rc.canCompleteTowerPattern(patternToFinish, target)) {
                if (patternToFinish == Config.nextResourceType() || ticksDelayed > 0) {
                    bot.build(patternToFinish, target);
                    push(new RemoveMarkerStrategy(target.add(Direction.NORTH)));
                    push(new RemoveMarkerStrategy(target.add(getOffset(patternToFinish))));
                    runTick();
                }
                ticksDelayed++;
            } else {
                ticksDelayed = 0;
            }
            return;
        }

        if (!isInView()) {
            pathfinder.makeMove(target);
            MapInfo nearest = CellTracker.getNearestCell(c -> c.getPaint().equals(PaintType.EMPTY) && rc.canAttack(c.getMapLocation()) && paintLevel() > 0.7);
            if (nearest != null) {
                bot.checkerboardAttack(nearest.getMapLocation());
            }
            return;
        }

        UnitType pattern = getShownPattern();
        if (pattern == null) {
            pathfinder.makeMove(target.add(getOffset(nextTowerType())));
            MapInfo nearest = CellTracker.getNearestCell(c -> c.getPaint().equals(PaintType.EMPTY) && rc.canAttack(c.getMapLocation()) && paintLevel() > 0.7 && isCellInTowerBounds(target, c.getMapLocation()));
            if (nearest != null) {
                rc.attack(nearest.getMapLocation(), getCellColour(target, nearest.getMapLocation(), nextTowerType()));
            }
            return;
        }

        Pair<MapLocation, Boolean> todo = getNextTile();
        if (todo != null) {
            if (rc.canAttack(todo.first)) {
                rc.attack(todo.first, todo.second);
            } else {
                pathfinder.makeMove(todo.first);
                if (rc.canAttack(todo.first)) {
                    rc.attack(todo.first, todo.second);
                }
            }
        } else {
            pathfinder.makeMove(target.add(Direction.NORTH));
        }
    }
}
