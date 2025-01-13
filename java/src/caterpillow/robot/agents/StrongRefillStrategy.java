package caterpillow.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import caterpillow.robot.Strategy;

import static caterpillow.Game.*;
import static caterpillow.util.Util.*;
import static java.lang.Math.max;

/*

will go towards target location, trying to refill from there
returns once refilled or deemed impossible
will wait if necessary

*/
public class StrongRefillStrategy extends Strategy {

    MapLocation target;
    Agent bot;
    double minAmt;

    int req() {
        return max(0, (int) ((double) rc.getType().paintCapacity * minAmt) - rc.getPaint());
    }

    public StrongRefillStrategy(MapLocation target, double minAmt) {
        this.target = target;
        bot = (Agent) Game.bot;
        this.minAmt = minAmt;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (req() == 0) return true;
        if (rc.canSenseLocation(target)) {
            RobotInfo bot = rc.senseRobotAtLocation(target);
            if (bot == null || !bot.getType().isTowerType() || !isFriendly(bot)) {
                // give up
                return true;
            }
            if (bot.getPaintAmount() < req() && !downgrade(bot.getType()).equals(UnitType.LEVEL_ONE_PAINT_TOWER)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        bot.pathfinder.makeMove(target);
        if (rc.canSenseLocation(target)) {
            bot.refill(rc.senseRobotAtLocation(target));
        }
    }
}
