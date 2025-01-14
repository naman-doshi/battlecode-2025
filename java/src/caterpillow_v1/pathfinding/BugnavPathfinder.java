package caterpillow_v1.pathfinding;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

import static caterpillow_v1.Game.*;

public class BugnavPathfinder extends AbstractPathfinder {

    public MapLocation target;
    public Direction bottomDir;
    public Direction topDir;
    public int stackSize;
    public boolean leftTurn = false;

    @Override
    public Direction getMove(MapLocation to) {
        if (rc.getLocation().equals(to)) {
            return null;
        }
        if (target == null || !target.equals(to)) {
            target = to;
            stackSize = 0;
            topDir = bottomDir = rc.getLocation().directionTo(target);
        }
        if (leftTurn) {
            if (!rc.getLocation().directionTo(target).equals(bottomDir)) {
                if (rc.getLocation().directionTo(target).equals(bottomDir.rotateRight())) {
                    stackSize++;
                    bottomDir = bottomDir.rotateRight();
                } else if (rc.getLocation().directionTo(target).equals(bottomDir.rotateLeft())) {
                    stackSize--;
                    bottomDir = bottomDir.rotateLeft();
                    if (stackSize < 0) {
                        stackSize = 0;
                        topDir = bottomDir;
                    }
                } else {
                    stackSize = 0;
                    topDir = bottomDir = rc.getLocation().directionTo(target);
                }
            }
            if (stackSize >= 2 && rc.canMove(topDir.rotateRight().rotateRight())) {
                stackSize -= 2;
                topDir = topDir.rotateRight().rotateRight();
                return topDir;
            }
            if (stackSize >= 1 && rc.canMove(topDir.rotateRight())) {
                stackSize--;
                topDir = topDir.rotateRight();
                return topDir;
            }
        } else {
            if (!rc.getLocation().directionTo(target).equals(bottomDir)) {
                if (rc.getLocation().directionTo(target).equals(bottomDir.rotateLeft())) {
                    stackSize++;
                    bottomDir = bottomDir.rotateLeft();
                } else if (rc.getLocation().directionTo(target).equals(bottomDir.rotateRight())) {
                    stackSize--;
                    bottomDir = bottomDir.rotateRight();
                    if (stackSize < 0) {
                        stackSize = 0;
                        topDir = bottomDir;
                    }
                } else {
                    stackSize = 0;
                    topDir = bottomDir = rc.getLocation().directionTo(target);
                }
            }
            if (stackSize >= 2 && rc.canMove(topDir.rotateLeft().rotateLeft())) {
                stackSize -= 2;
                topDir = topDir.rotateLeft().rotateLeft();
                return topDir;
            }
            if (stackSize >= 1 && rc.canMove(topDir.rotateLeft())) {
                stackSize--;
                topDir = topDir.rotateLeft();
                return topDir;
            }
        }
        int iters = 0;
        while(!rc.canMove(topDir)) {
            if (stackSize == 0) {
                if (rc.canMove(topDir.rotateRight())) leftTurn = false;
                else if (rc.canMove(topDir.rotateLeft())) leftTurn = true;
                else if (rc.canMove(topDir.rotateRight().rotateRight())) leftTurn = false;
                else if (rc.canMove(topDir.rotateLeft().rotateLeft())) leftTurn = true;
                else leftTurn = trng.nextInt(0, 1) == 1; // change later
            }
            if (leftTurn) topDir = topDir.rotateLeft();
            else topDir = topDir.rotateRight();
            stackSize++;
            iters++;
            if (iters == 8) {
            }
        }
        assert rc.canMove(topDir);
        return topDir;
    }

    @Override
    public void makeMove(MapLocation to) throws GameActionException {
        if (rc.isMovementReady()) {
            Direction dir = getMove(to);
            if (dir != null && rc.canMove(dir)) {
                rc.move(dir);
            }
        }
    }
}
