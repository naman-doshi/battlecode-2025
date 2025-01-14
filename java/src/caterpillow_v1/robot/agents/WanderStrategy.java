package caterpillow_v1.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow_v1.Game;
import caterpillow_v1.robot.Strategy;

import java.util.Random;

import static caterpillow_v1.Game.*;

// pathfinding testing
public class WanderStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    Random rng;

    public WanderStrategy() {
        bot = (Agent) Game.bot;
        rng = new Random(seed);
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        if (!rc.isActionReady()) return;
        if (target == null || rc.getLocation().equals(target)) {
            target = new MapLocation(rng.nextInt(rc.getMapWidth()), rng.nextInt(rc.getMapHeight()));
        }
        bot.pathfinder.makeMove(target);
        rc.setIndicatorLine(rc.getLocation(), target, 0, 0, 255);
    }
}
