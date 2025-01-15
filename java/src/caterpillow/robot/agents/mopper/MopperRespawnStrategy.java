package caterpillow.robot.agents.mopper;

import battlecode.common.*;
import caterpillow.Game;
import caterpillow.pathfinding.AbstractPathfinder;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.Strategy;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;
import static java.lang.Math.min;

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
            bot.doBestAttack();
        }
    }
}
