package caterpillow.robot.agents.roaming;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.Config;

import java.util.List;
import java.util.Random;

import static caterpillow.Game.rc;
import static caterpillow.Game.seed;

// pathfinding testing
public class AggroRoamStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    Random rng;

    List<MapLocation> targets;

    public AggroRoamStrategy() throws GameActionException {
        bot = (Agent) Game.bot;
        rng = new Random(seed);
        targets = Config.getAggroTargetList(rng);
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
