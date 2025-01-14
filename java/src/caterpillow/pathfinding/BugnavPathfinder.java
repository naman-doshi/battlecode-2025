package caterpillow.pathfinding;

import battlecode.common.*;

import static caterpillow.Game.rc;
import static caterpillow.Game.trng;
import caterpillow.util.GamePredicate;

public class BugnavPathfinder extends AbstractPathfinder {
    // temporary patch to make sure nothing breaks
    private MapLocation expected;

    public MapLocation target;
    public Direction bottomDir;
    public Direction topDir;
    public int stackSize;
    public boolean leftTurn = false;
    public GamePredicate<MapInfo> avoid;

    public BugnavPathfinder(GamePredicate<MapInfo> avoid) {
        this.avoid = avoid;
        this.expected = rc.getLocation();
    }
    public BugnavPathfinder() {
        this.avoid = m -> false;
        this.expected = rc.getLocation();
    }

    boolean canMove(Direction dir) throws GameActionException {
        return rc.canMove(dir) && !avoid.test(rc.senseMapInfo(rc.getLocation().add(dir)));
    }

    boolean canMoveAndIsAlly(Direction dir) throws GameActionException {
        if (!rc.canMove(dir)) {
            return false;
        }
        MapInfo info = rc.senseMapInfo(rc.getLocation().add(dir));
        return info.getPaint().isAlly() && avoid.test(info);
    }

    boolean canMoveAndIsNeutral(Direction dir) throws GameActionException {
        if (!rc.canMove(dir)) {
            return false;
        }
        MapInfo info = rc.senseMapInfo(rc.getLocation().add(dir));
        return info.getPaint().equals(PaintType.EMPTY) && avoid.test(info);
    }

    @Override
    public Direction getMove(MapLocation to) throws GameActionException {
        if (rc.getLocation().equals(to)) {
            return null;
        }
        if (target == null || !target.equals(to) || expected != rc.getLocation()) {
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
            if (stackSize >= 2 && canMove(topDir.opposite())) {
                stackSize -= 2;
                topDir = topDir.rotateRight().rotateRight();
                return topDir;
            }
            if (stackSize >= 1 && canMove(topDir.rotateRight())) {
                stackSize--;
                topDir = topDir.rotateRight();
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
            if (stackSize >= 2 && canMove(topDir.opposite())) {
                stackSize -= 2;
                topDir = topDir.rotateLeft().rotateLeft();
                return topDir;
            }
            if (stackSize >= 1) {
                stackSize--;
                topDir = topDir.rotateLeft();
            }
        }
        if(stackSize == 0) {
            if(canMoveAndIsAlly(topDir)) return topDir;
            if(canMoveAndIsAlly(topDir.rotateRight())) {
                leftTurn = false;
                return topDir.rotateRight();
            }
            if(canMoveAndIsAlly(topDir.rotateLeft())) {
                leftTurn = true;
                return topDir.rotateLeft();
            }
            if(canMoveAndIsNeutral(topDir)) return topDir;
            if(canMoveAndIsNeutral(topDir.rotateRight())) {
                leftTurn = false;
                return topDir.rotateRight();
            }
            if(canMoveAndIsNeutral(topDir.rotateLeft())) {
                leftTurn = true;
                return topDir.rotateLeft();
            }
            if(canMove(topDir)) return topDir;
            if(canMove(topDir.rotateRight())) {
                leftTurn = false;
                return topDir.rotateRight();
            }
            if(canMove(topDir.rotateLeft())) {
                leftTurn = true;
                return topDir.rotateLeft();
            }
        }
        int iters = 0;
        while (!canMove(topDir))  {
            MapLocation nextLoc = rc.getLocation().add(topDir);
            // avoid following the edge of the map
            if(nextLoc.x < 0 || nextLoc.y < 0 || nextLoc.x >= rc.getMapWidth() || nextLoc.y >= rc.getMapHeight()) {
                leftTurn = !leftTurn;
                topDir = bottomDir;
                continue;
            }
            if (stackSize == 0) {
                if (canMove(topDir.opposite())) leftTurn = false;
                else if (canMove(topDir.opposite())) leftTurn = true;
                else leftTurn = trng.nextInt(0, 1) == 1; // change later
            }
            if (leftTurn) topDir = topDir.rotateLeft();
            else topDir = topDir.rotateRight();
            stackSize++;
            iters++;
            if (iters == 8) {
                return null;
            }
        }
        assert canMove(topDir);
        // rc.setIndicatorLine(rc.getLocation(), rc.getLocation().add(topDir), 0, 0, 255);
        // rc.setIndicatorString(topDir + " " + bottomDir + " " + leftTurn + " " + stackSize);
        return topDir;
    }

    @Override
    public void makeMove(MapLocation to) throws GameActionException {
        if (rc.isMovementReady()) {
            Direction dir = getMove(to);
            if (dir != null && rc.canMove(dir)) {
                rc.move(dir);
                expected = rc.getLocation();
            }
        }
    }
}