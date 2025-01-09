package caterpillow.robot.agents;

import battlecode.common.UnitType;
import caterpillow.pathfinding.AbstractPathfinder;
import caterpillow.robot.Robot;

public abstract class Agent extends Robot {
    protected UnitType type;
    public AbstractPathfinder pathfinder;

    @Override
    public UnitType getType() {
        return type;
    }
}
