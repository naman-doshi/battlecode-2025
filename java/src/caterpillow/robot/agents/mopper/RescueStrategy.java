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
public class RescueStrategy extends Strategy {

    public Mopper bot;
    MapLocation target;
    AbstractPathfinder pathfinder; // custom pathfinder with different rules

    public RescueStrategy(MapLocation target) throws GameActionException {
        bot = (Mopper) Game.bot;
        this.target = target;
        // anythnig goes
        pathfinder = new BugnavPathfinder();
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
        if (!Config.shouldRescue(info)) {
            return true;
        }
        if (bot.donate(info) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("RESCUE");
        pathfinder.makeMove(target);
    }
}
