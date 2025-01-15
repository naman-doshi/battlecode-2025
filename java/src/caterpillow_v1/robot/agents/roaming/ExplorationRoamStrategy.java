package caterpillow_v1.robot.agents.roaming;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow_v1.Game;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.robot.agents.Agent;
import caterpillow_v1.Config;

import java.util.List;
import java.util.Random;

import static caterpillow_v1.Game.rc;
import static caterpillow_v1.Game.seed;

// try to increase chances of finding new area
public class ExplorationRoamStrategy extends Strategy {
    Agent bot;
    MapLocation target;
    Random rng;

    List<MapLocation> targets;

    public ExplorationRoamStrategy() throws GameActionException {
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
            target = Config.genPassiveTarget(rng);
        }
        bot.pathfinder.makeMove(target);
        rc.setIndicatorLine(rc.getLocation(), target, 0, 0, 255);
    }
}
