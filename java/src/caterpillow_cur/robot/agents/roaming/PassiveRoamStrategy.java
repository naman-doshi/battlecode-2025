package caterpillow_cur.robot.agents.roaming;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow_cur.Game;
import caterpillow_cur.robot.Strategy;
import caterpillow_cur.robot.agents.Agent;
import caterpillow_cur.Config;

import java.util.List;
import java.util.Random;

import static caterpillow_cur.Game.rc;
import static caterpillow_cur.Game.seed;

// just walk around wtv
public class PassiveRoamStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    Random rng;

    public PassiveRoamStrategy() throws GameActionException {
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
