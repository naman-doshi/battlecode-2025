package caterpillow.robot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.UnitType;

public abstract class Robot {
    protected static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    public abstract UnitType getType();

    public abstract void init(RobotController rc);

    public abstract void runTick(RobotController rc) throws GameActionException;
}
