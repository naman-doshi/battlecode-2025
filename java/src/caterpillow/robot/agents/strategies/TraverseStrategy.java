package caterpillow.robot.agents.strategies;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

// *just* in case we optimise this in the future
public class TraverseStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    int distSquared;

    public TraverseStrategy(MapLocation target, int distSquared) {
        bot = (Agent) Game.bot;
        this.target = target;
        this.distSquared = distSquared;
    }

    @Override
    public boolean isComplete() {
        return rc.getLocation().distanceSquaredTo(target) <= distSquared;
    }

    @Override
    public void runTick() throws GameActionException {
        if (!rc.isMovementReady()) return;
        rc.move(bot.pathfinder.getMove(target));
        rc.setIndicatorLine(rc.getLocation(), target, 255, 0, 0);
    }
}
