package caterpillow.robot.agents.strategies;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

// TODO: make this smarter in the future ig?
public class LinkStrategy extends Strategy {

    Agent bot;
    MapLocation target;

    public LinkStrategy(MapLocation target) {
        bot = (Agent) Game.bot;
        this.target = target;
    }

    @Override
    public boolean isComplete() {
        return rc.canSendMessage(target);
    }

    @Override
    public void runTick() throws GameActionException {
        rc.setIndicatorString("LINKING");
        if (!rc.isActionReady()) return;
        if (rc.getLocation().distanceSquaredTo(target) == 1) {
            if (rc.canAttack(rc.getLocation())) {
                // TODO: calculate this colour properly
                rc.attack(rc.getLocation(), false);
            }
        } else {
            rc.move(bot.pathfinder.getMove(target));
            rc.setIndicatorLine(rc.getLocation(), target, 0, 255, 0);
        }
    }
}