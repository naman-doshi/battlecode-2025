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
    int target;
    AbstractPathfinder pathfinder; // custom pathfinder with different rules

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
