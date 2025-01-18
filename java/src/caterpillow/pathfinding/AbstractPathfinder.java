package caterpillow.pathfinding;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

public abstract class AbstractPathfinder {
    // just assume that this move is applied and update the position for now
    // if we want more complex behaviour just do later
    public abstract Direction getMove(MapLocation to) throws GameActionException;
    public abstract Direction makeMove(MapLocation to) throws GameActionException;
    public abstract void makeMove(Direction dir) throws GameActionException;
    public abstract void reset();
}
