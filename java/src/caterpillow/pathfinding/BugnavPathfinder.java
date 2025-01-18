package caterpillow.pathfinding;

import java.util.HashMap;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import static caterpillow.Game.rc;
import static caterpillow.Game.trng;
import caterpillow.util.GamePredicate;
import caterpillow.util.Util;

public class BugnavPathfinder extends AbstractPathfinder {
    // temporary patch to make sure nothing breaks
    private MapLocation expected;

    public MapLocation target;
    public Direction bottomDir;
    public Direction topDir;
    public int stackSize;
    public boolean leftTurn = false;
    public HashMap<MapLocation, Boolean> leftTurnHist = new HashMap<>();
    public int lastNonzeroStackTime = 0;
    public GamePredicate<MapInfo> avoid;
    public boolean reset = false;

    public BugnavPathfinder(GamePredicate<MapInfo> avoid) {
        this.avoid = avoid;
        this.expected = rc.getLocation();
    }
    public BugnavPathfinder() {
        this.avoid = m -> false;
        this.expected = rc.getLocation();
    }

    public boolean canMove(Direction dir) throws GameActionException {
        return rc.canMove(dir) && !avoid.test(rc.senseMapInfo(rc.getLocation().add(dir)));
    }

    boolean canMoveAndIsAlly(Direction dir) throws GameActionException {
        if (!rc.canMove(dir)) {
            return false;
        }
        MapInfo info = rc.senseMapInfo(rc.getLocation().add(dir));
        return info.getPaint().isAlly() && !avoid.test(info);
    }

    boolean canMoveAndIsNeutral(Direction dir) throws GameActionException {
        if (!rc.canMove(dir)) {
            return false;
        }
        MapInfo info = rc.senseMapInfo(rc.getLocation().add(dir));
        return info.getPaint().equals(PaintType.EMPTY) && !avoid.test(info);
    }

    @Override
    public Direction getMove(MapLocation to) throws GameActionException {
        if (rc.getLocation().equals(to)) {
            return null;
        }
        if(rc.getLocation().isAdjacentTo(to) && canMove(rc.getLocation().directionTo(to))) {
            return rc.getLocation().directionTo(to);
        }
        if (target == null || expected != rc.getLocation() || reset) {
            stackSize = 0;
            topDir = bottomDir = rc.getLocation().directionTo(to);
            leftTurnHist.clear();
            reset = false;
        }
        target = to;
        if(leftTurnHist.containsKey(rc.getLocation()) && (stackSize == 0 || leftTurn == leftTurnHist.get(rc.getLocation()))) {
            leftTurn = !leftTurnHist.get(rc.getLocation());
            stackSize = 1;
            topDir = leftTurn ? bottomDir.rotateLeft() : bottomDir.rotateRight();
            rc.setIndicatorDot(rc.getLocation(), 255, 255, 0);
        }
        if (leftTurn) {
            if (!rc.getLocation().directionTo(target).equals(bottomDir)) {
                if (rc.getLocation().directionTo(target).equals(bottomDir.rotateRight())) {
                    stackSize++;
                    bottomDir = bottomDir.rotateRight();
                }
                // else if (rc.getLocation().directionTo(target).equals(bottomDir.rotateRight().rotateRight())) {
                //     stackSize += 2;
                //     bottomDir = bottomDir.rotateRight().rotateRight();
                // }
                else if (rc.getLocation().directionTo(target).equals(bottomDir.rotateLeft())) {
                    stackSize--;
                    bottomDir = bottomDir.rotateLeft();
                    if (stackSize < 0) {
                        stackSize = 0;
                        topDir = bottomDir;
                    }
                }
                // else if (rc.getLocation().directionTo(target).equals(bottomDir.rotateLeft().rotateLeft())) {
                //     stackSize -= 2;
                //     bottomDir = bottomDir.rotateLeft().rotateLeft();
                //     if (stackSize < 0) {
                //         stackSize = 0;
                //         topDir = bottomDir;
                //     }
                // }
                else {
                    stackSize = 0;
                    topDir = bottomDir = rc.getLocation().directionTo(target);
                    leftTurnHist.clear();
                }
            }
            if (stackSize >= 2 && canMove(topDir.rotateRight().rotateRight())) {
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
                }
                // else if (rc.getLocation().directionTo(target).equals(bottomDir.rotateLeft().rotateLeft())) {
                //     stackSize += 2;
                //     bottomDir = bottomDir.rotateLeft().rotateLeft();
                // }
                else if (rc.getLocation().directionTo(target).equals(bottomDir.rotateRight())) {
                    stackSize--;
                    bottomDir = bottomDir.rotateRight();
                    if (stackSize < 0) {
                        stackSize = 0;
                        topDir = bottomDir;
                    }
                }
                // else if (rc.getLocation().directionTo(target).equals(bottomDir.rotateRight().rotateRight())) {
                //     stackSize -= 2;
                //     bottomDir = bottomDir.rotateRight().rotateRight();
                //     if (stackSize < 0) {
                //         stackSize = 0;
                //         topDir = bottomDir;
                //     }
                // }
                else {
                    stackSize = 0;
                    topDir = bottomDir = rc.getLocation().directionTo(target);
                    leftTurnHist.clear();
                }
            }

            if (stackSize >= 2 && canMove(topDir.rotateLeft().rotateLeft())) {
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
                stackSize = 0;
                continue;
            }
            if (stackSize == 0 && lastNonzeroStackTime < rc.getRoundNum() - 4) {
                if (canMove(topDir.rotateRight().rotateRight())) leftTurn = false;
                else if (canMove(topDir.rotateLeft().rotateLeft())) leftTurn = true;
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
    public Direction makeMove(MapLocation to) throws GameActionException {
        Direction dir = null;
        if (rc.isMovementReady()) {
            dir = getMove(to);
            if (dir != null && rc.canMove(dir)) {
                assert !avoid.test(rc.senseMapInfo(rc.getLocation().add(dir)));
                makeMove(dir);
            } else {
                // emergency!!!
                if (avoid.test(rc.senseMapInfo(rc.getLocation()))) {
                    // super jank workaround
                    GamePredicate<MapInfo> opred = avoid;
                    avoid = m -> false;
                    dir = getMove(to);
                    if (dir != null && rc.canMove(dir)) {
                        makeMove(dir);
                    }
                    avoid = opred;
                }
            }
        }
        // rc.setIndicatorString(topDir.toString() + " " + bottomDir.toString() + " " + leftTurn + " " + stackSize);
        rc.setIndicatorLine(rc.getLocation(), to, 0, 255, 0);
        return dir;
    }

    @Override
    public void makeMove(Direction dir) throws GameActionException {
        // rc.setIndicatorString("HERE " + rc.getLocation().toString() + " " + leftTurn + " " + stackSize + " " + topDir + " " + bottomDir + " " + dir);
        if(stackSize > 0) leftTurnHist.put(rc.getLocation(), leftTurn);
        if (dir != null && rc.canMove(dir)) rc.move(dir);
        expected = rc.getLocation();
        if(stackSize > 0) {
            lastNonzeroStackTime = rc.getRoundNum();
        }
    }

    @Override
    public void reset() {
        reset = true;
    }
}
