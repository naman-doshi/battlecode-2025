package caterpillow.robot.agents;

import battlecode.common.*;
import battlecode.schema.RobotType;
import caterpillow.packet.packets.AdoptionPacket;
import caterpillow.pathfinding.AbstractPathfinder;
import caterpillow.robot.Robot;
import caterpillow.robot.Strategy;

import static caterpillow.Util.*;
import static caterpillow.Game.*;

public abstract class Agent extends Robot {
    protected UnitType type;
    public AbstractPathfinder pathfinder;
    public MapLocation home;

    Strategy primaryStrategy;
    Strategy secondaryStrategy;

    @Override
    public void init() throws GameActionException {

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
            primaryStrategy.runTick();
        }
    }

    @Override
    public UnitType getType() {
        return type;
    }
}
