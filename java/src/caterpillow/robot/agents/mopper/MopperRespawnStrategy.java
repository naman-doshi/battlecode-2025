package caterpillow.robot.agents.mopper;

import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.pathfinding.AbstractPathfinder;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.Strategy;
import static caterpillow.util.Util.VISION_RAD;
import static caterpillow.util.Util.downgrade;
import static caterpillow.util.Util.getNearestRobot;
import static caterpillow.util.Util.isFriendly;

// defend your home against invaders!@!!!
// bro this shit sucks
public class MopperRespawnStrategy extends Strategy {

    public Mopper bot;
    UnitType homeType;

    AbstractPathfinder pathfinder;

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
            // fight
            RobotInfo target = bot.getBestTarget();
            assert target != null;
            pathfinder.makeMove(target.getLocation());
            bot.doBestAttack(target.getLocation());
        }
    }
}
