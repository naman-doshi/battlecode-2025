package fix_atk_micro.robot.agents;

import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import fix_atk_micro.Game;
import static fix_atk_micro.Game.*;
import fix_atk_micro.robot.Strategy;

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
        if (!rc.isMovementReady()) return;
        if (target == null || rc.getLocation().equals(target)) {
            target = new MapLocation(rng.nextInt(mapWidth), rng.nextInt(mapHeight));
        }
        bot.pathfinder.makeMove(target);
        rc.setIndicatorLine(rc.getLocation(), target, 0, 0, 255);
    }
}
