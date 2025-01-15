package caterpillow_v1.robot.agents.roaming;

import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow_v1.Config;
import caterpillow_v1.Game;
import static caterpillow_v1.Game.rc;
import static caterpillow_v1.Game.seed;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.robot.agents.Agent;

// for when u want to run straight into enemy territory
public class StrongAggroRoamStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    Random rng;

    List<MapLocation> targets;

    public StrongAggroRoamStrategy() throws GameActionException {
        bot = (Agent) Game.bot;
        //assert (Game.origin != null) : "origin is null";
        rng = new Random(seed);
        targets = Config.getEnemySpawnList(rng);
        target = targets.getFirst();
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        while (rc.canSenseLocation(target)) {
            targets.removeFirst();
            if (targets.isEmpty()) {
                targets.add(Config.genAggroTarget(rng));
            }
            target = targets.getFirst();
        }
        bot.pathfinder.makeMove(target);
        rc.setIndicatorLine(rc.getLocation(), target, 0, 0, 255);
    }
}
