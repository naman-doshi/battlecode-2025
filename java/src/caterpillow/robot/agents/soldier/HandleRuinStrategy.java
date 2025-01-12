package caterpillow.robot.agents.soldier;

import battlecode.common.*;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.troll.QueueStrategy;
import caterpillow.util.Pair;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class HandleRuinStrategy extends QueueStrategy {

    MapLocation target;
    Agent bot;

    public boolean didSkip() throws GameActionException {
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
        rc.setIndicatorString("HANDLING RUIN");
        super.runTick();
    }
}
