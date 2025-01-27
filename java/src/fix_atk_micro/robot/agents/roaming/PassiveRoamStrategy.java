package fix_atk_micro.robot.agents.roaming;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import fix_atk_micro.Game;
import fix_atk_micro.robot.Strategy;
import fix_atk_micro.robot.agents.Agent;
import fix_atk_micro.Config;

import java.util.List;
import java.util.Random;

import static fix_atk_micro.Game.rc;
import static fix_atk_micro.Game.seed;

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
