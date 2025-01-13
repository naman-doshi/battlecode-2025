package caterpillow.robot.agents.soldier;

import battlecode.common.*;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.util.Pair;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

/*

reinforces tower
returns once the tower is reinforced fully or if it dies

*/

public class ReinforceTowerStrategy extends Strategy {

    Agent bot;
    MapLocation target;

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

    Pair<MapLocation, Boolean> getNextTile() throws GameActionException {
        MapInfo best = null;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx == 0 && dy == 0) continue;
                MapInfo info = rc.senseMapInfo(new MapLocation(target.x + dx, target.y + dy));
                if ((info.getPaint().equals(PaintType.EMPTY) || info.getPaint().isSecondary() != getCellColour(target, info.getMapLocation(), rc.senseRobotAtLocation(target).getType())) && !info.getPaint().isEnemy()) {
                    if (best == null || best.getMapLocation().distanceSquaredTo(rc.getLocation()) > info.getMapLocation().distanceSquaredTo(rc.getLocation())) {
                        best = info;
                    }
                }
            }
        }
        if (best == null) {
            return null;
        } else {
            return new Pair<>(best.getMapLocation(), getCellColour(target, best.getMapLocation(), rc.senseRobotAtLocation(target).getType()));
        }
    }

    public ReinforceTowerStrategy(MapLocation target) {
        bot = (Agent) Game.bot;
        this.target = target;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (isInView()) {
            if (rc.senseRobotAtLocation(target) == null) {
                // bruh it died
                return true;
            }
            if (!isBuildable()) {
                // enemy tiles here already (sad stuff)
                return false;
            }
            return getNextTile() == null;
        }
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("REINFORCE");
        bot.pathfinder.makeMove(target);

        if (!isInView()) {
            return;
        }
        Pair<MapLocation, Boolean> todo = getNextTile();
        if (todo == null) {
            assert false : "this shouldnt be runnning";
        } else {
            if (rc.canAttack(todo.first)) {
                rc.attack(todo.first, todo.second);
            }
        }
    }
}
