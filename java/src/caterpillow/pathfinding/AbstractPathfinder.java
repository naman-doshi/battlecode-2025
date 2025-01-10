package caterpillow.pathfinding;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public abstract class AbstractPathfinder {
    // just assume that this move is applied and update the position for now
    // if we want more complex behaviour just do later
    public abstract Direction getMove(MapLocation to);
}
