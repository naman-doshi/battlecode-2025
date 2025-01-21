package caterpillow.robot.agents.roaming;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow.Config;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.Game.seed;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;

public class AggroRoamStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    Random rng;
    boolean lastMove;

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
        lastMove = false;
    }

    public AggroRoamStrategy(boolean b) throws GameActionException {
        this();
        lastMove = b;
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
        bot.pathfinder.makeMove(target, lastMove);
        rc.setIndicatorLine(rc.getLocation(), target, 0, 0, 255);
    }
}
