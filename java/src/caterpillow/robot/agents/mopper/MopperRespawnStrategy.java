package caterpillow.robot.agents.mopper;

import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import caterpillow.robot.Strategy;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;
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
        return getNearestRobot(bot -> !isFriendly(bot)) != null;
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
            rc.move(bot.pathfinder.getMove(bot.home));
            if (rc.canCompleteTowerPattern(homeType, bot.home)) {
                bot.build(homeType, bot.home);
            }
        } else {
            // fight

        }
    }
}
