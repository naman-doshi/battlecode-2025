package caterpillow.pathfinding;

import java.util.HashMap;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import battlecode.common.RobotInfo;

import static caterpillow.Game.rc;
import static caterpillow.Game.trng;
import caterpillow.util.GamePredicate;
import static caterpillow.util.Util.*;
import caterpillow.tracking.CellTracker;
import caterpillow.util.Profiler;


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

    @Override
    public Direction getMove(MapLocation to) throws GameActionException {
        if (!rc.isMovementReady() || rc.getLocation().equals(to)) {
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
        if(stackSize <= 1) {
            stackSize = 0;
            topDir = bottomDir;
            Direction[] poss = {topDir, topDir.rotateRight(), topDir.rotateLeft()};
            Direction best = null;
            int bestScore = 1000000000;
            for(Direction d : poss) {
                if(canMove(d)) {
                    int score = 0;
                    for(Direction off : directions) {
                        if(off.equals(d.opposite())) continue;
                        MapLocation loc = rc.getLocation().add(d).add(off);
                        if(!rc.onTheMap(loc)) continue;
                        RobotInfo robot = rc.senseRobotAtLocation(loc);
                        if(robot != null && robot.team.equals(rc.getTeam())) {
                            score++;
                        }
                    }
                    MapInfo info = rc.senseMapInfo(rc.getLocation().add(d));
                    if(!info.getPaint().isAlly()) score++;
                    if(info.getPaint().isEnemy()) {
                        score *= 2;
                    }
                    score *= 1000000;
                    score += rc.getLocation().add(d).distanceSquaredTo(target) * 10;
                    score += trng.nextInt(10);
                    if(score < bestScore) {
                        bestScore = score;
                        best = d;
                    }
                    indicate(d.toString() + " " + score);
                }
            }
            if(best != null) indicate(best.toString());
            if(best != null) {
                if(best.equals(topDir.rotateRight())) {
                    leftTurn = false;
                    stackSize = 1;
                }
                if(best.equals(topDir.rotateLeft())) {
                    leftTurn = true;
                    stackSize = 1;
                }
                return best;
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
                if(trng.nextInt(0, 1) == 0) {
                    if (canMove(topDir.rotateRight().rotateRight())) leftTurn = false;
                    else if (canMove(topDir.rotateLeft().rotateLeft())) leftTurn = true;
                    else leftTurn = trng.nextInt(0, 1) == 1; // change later
                } else {
                    if (canMove(topDir.rotateLeft().rotateLeft())) leftTurn = true;
                    else if (canMove(topDir.rotateRight().rotateRight())) leftTurn = false;
                    else leftTurn = trng.nextInt(0, 1) == 1; // change later
                }
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
        if (dir != null && rc.canMove(dir)) {
            if(stackSize > 0) {
                // indicate("LEFTTURNHIST " + rc.getLocation().toString() + " " + leftTurn);
                leftTurnHist.put(rc.getLocation(), leftTurn);
            }
            rc.move(dir);
            CellTracker.postMove(dir);
        }
        expected = rc.getLocation();
        if(stackSize > 0) {
            lastNonzeroStackTime = rc.getRoundNum();
        }
        // indicate("LeftTurn: " + leftTurn);
    }

    @Override
    public void reset() {
        reset = true;
    }
}
