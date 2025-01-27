package fix_atk_micro.robot.agents.roaming;

import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import fix_atk_micro.Game;
import static fix_atk_micro.Game.rc;
import static fix_atk_micro.Game.seed;
import fix_atk_micro.robot.Strategy;
import fix_atk_micro.robot.agents.Agent;

// just walk around wtv
public class RandomRoamStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    Random rng;

    public RandomRoamStrategy() throws GameActionException {
        bot = (Agent) Game.bot;
        rng = new Random(seed);
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
