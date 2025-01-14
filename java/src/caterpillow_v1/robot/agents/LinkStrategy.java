package caterpillow_v1.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow_v1.Game;
import caterpillow_v1.robot.Strategy;

import static caterpillow_v1.Game.*;
import static caterpillow_v1.util.Util.*;

// TODO: make this smarter in the future ig?
public class LinkStrategy extends Strategy {

    Agent bot;
    MapLocation target;

    public LinkStrategy(MapLocation target) {
        assert target != null;
        bot = (Agent) Game.bot;
        this.target = target;
    }

    @Override
    public boolean isComplete() {
        return rc.canSendMessage(target);
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("LINKING");
        bot.pathfinder.makeMove(target);
        rc.setIndicatorLine(rc.getLocation(), target, 0, 255, 0);
        if (!rc.isActionReady()) return;
        if (rc.getLocation().distanceSquaredTo(target) == 1) {
            if (rc.canAttack(rc.getLocation())) {
                // TODO: calculate this colour properly
                rc.attack(rc.getLocation(), false);
            }
        }
    }
}