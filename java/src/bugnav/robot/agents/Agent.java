package bugnav.robot.agents;

import battlecode.common.UnitType;
import battlecode.common.RobotController;
import bugnav.pathfinding.AbstractPathfinder;
import bugnav.pathfinding.BugnavPathfinder;
import bugnav.robot.Robot;

public abstract class Agent extends Robot {
    protected UnitType type;
    public AbstractPathfinder pathfinder;

    @Override
    public void init(RobotController rc) {
        super.init(rc);
        pathfinder = new BugnavPathfinder(rc);
    }

    @Override
    public UnitType getType() {
        return type;
    }
}
