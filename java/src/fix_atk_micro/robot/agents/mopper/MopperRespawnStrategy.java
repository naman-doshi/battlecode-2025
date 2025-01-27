package fix_atk_micro.robot.agents.mopper;

import battlecode.common.GameActionException;
import battlecode.common.UnitType;
import fix_atk_micro.Game;
import static fix_atk_micro.Game.rc;
import fix_atk_micro.pathfinding.BugnavPathfinder;
import fix_atk_micro.pathfinding.BugnavPathfinder;
import fix_atk_micro.robot.Strategy;
import static fix_atk_micro.tracking.RobotTracker.getNearestRobot;
import static fix_atk_micro.util.Util.VISION_RAD;
import static fix_atk_micro.util.Util.downgrade;
import static fix_atk_micro.util.Util.isFriendly;

// defend your home against invaders!@!!!
// bro this shit sucks
public class MopperRespawnStrategy extends Strategy {

    public Mopper bot;
    UnitType homeType;

    BugnavPathfinder pathfinder;

    public MopperRespawnStrategy() throws GameActionException {
        bot = (Mopper) Game.bot;
        homeType = downgrade(rc.senseRobotAtLocation(bot.home).getType());
        pathfinder = new BugnavPathfinder(c -> c.getMapLocation().distanceSquaredTo(bot.home) > VISION_RAD);
    }

    private boolean isInDanger() throws GameActionException {
        return getNearestRobot(bot -> !isFriendly(bot) && bot.getType().isRobotType()) != null;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return !isInDanger() && !isTowerDead();
    }

    public boolean isTowerDead() throws GameActionException {
        return rc.senseRobotAtLocation(bot.home) == null;
    }

    @Override
    public void runTick() throws GameActionException {
        if (isTowerDead()) {
            // go respawn
            pathfinder.makeMove(bot.home);
            if (rc.canCompleteTowerPattern(homeType, bot.home)) {
                bot.build(homeType, bot.home);
            }
        } else {
            //pathfinder.makeMove(target.getLocation());
            bot.doBestAttack();
        }
    }
}
