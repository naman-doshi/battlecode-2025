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
public class RefillStrategy extends Strategy {

    public Mopper bot;
    int target;
    BugnavPathfinder pathfinder; // custom pathfinder with different rules

    public RefillStrategy(RobotInfo target) throws GameActionException {
        bot = (Mopper) Game.bot;
        this.target = target.getID();
        pathfinder = new BugnavPathfinder(c -> c.getPaint().isEnemy());
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (!rc.canSenseRobot(target)) {
            return true;
        }
        RobotInfo info = rc.senseRobot(target);
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
        RobotInfo info = rc.senseRobot(target);
        assert info != null;
        pathfinder.makeMove(info.getLocation());
    }
}
