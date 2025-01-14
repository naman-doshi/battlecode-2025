package caterpillow.pathfinding;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import static caterpillow.Game.rc;
import static caterpillow.Game.trng;
import caterpillow.util.GamePredicate;

public class BugnavPathfinder extends AbstractPathfinder {
    public MapLocation target;
    public Direction bottomDir;
    public Direction topDir;
    public int stackSize;
    public boolean leftTurn = false;
    public GamePredicate<MapInfo> avoid;

    public BugnavPathfinder(GamePredicate<MapInfo> avoid) {
        this.avoid = avoid;
    }
    public BugnavPathfinder() {
        this.avoid = m -> false;
    }

    @Override
    public Direction getMove(MapLocation to) throws GameActionException {
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
            if (stackSize >= 2 && rc.canMove(topDir.opposite()) && !avoid.test(rc.senseMapInfo(rc.getLocation().add(topDir.opposite())))) {
                stackSize -= 2;
                topDir = topDir.rotateRight().rotateRight();
                return topDir;
            }
            if (stackSize >= 1 && rc.canMove(topDir.rotateRight()) && !avoid.test(rc.senseMapInfo(rc.getLocation().add(topDir.rotateRight())))) {
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
            if (stackSize >= 2 && rc.canMove(topDir.opposite()) && !avoid.test(rc.senseMapInfo(rc.getLocation().add(topDir.opposite())))) {
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
            if(rc.canMove(topDir) && rc.senseMapInfo(rc.getLocation().add(topDir)).getPaint().isAlly()) return topDir;
            if(rc.canMove(topDir.rotateRight()) && rc.senseMapInfo(rc.getLocation().add(topDir.rotateRight())).getPaint().isAlly()) {
                leftTurn = false;
                return topDir.rotateRight();
            }
            if(rc.canMove(topDir.rotateLeft()) && rc.senseMapInfo(rc.getLocation().add(topDir.rotateLeft())).getPaint().isAlly()) {
                leftTurn = true;
                return topDir.rotateLeft();
            }
            if(rc.canMove(topDir) && !rc.senseMapInfo(rc.getLocation().add(topDir)).getPaint().isEnemy()) return topDir;
            if(rc.canMove(topDir.rotateRight()) && !rc.senseMapInfo(rc.getLocation().add(topDir.rotateRight())).getPaint().isEnemy()) {
                leftTurn = false;
                return topDir.rotateRight();
            }
            if(rc.canMove(topDir.rotateLeft()) && !rc.senseMapInfo(rc.getLocation().add(topDir.rotateLeft())).getPaint().isEnemy()) {
                leftTurn = true;
                return topDir.rotateLeft();
            }
            if(rc.canMove(topDir)) return topDir;
            if(rc.canMove(topDir.rotateRight())) {
                leftTurn = false;
                return topDir.rotateRight();
            }
            if(rc.canMove(topDir.rotateLeft())) {
                leftTurn = true;
                return topDir.rotateLeft();
            }
        }
        int iters = 0;
        while(!rc.canMove(topDir) || avoid.test(rc.senseMapInfo(rc.getLocation().add(topDir)))) {
            MapLocation nextLoc = rc.getLocation().add(topDir);
            // avoid following the edge of the map
            if(nextLoc.x < 0 || nextLoc.y < 0 || nextLoc.x >= rc.getMapWidth() || nextLoc.y >= rc.getMapHeight()) {
                leftTurn = !leftTurn;
                topDir = bottomDir;
                continue;
            }
            if (stackSize == 0) {
                if (rc.canMove(topDir.opposite()) && !avoid.test(rc.senseMapInfo(rc.getLocation().add(topDir.opposite())))) leftTurn = false;
                else if (rc.canMove(topDir.opposite()) && !avoid.test(rc.senseMapInfo(rc.getLocation().add(topDir.opposite())))) leftTurn = true;
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
        assert rc.canMove(topDir);
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
            }
        }
    }
}