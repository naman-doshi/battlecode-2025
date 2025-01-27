package fix_atk_micro.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import fix_atk_micro.Game;
import fix_atk_micro.pathfinding.BugnavPathfinder;
import fix_atk_micro.robot.Strategy;

import static fix_atk_micro.Game.*;
import static fix_atk_micro.util.Util.indicate;

// *just* in case we optimise this in the future
public class TraverseStrategy extends Strategy {

    Agent bot;
    public MapLocation target;
    int distSquared;

    public TraverseStrategy(MapLocation target, int distSquared) {
        bot = (Agent) Game.bot;
        this.target = target;
        this.distSquared = distSquared;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return rc.getLocation().distanceSquaredTo(target) <= distSquared;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("TRAVELLING TO " + target);
        if (!rc.isMovementReady()) return;
        bot.pathfinder.makeMove(target);
        rc.setIndicatorLine(rc.getLocation(), target, 255, 0, 0);
    }
}
