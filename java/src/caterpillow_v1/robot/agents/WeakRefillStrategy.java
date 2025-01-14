package caterpillow_v1.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow_v1.Game;
import caterpillow_v1.robot.Strategy;

import static caterpillow_v1.Game.*;
import static caterpillow_v1.util.Util.*;
import static java.lang.Math.max;

/*

will go towards target location, trying to refill from there
will refill as much as possible, or deem not worth

*/
public class WeakRefillStrategy extends Strategy {

    MapLocation target;
    Agent bot;
    double minRefillMul;
    boolean hasRefilled;

    int minRefill() {
        return (int) ((double) rc.getType().paintCapacity * minRefillMul);
    }

    public WeakRefillStrategy(MapLocation target, double minRefillMul) {
        this.target = target;
        bot = (Agent) Game.bot;
        this.minRefillMul = minRefillMul;
        hasRefilled = false;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (hasRefilled) {
            return true;
        }
        if (rc.canSenseLocation(target)) {
            RobotInfo bot = rc.senseRobotAtLocation(target);
            if (bot == null || !bot.getType().isTowerType() || !isFriendly(bot)) {
                // give up
                return true;
            }
            if (bot.getPaintAmount() < minRefill() && !downgrade(bot.getType()).equals(UnitType.LEVEL_ONE_PAINT_TOWER)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("REFILLING");
        bot.pathfinder.makeMove(target);
        if (rc.canSenseLocation(target)) {
            bot.refill(rc.senseRobotAtLocation(target));
            hasRefilled = true;
        }
    }
}
