package bugnav.pathfinding;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public abstract class AbstractPathfinder {
    public RobotController rc;
    // wtv setup u need
    public AbstractPathfinder(RobotController rc) {
        this.rc = rc;
    }

    // just assume that this move is applied and update the position for now
    // if we want more complex behaviour just do later
    public abstract Direction getMove(MapLocation to);
}
