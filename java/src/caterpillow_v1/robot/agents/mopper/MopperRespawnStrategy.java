package caterpillow_v1.robot.agents.mopper;

import battlecode.common.*;
import caterpillow_v1.Game;
import caterpillow_v1.robot.Strategy;

import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;
import static java.lang.Math.min;

// defend your home against invaders!@!!!
public class MopperRespawnStrategy extends Strategy {

    public Mopper bot;
    UnitType homeType;

    public MopperRespawnStrategy() throws GameActionException {
        bot = (Mopper) Game.bot;
        homeType = downgrade(rc.senseRobotAtLocation(bot.home).getType());
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
            bot.pathfinder.makeMove(bot.home);
            if (rc.canCompleteTowerPattern(homeType, bot.home)) {
                bot.build(homeType, bot.home);
            }
        } else {
            // fight
            RobotInfo target = bot.getBestTarget();
            assert target != null;
            Direction dir = getClosestDirTo(target.getLocation(), c -> isCellInTowerBounds(bot.home, c.getMapLocation()) && canMove(c.getMapLocation()));
            if (dir != null) {
                rc.move(dir);
            }
            bot.doBestAttack();
        }
    }
}
