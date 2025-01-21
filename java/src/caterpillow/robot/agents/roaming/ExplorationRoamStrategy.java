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

// try to increase chances of finding new area
public class ExplorationRoamStrategy extends Strategy {
    Agent bot;
    public MapLocation target;
    Random rng;

    public ExplorationRoamStrategy() throws GameActionException {
        bot = (Agent) Game.bot;
        rng = new Random(seed);
    }
    public ExplorationRoamStrategy(boolean b) throws GameActionException {
        this();
    }
    public ExplorationRoamStrategy(MapLocation target) throws GameActionException {
        this();
        this.target = target;
    }
    public ExplorationRoamStrategy(MapLocation target, boolean b) throws GameActionException {
        this(target);
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
