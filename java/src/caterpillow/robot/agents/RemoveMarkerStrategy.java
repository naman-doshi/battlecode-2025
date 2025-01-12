package caterpillow.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import caterpillow.Game;
import caterpillow.robot.Strategy;

import static caterpillow.Game.*;

// *just* in case we optimise this in the future
public class RemoveMarkerStrategy extends Strategy {

    Agent bot;
    MapLocation target;

    public RemoveMarkerStrategy(MapLocation target) {
        bot = (Agent) Game.bot;
        this.target = target;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (rc.canSenseLocation(target)) {
            if (rc.senseMapInfo(target).getMark().equals(PaintType.EMPTY)) {
                return true;
            }
            if (rc.canRemoveMark(target)) {
                rc.removeMark(target);
                return true;
            }
        }
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        if (!rc.isMovementReady()) return;
        rc.move(bot.pathfinder.getMove(target));
        rc.setIndicatorLine(rc.getLocation(), target, 255, 0, 0);
    }
}
