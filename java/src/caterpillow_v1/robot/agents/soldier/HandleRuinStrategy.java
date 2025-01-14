package caterpillow_v1.robot.agents.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow_v1.Game;
import static caterpillow_v1.Game.rc;
import caterpillow_v1.robot.agents.Agent;
import caterpillow_v1.robot.troll.QueueStrategy;
import static caterpillow_v1.util.Util.isFriendly;
import static caterpillow_v1.util.Util.isTowerBeingBuilt;

import static caterpillow_v1.util.Util.*;

public class HandleRuinStrategy extends QueueStrategy {

    MapLocation target;
    Agent bot;

    public boolean didSkip() throws GameActionException {
        if (maxedTowers()) {
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
