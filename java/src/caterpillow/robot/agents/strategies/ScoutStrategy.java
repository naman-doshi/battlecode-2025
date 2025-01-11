package caterpillow.robot.agents.strategies;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;

import static caterpillow.Game.*;

// TODO
public class ScoutStrategy extends Strategy {

    Agent bot;
    MapLocation target;

    public ScoutStrategy() {
        bot = (Agent) Game.bot;
//        target = new MapLocation(rng.nextInt(rc.getMapWidth()), rng.nextInt(rc.getMapHeight()));
//        target = findOpposingLoc(rc.getLocation());
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        if (!rc.isActionReady()) return;
        rc.move(bot.pathfinder.getMove(target));
        rc.setIndicatorLine(rc.getLocation(), target, 0, 0, 0);
    }
}
