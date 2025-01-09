package caterpillow.pathfinding;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public abstract class AbstractPathfinder {
    public MapLocation pos;

    // wtv setup u need
    public AbstractPathfinder(RobotController rc) {
        pos = rc.getLocation();
    }

    // just assume that this move is applied and update the position for now
    // if we want more complex behaviour just do later
    public abstract Direction getMove(MapLocation to, boolean avoidFriendly, RobotController rc);

    public Direction getMove(MapLocation to, RobotController rc) {
        return getMove(to, false, rc);
    }
}
