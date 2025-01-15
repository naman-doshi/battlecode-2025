package caterpillow.robot.agents.soldier;

import battlecode.common.*;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.troll.QueueStrategy;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.isTowerBeingBuilt;

import static caterpillow.util.Util.*;

public class HandleRuinStrategy extends QueueStrategy {

    MapLocation target;
    Agent bot;

    public boolean didSkip() throws GameActionException {
        if (maxedTowers()) {
            return true;
        }
        if (!rc.canSenseLocation(target.add(Direction.NORTH))) {
            return true;
        }
        if (isTowerBeingBuilt(target)) {
            return false;
        }
        RobotInfo info = rc.senseRobotAtLocation(target);
        return info == null || !isFriendly(info);
    }

    public HandleRuinStrategy(MapLocation target, UnitType type) {
        bot = (Agent) Game.bot;
        this.target = target;
        push(new BuildTowerStrategy(target, type));
        push(new TrollRuinStrategy(target));
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("HANDLING RUIN");
        super.runTick();
    }
}
