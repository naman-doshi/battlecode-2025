package fix_atk_micro.robot.agents.roaming;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import fix_atk_micro.Config;
import fix_atk_micro.Game;
import static fix_atk_micro.Game.rc;
import static fix_atk_micro.Game.seed;
import fix_atk_micro.robot.Strategy;
import fix_atk_micro.robot.agents.Agent;

public class AggroRoamStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    Random rng;

    List<MapLocation> targets;

    public AggroRoamStrategy() throws GameActionException {
        bot = (Agent) Game.bot;
        //assert (Game.origin != null) : "origin is null";
        rng = new Random(seed);
        targets = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            targets.add(Config.genAggroTarget(rng));
        }
        targets.add(Config.getEnemySpawnList(rng).getFirst()); // fk it we go into their spawn
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
