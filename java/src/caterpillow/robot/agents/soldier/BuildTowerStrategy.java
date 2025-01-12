package caterpillow.robot.agents.soldier;

import battlecode.common.*;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.util.Pair;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

/*

the person who first is unable to find a new tile to paint (cuz its finished) is the person who finishes the tower
YOU MUST CALL ISCOMPLETE BEFORE RUNTICK ON THIS STRATEGY

*/

public class BuildTowerStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    UnitType typePref;
    UnitType patternToFinish;

    final static UnitType[] poss = {UnitType.LEVEL_ONE_DEFENSE_TOWER, UnitType.LEVEL_ONE_MONEY_TOWER, UnitType.LEVEL_ONE_PAINT_TOWER};

    Direction getOffset(UnitType type) {
        switch (type) {
            case LEVEL_ONE_DEFENSE_TOWER:
                return Direction.NORTH;
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

    boolean isBuildable() throws GameActionException {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                MapLocation loc = new MapLocation(target.x + dx, target.y + dy);
                if (rc.senseMapInfo(loc).getPaint().isEnemy()) {
                    return false;
                }
            }
        }
        return true;
    }

    UnitType getShownPattern() throws GameActionException {
        for (UnitType type : poss) {
            Direction dir = getOffset(type);
            MapLocation loc = target.add(dir);
            if (rc.canSenseLocation(loc)) {
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

    Pair<MapLocation, Boolean> getNextTile() throws GameActionException {
        UnitType type = getShownPattern();
        MapInfo best = null;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx == 0 && dy == 0) continue;
                MapInfo info = rc.senseMapInfo(new MapLocation(target.x + dx, target.y + dy));
                if (info.getPaint().equals(PaintType.EMPTY) || info.getPaint().isSecondary() != getCellColour(target, info.getMapLocation(), type)) {
                    if (best == null || best.getMapLocation().distanceSquaredTo(rc.getLocation()) > info.getMapLocation().distanceSquaredTo(rc.getLocation())) {
                        best = info;
                    }
                }
            }
        }
        if (best == null) {
            return new Pair<>(null, null);
        } else {
            return new Pair<>(best.getMapLocation(), getCellColour(target, best.getMapLocation(), type));
        }
    }

    public BuildTowerStrategy(MapLocation target, UnitType typePref) {
        bot = (Agent) Game.bot;
        this.target = target;
        this.typePref = typePref;
        patternToFinish = null;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (isInView()) {
            if (rc.senseRobotAtLocation(target) != null) {
                // tower already therer
                println("return cuz robot there");
                return true;
            }
            if (!isBuildable()) {
                // cant build so give up
                return true;
            }
            Pair<MapLocation, Boolean> next = getNextTile();
            if (next.first == null) { // no more building needed
                MapLocation markLoc = target.add(getOffset(getShownPattern())); // this is the stupidest line of code ever
                if (!rc.senseMapInfo(markLoc).getMark().equals(PaintType.EMPTY)) { // mark is still there
                    println("set bot to finisher");
                    patternToFinish = getShownPattern();
                    assert rc.canRemoveMark(markLoc);
                    rc.removeMark(markLoc);
                }
                // finished pattern
                return patternToFinish == null;
            }
        }
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        rc.setIndicatorString("BUILDER");
        // go home if if its run out of things to do (which is unlikely since itll probably die first)
        if (rc.isMovementReady()) {
            rc.move(bot.pathfinder.getMove(target));
        }

        // im putting this up here idc anymore
        if (patternToFinish != null) {
            if (rc.canCompleteTowerPattern(patternToFinish, target)) {
                bot.build(patternToFinish, target);
            }
            return;
        }

//        println("is in view " + isInView());
        if (!isInView()) {
            return;
        }
        if (getShownPattern() == null) {
//            println("shown pattern is null");
            // set the pattern
            MapLocation markLoc = target.add(getOffset(typePref));
//            println(markLoc);
//            println("dist " + markLoc.distanceSquaredTo(rc.getLocation()));
            if (rc.canMark(markLoc)) {
//                println("mark at " + markLoc);
                rc.mark(markLoc, true);
            }
        }
        UnitType pattern = getShownPattern();
        println(pattern);
        if (pattern == null) {
            return;
        }
        Pair<MapLocation, Boolean> todo = getNextTile();
        println(todo.first + " " + todo.second);
        if (todo.first == null) {
            assert false : "this shouldnt be runnning";
        } else {
            if (rc.canAttack(todo.first)) {
                rc.attack(todo.first, todo.second);
            }
        }
    }
}
