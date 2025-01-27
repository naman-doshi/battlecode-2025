package fix_atk_micro.robot.agents.mopper;

import battlecode.common.*;
import fix_atk_micro.Config;
import fix_atk_micro.Game;
import fix_atk_micro.pathfinding.BugnavPathfinder;
import fix_atk_micro.pathfinding.BugnavPathfinder;
import fix_atk_micro.robot.Strategy;
import fix_atk_micro.robot.agents.roaming.PassiveRoamStrategy;

import static fix_atk_micro.util.Util.*;
import static fix_atk_micro.Game.*;
import static java.lang.Math.min;

// save dying bots
public class RescueStrategy extends Strategy {

    public Mopper bot;
    MapLocation target;
    BugnavPathfinder pathfinder; // custom pathfinder with different rules

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
