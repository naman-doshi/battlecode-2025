package lmkaepillow.robot.agents;
import battlecode.common.UnitType;
import lmkaepillow.pathfinding.AbstractPathfinder;
import lmkaepillow.robot.Robot;

public abstract class Agent extends Robot {
    protected UnitType type;
    public AbstractPathfinder pathfinder;

    @Override
    public UnitType getType() {
        return type;
    }
}
