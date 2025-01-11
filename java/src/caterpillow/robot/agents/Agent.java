package caterpillow.robot.agents;

import battlecode.common.*;
import caterpillow.packet.packets.OriginPacket;
import caterpillow.pathfinding.AbstractPathfinder;
import caterpillow.robot.Robot;
import caterpillow.robot.Strategy;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public abstract class Agent extends Robot {
    protected UnitType type;
    public AbstractPathfinder pathfinder;
    public MapLocation home;
    public int homeID;

    Strategy primaryStrategy;
    Strategy secondaryStrategy;

    public void build(UnitType type, MapLocation loc) throws GameActionException{
        rc.completeTowerPattern(type, loc);
        pm.send(loc, new OriginPacket(origin));
    }

    public void setParent(RobotInfo parent) {
        home = parent.getLocation();
        homeID = parent.getID();
    }

    @Override
    public void init() throws GameActionException {
        // set home
        RobotInfo parent = getNearestRobot(info -> info.getType().isTowerType() && info.getTeam().isPlayer());
        assert parent != null;
        setParent(parent);
    }

    @Override
    public void runTick() throws GameActionException {
        if (secondaryStrategy != null) {
            if (secondaryStrategy.isComplete()) {
                secondaryStrategy = null;
            }
        }
        if (secondaryStrategy != null) {
            secondaryStrategy.runTick();
        } else {
            if (primaryStrategy.isComplete()) {
                dead("primary strategy completed");
                return;
            }
            primaryStrategy.runTick();
        }
    }

    @Override
    public UnitType getType() {
        return type;
    }
}
