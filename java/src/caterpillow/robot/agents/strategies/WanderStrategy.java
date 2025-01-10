package caterpillow.robot.agents.strategies;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow.Game;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;

import static caterpillow.Util.*;
import static caterpillow.Game.*;

public class WanderStrategy extends Strategy {

    Agent bot;
    MapLocation target;

    public WanderStrategy() {
        bot = (Agent) Game.bot;
        target = new MapLocation(rng.nextInt(rc.getMapWidth()), rng.nextInt(rc.getMapHeight()));
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        if (!rc.isActionReady()) return;
        rc.move(bot.pathfinder.getMove(target));
        rc.setIndicatorLine(rc.getLocation(), target, 0, 255, 0);
    }
}
