package fix_atk_micro.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import fix_atk_micro.Game;
import fix_atk_micro.robot.Strategy;
import fix_atk_micro.tracking.TowerTracker;

import static fix_atk_micro.Game.*;
import static fix_atk_micro.util.Util.*;
import static java.lang.Math.max;

/*

will go towards target location, trying to refill from there
will refill as much as possible, or deem not worth

*/
public class WeakRefillStrategy extends Strategy {

    MapLocation target;
    Agent bot;
    // minimum amount of paint to refill
    double minRefillMul;
    boolean hasRefilled;
    int minSkipAmt;

    int minRefill() {
        return (int) ((double) rc.getType().paintCapacity * minRefillMul);
    }

    public WeakRefillStrategy(double minRefillMul) {
        bot = (Agent) Game.bot;
        this.minRefillMul = minRefillMul;
        hasRefilled = false;
        minSkipAmt = rc.getPaint() + minRefill();
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (hasRefilled || rc.getPaint() >= minSkipAmt) {
            return true;
        }
        RobotInfo res = TowerTracker.getNearestVisibleTower(b -> isFriendly(b) && b.getPaintAmount() >= minRefill());
        if (res == null) {
            return true;
        }
        target = res.getLocation();
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("REFILLING");
        bot.pathfinder.makeMove(target);
        if (rc.canSenseLocation(target) && rc.senseRobotAtLocation(target) != null && rc.canTransferPaint(target, -1)) {
            bot.refill(rc.senseRobotAtLocation(target));
            hasRefilled = true;
        }
    }
}
