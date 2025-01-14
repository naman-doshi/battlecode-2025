package caterpillow.robot.agents.mopper;

import battlecode.common.*;
import caterpillow.Config;
import caterpillow.Game;
import caterpillow.pathfinding.AbstractPathfinder;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.roaming.PassiveRoamStrategy;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;
import static java.lang.Math.min;

// save dying bots
public class RefillStrategy extends Strategy {

    public Mopper bot;
    MapLocation target;
    AbstractPathfinder pathfinder; // custom pathfinder with different rules

    public RefillStrategy(MapLocation target) throws GameActionException {
        bot = (Mopper) Game.bot;
        this.target = target;
        pathfinder = new BugnavPathfinder(c -> c.getPaint().isEnemy());
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (!rc.canSenseLocation(target)) {
            return false;
        }
        RobotInfo info = rc.senseRobotAtLocation(target);
        if (info == null) {
            return true;
        }
        if (!Config.shouldRefill(info)) {
            return true;
        }
        if (bot.donate(info) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("REFILL");
        pathfinder.makeMove(target);
    }
}
