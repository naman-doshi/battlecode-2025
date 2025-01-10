package bugnav.pathfinding;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import static bugnav.Util.*;

public class BugnavPathfinder extends AbstractPathfinder {
    public BugnavPathfinder(RobotController rc) {
        super(rc);
    }
    public MapLocation target;
    public Direction bottomDir;
    public Direction topDir;
    public int stackSize;
    public boolean leftTurn = false;

    @Override
    public Direction getMove(MapLocation to) {
        if(target == null || !target.equals(to)) {
            target = to;
            stackSize = 0;
            topDir = bottomDir = rc.getLocation().directionTo(target);
        }
        if(leftTurn) {
            if(!rc.getLocation().directionTo(target).equals(bottomDir)) {
                if(rc.getLocation().directionTo(target).equals(bottomDir.rotateRight())) {
                    stackSize++;
                    bottomDir = bottomDir.rotateRight();
                } else if(rc.getLocation().directionTo(target).equals(bottomDir.rotateLeft())) {
                    stackSize--;
                    bottomDir = bottomDir.rotateLeft();
                    if(stackSize < 0) {
                        stackSize = 0;
                        topDir = bottomDir;
                    }
                } else {
                    stackSize = 0;
                    topDir = bottomDir = rc.getLocation().directionTo(target);
                }
            }
            if(stackSize >= 2 && rc.canMove(topDir.rotateRight().rotateRight())) {
                stackSize -= 2;
                topDir = topDir.rotateRight().rotateRight();
                return topDir;
            }
            if(stackSize >= 1 && rc.canMove(topDir.rotateRight())) {
                stackSize--;
                topDir = topDir.rotateRight();
                return topDir;
            }
        } else {
            if(!rc.getLocation().directionTo(target).equals(bottomDir)) {
                if(rc.getLocation().directionTo(target).equals(bottomDir.rotateLeft())) {
                    stackSize++;
                    bottomDir = bottomDir.rotateLeft();
                } else if(rc.getLocation().directionTo(target).equals(bottomDir.rotateRight())) {
                    stackSize--;
                    bottomDir = bottomDir.rotateRight();
                    if(stackSize < 0) {
                        stackSize = 0;
                        topDir = bottomDir;
                    }
                } else {
                    stackSize = 0;
                    topDir = bottomDir = rc.getLocation().directionTo(target);
                }
            }
            if(stackSize >= 2 && rc.canMove(topDir.rotateLeft().rotateLeft())) {
                stackSize -= 2;
                topDir = topDir.rotateLeft().rotateLeft();
                return topDir;
            }
            if(stackSize >= 1 && rc.canMove(topDir.rotateLeft())) {
                stackSize--;
                topDir = topDir.rotateLeft();
                return topDir;
            }
        }
        int iters = 0;
        while(!rc.canMove(topDir)) {
            if(stackSize == 0) {
                if(rc.canMove(topDir.rotateRight())) leftTurn = false;
                else if(rc.canMove(topDir.rotateLeft())) leftTurn = true;
                else if(rc.canMove(topDir.rotateRight().rotateRight())) leftTurn = false;
                else if(rc.canMove(topDir.rotateLeft().rotateLeft())) leftTurn = true;
                else leftTurn = rng.nextInt(0, 1) == 1;
            }
            if(leftTurn) topDir = topDir.rotateLeft();
            else topDir = topDir.rotateRight();
            stackSize++;
            iters++;
            if(iters == 8) {
                stackSize -= 8;
                return Direction.CENTER;
            }
        }
        return topDir;
    }
}
