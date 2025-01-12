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

    public Strategy primaryStrategy;
    public Strategy secondaryStrategy;

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
        RobotInfo parent = getNearestRobot(info -> info.getType().isTowerType() && isFriendly(info));
        if (parent != null) {
            setParent(parent);
        } else {
            // rip parent died
            // tiny edge case where your spawning tower dies at the same time you spawn
            MapInfo ruin = getNearestCell(c -> c.hasRuin());
            UnitType type = nextTowerType();
            if (rc.canCompleteTowerPattern(type, ruin.getMapLocation())) {
                rc.completeTowerPattern(type, ruin.getMapLocation());
                setParent(rc.senseRobotAtLocation(ruin.getMapLocation()));
            } else {
                // there is this stupid edge case where you get spawned, and then the tower dies before it can send over a message
                origin = ruin.getMapLocation();
                home = ruin.getMapLocation();
            }
        }
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
}
