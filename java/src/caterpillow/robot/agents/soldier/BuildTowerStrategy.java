package caterpillow.robot.agents.soldier;

import battlecode.common.*;
import caterpillow.Config;
import caterpillow.Game;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.RemoveMarkerStrategy;
import caterpillow.robot.troll.QueueStrategy;
import caterpillow.util.Pair;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

/*

YOU MUST CALL ISCOMPLETE BEFORE RUNTICK ON THIS STRATEGY

bro this is so cooked, idek anymore
ive like bandage fixed at least 10 things

ceebs removing marks, its the tower's job to do that now

*/

public class BuildTowerStrategy extends QueueStrategy {

    Soldier bot;
    MapLocation target;
    UnitType typePref;
    UnitType readType;
    UnitType patternToFinish;
    int ticksDelayed = 0;

    final static UnitType[] poss = {UnitType.LEVEL_ONE_DEFENSE_TOWER, UnitType.LEVEL_ONE_MONEY_TOWER, UnitType.LEVEL_ONE_PAINT_TOWER};

    Direction getOffset(UnitType type) {
        switch (type) {
            case LEVEL_ONE_DEFENSE_TOWER:
                return Direction.WEST;
            case LEVEL_ONE_PAINT_TOWER:
                return Direction.EAST;
            case LEVEL_ONE_MONEY_TOWER:
                return Direction.SOUTH;
        }
        assert false;
        return null;
    }

    boolean isInView() {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (!rc.canSenseLocation(new MapLocation(target.x + dx, target.y + dy))) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean shouldGiveUp() throws GameActionException {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx == 0 || dy == 0) {
                    continue;
                }
                MapLocation loc = target.translate(dx, dy);
                if (rc.canSenseLocation(loc)) {
                    if (isBlockingPattern(rc.senseMapInfo(loc))) {
                        return true;
                    }
                }
            }
        }
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


//    public boolean isAlreadyBuilt() throws GameActionException {
//        for (UnitType type : poss) {
//            if (isPatternComplete(target, type)) {
//                return true;
//            }
//        }
//        return false;
//    }

    public BuildTowerStrategy(MapLocation target, UnitType typePref) {
        super();
        bot = (Soldier) Game.bot;
        this.target = target;
        this.typePref = typePref;
        readType = null;
        patternToFinish = null;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (!super.isComplete()) return false;
        if (maxedTowers()) {
            return true;
        }
        if (shouldGiveUp()) {
            return true;
        }
        if (!rc.canSenseLocation(target)) {
            return false;
        }
        if (rc.senseRobotAtLocation(target) != null) {
            return true;
        }
        if (patternToFinish != null) {
            if (shouldGiveUp()) {
                return true;
            }
            return false;
        }
        if (!isInView()) {
            return false;
        }
        if (isTowerBeingBuilt(target)) {
            return true;
        }
        if (getShownPattern() != null) {
            // there is a marker
            Pair<MapLocation, Boolean> next = getNextTile();
            if (next == null) {
                // already completed
                if (isInDanger()) {
                    System.out.println("ABANDONED\n");
                    return true;
                }
                // try to claim completion privileges
                MapLocation markLoc = target.add(Direction.NORTH);
                if (rc.canMark(markLoc)) {
                    rc.mark(markLoc, true);
                    patternToFinish = getShownPattern();
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
            MapLocation markLoc = target.add(getOffset(typePref));
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

        // go home if if its run out of things to do (which is unlikely since itll probably die first)
        bot.pathfinder.makeMove(target);

        // im putting this up here idc anymore
        if (patternToFinish != null) {
            if (isInDanger()) {
                bot.pathfinder.makeMove(target.add(Direction.NORTH));
                if (rc.canRemoveMark(target.add(Direction.NORTH))) {
                    rc.removeMark(target.add(Direction.NORTH));
                }
                return;
            }
            if (!isInView()) {
                bot.pathfinder.makeMove(target);
                return;
            }
            Pair<MapLocation, Boolean> res = getNextTile(patternToFinish);
            if (res != null) {
                if (rc.canAttack(res.first)) {
                    rc.attack(res.first, res.second);
                }
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
            MapInfo nearest = getNearestCell(c -> c.getPaint().equals(PaintType.EMPTY) && rc.canAttack(c.getMapLocation()) && paintLevel() > 0.7);
            if (nearest != null) {
                bot.checkerboardAttack(nearest.getMapLocation());
            }
            return;
        }

        UnitType pattern = getShownPattern();
        if (pattern == null) {
            MapInfo nearest = getNearestCell(c -> c.getPaint().equals(PaintType.EMPTY) && rc.canAttack(c.getMapLocation()) && paintLevel() > 0.7 && isCellInTowerBounds(target, c.getMapLocation()));
            if (nearest != null) {
                rc.attack(nearest.getMapLocation(), getCellColour(target, nearest.getMapLocation(), typePref));
            }
            return;
        }

        Pair<MapLocation, Boolean> todo = getNextTile();
        if (todo != null) {
            if (rc.canAttack(todo.first)) {
                rc.attack(todo.first, todo.second);
            }
        }
    }
}
