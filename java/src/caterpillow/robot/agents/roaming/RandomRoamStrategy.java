package caterpillow.robot.agents.roaming;

import caterpillow.util.CustomRandom;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.Game.seed;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;

// just walk around wtv
public class RandomRoamStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    CustomRandom rng;

    public RandomRoamStrategy() throws GameActionException {
        bot = (Agent) Game.bot;
        rng = new CustomRandom(seed);
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        while (target == null || rc.canSenseLocation(target)) {
            int x = rng.nextInt(rc.getMapWidth());
            int y = rng.nextInt(rc.getMapHeight());
            target = new MapLocation(x, y);
        }
        bot.pathfinder.makeMove(target);
        rc.setIndicatorLine(rc.getLocation(), target, 0, 0, 255);
    }
}
