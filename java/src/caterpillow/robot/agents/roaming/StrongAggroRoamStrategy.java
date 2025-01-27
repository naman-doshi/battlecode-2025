package caterpillow.robot.agents.roaming;

import java.util.List;
import caterpillow.util.CustomRandom;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow.Config;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.Game.seed;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;

// for when u want to run straight into enemy territory
public class StrongAggroRoamStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    CustomRandom rng;

    List<MapLocation> targets;

    public StrongAggroRoamStrategy() throws GameActionException {
        bot = (Agent) Game.bot;
        //assert (Game.origin != null) : "origin is null";
        rng = new CustomRandom(seed);
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
