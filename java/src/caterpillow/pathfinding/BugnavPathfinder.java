package caterpillow.pathfinding;

import java.util.HashMap;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import caterpillow.Game;
import static caterpillow.Game.bot;
import static caterpillow.Game.mapHeight;
import static caterpillow.Game.mapWidth;
import static caterpillow.Game.rc;
import static caterpillow.Game.team;
import static caterpillow.Game.trng;
import caterpillow.robot.agents.Agent;
import static caterpillow.tracking.CellTracker.mapInfos;
import caterpillow.tracking.RobotTracker;
import caterpillow.util.GameFunction;
import caterpillow.util.GamePredicate;
import static caterpillow.util.Util.directions;
import caterpillow.util.Profiler;
import static caterpillow.tracking.CellTracker.*;

public class BugnavPathfinder {
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
    public GameFunction<MapInfo, Integer> cellPenalty;
    public boolean alwaysLeftTurn = false;
    public boolean reset = false;

    public BugnavPathfinder(GamePredicate<MapInfo> avoid) {
        this.avoid = avoid;
        this.cellPenalty = m -> 0;
        this.expected = Game.pos;
    }
    public BugnavPathfinder(GamePredicate<MapInfo> avoid, GameFunction<MapInfo, Integer> cellPenalty) {
        this.avoid = avoid;
        this.cellPenalty = cellPenalty;
        this.expected = Game.pos;
    }
    public BugnavPathfinder() {
        this.avoid = m -> false;
        this.expected = Game.pos;
    }

    public boolean canMove(Direction dir) throws GameActionException {
        return rc.canMove(dir) && !avoid.test(rc.senseMapInfo(Game.pos.add(dir)));
    }

    /*

    orth_directions = [
    (1, 0, "EAST"),
    (0, 1, "NORTH"),
    (-1, 0, "WEST"),
    (0, -1, "SOUTH")
]

directions = [
    (0, 1, "NORTH"),
    (1, 1, "NORTHEAST"),
    (1, 0, "EAST"),
    (1, -1, "SOUTHEAST"),
    (0, -1, "SOUTH"),
    (-1, -1, "SOUTHWEST"),
    (-1, 0, "WEST"),
    (-1, 1, "NORTHWEST")
]

def bruh(cap):
    print("int score = 0;")
    print("switch (topDir.ordinal()) {")
    diri = -1
    for dx, dy, dir_str in directions:
        diri += 1
        print(f"case {diri}:")
        x = 4 + dx
        y = 4 + dy
        for di in range(-1, cap):
            possi = (diri + di + 8) % 8
            dx2, dy2, dir_str2 = directions[possi]
            print(f"if (canMove(Direction.{dir_str2})) {{")
            print("score = 0;")
            for dx3, dy3, dir_str3 in directions:
                print(f"if (RobotTracker.bot{x + dx3}{y + dy3} != null && RobotTracker.bot{x + dx3}{y + dy3}.team == team) score++;")
            print(f"MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.{dir_str2}));")
            print("if (info.getPaint() == PaintType.EMPTY) score++;")
            print("if (info.getPaint().isEnemy()) score *= 2;")
            print("if (cellPenalty != null) score += cellPenalty.apply(info);")
            print("score *= 1000000;")
            print(f"score += Game.pos.add(Direction.{dir_str2}).distanceSquaredTo(target) * 10;")
            print("score += trng.nextInt(10);")
            print("if (score < bestScore) {")
            print("bestScore = score;")
            print(f"best = Direction.{dir_str2};")
            print("}")
            print("}")
        print("break;")
    print("}")

print("if (alwaysLeftTurn) {")
bruh(1);
print("} else {")
bruh(2);
print("}")

     */

    public Direction getMove(MapLocation to) throws GameActionException {
        if (!rc.isMovementReady() || Game.pos.equals(to)) {
            return null;
        }
        if(Game.pos.isAdjacentTo(to) && canMove(Game.pos.directionTo(to))) {
            return Game.pos.directionTo(to);
        }
        if (target == null || expected != Game.pos || reset) {
            stackSize = 0;
            topDir = bottomDir = Game.pos.directionTo(to);
            leftTurnHist.clear();
            reset = false;
        }
        target = to;
        if (leftTurn) {
            if (!Game.pos.directionTo(target).equals(bottomDir)) {
                if (Game.pos.directionTo(target).equals(bottomDir.rotateRight())) {
                    stackSize++;
                    bottomDir = bottomDir.rotateRight();
                } else if (Game.pos.directionTo(target).equals(bottomDir.rotateLeft())) {
                    stackSize--;
                    bottomDir = bottomDir.rotateLeft();
                    if (stackSize < 0) {
                        stackSize = 0;
                        topDir = bottomDir;
                    }
                } else {
                    stackSize = 0;
                    topDir = bottomDir = Game.pos.directionTo(target);
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
            if (!Game.pos.directionTo(target).equals(bottomDir)) {
                if (Game.pos.directionTo(target).equals(bottomDir.rotateLeft())) {
                    stackSize++;
                    bottomDir = bottomDir.rotateLeft();
                } else if (Game.pos.directionTo(target).equals(bottomDir.rotateRight())) {
                    stackSize--;
                    bottomDir = bottomDir.rotateRight();
                    if (stackSize < 0) {
                        stackSize = 0;
                        topDir = bottomDir;
                    }
                } else {
                    stackSize = 0;
                    topDir = bottomDir = Game.pos.directionTo(target);
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
        if (stackSize <= 1) {
            stackSize = 0;
            topDir = bottomDir;
            Direction best = null;
            int bestScore = 1000000000;

            // begin

            if (alwaysLeftTurn) {
                int score = 0;
                switch (topDir.ordinal()) {
                    case 0:
                        if (canMove(Direction.NORTHWEST)) {
                            score = 0;
                            if (RobotTracker.bot46 != null && RobotTracker.bot46.team == team) score++;
                            if (RobotTracker.bot56 != null && RobotTracker.bot56.team == team) score++;
                            if (RobotTracker.bot55 != null && RobotTracker.bot55.team == team) score++;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            if (RobotTracker.bot35 != null && RobotTracker.bot35.team == team) score++;
                            if (RobotTracker.bot36 != null && RobotTracker.bot36.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.NORTHWEST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.NORTHWEST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.NORTHWEST;
                            }
                        }
                        if (canMove(Direction.NORTH)) {
                            score = 0;
                            if (RobotTracker.bot46 != null && RobotTracker.bot46.team == team) score++;
                            if (RobotTracker.bot56 != null && RobotTracker.bot56.team == team) score++;
                            if (RobotTracker.bot55 != null && RobotTracker.bot55.team == team) score++;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            if (RobotTracker.bot35 != null && RobotTracker.bot35.team == team) score++;
                            if (RobotTracker.bot36 != null && RobotTracker.bot36.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.NORTH));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.NORTH).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.NORTH;
                            }
                        }
                        break;
                    case 1:
                        if (canMove(Direction.NORTH)) {
                            score = 0;
                            if (RobotTracker.bot56 != null && RobotTracker.bot56.team == team) score++;
                            if (RobotTracker.bot66 != null && RobotTracker.bot66.team == team) score++;
                            if (RobotTracker.bot65 != null && RobotTracker.bot65.team == team) score++;
                            if (RobotTracker.bot64 != null && RobotTracker.bot64.team == team) score++;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            if (RobotTracker.bot46 != null && RobotTracker.bot46.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.NORTH));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.NORTH).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.NORTH;
                            }
                        }
                        if (canMove(Direction.NORTHEAST)) {
                            score = 0;
                            if (RobotTracker.bot56 != null && RobotTracker.bot56.team == team) score++;
                            if (RobotTracker.bot66 != null && RobotTracker.bot66.team == team) score++;
                            if (RobotTracker.bot65 != null && RobotTracker.bot65.team == team) score++;
                            if (RobotTracker.bot64 != null && RobotTracker.bot64.team == team) score++;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            if (RobotTracker.bot46 != null && RobotTracker.bot46.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.NORTHEAST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.NORTHEAST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.NORTHEAST;
                            }
                        }
                        break;
                    case 2:
                        if (canMove(Direction.NORTHEAST)) {
                            score = 0;
                            if (RobotTracker.bot55 != null && RobotTracker.bot55.team == team) score++;
                            if (RobotTracker.bot65 != null && RobotTracker.bot65.team == team) score++;
                            if (RobotTracker.bot64 != null && RobotTracker.bot64.team == team) score++;
                            if (RobotTracker.bot63 != null && RobotTracker.bot63.team == team) score++;
                            if (RobotTracker.bot53 != null && RobotTracker.bot53.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.NORTHEAST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.NORTHEAST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.NORTHEAST;
                            }
                        }
                        if (canMove(Direction.EAST)) {
                            score = 0;
                            if (RobotTracker.bot55 != null && RobotTracker.bot55.team == team) score++;
                            if (RobotTracker.bot65 != null && RobotTracker.bot65.team == team) score++;
                            if (RobotTracker.bot64 != null && RobotTracker.bot64.team == team) score++;
                            if (RobotTracker.bot63 != null && RobotTracker.bot63.team == team) score++;
                            if (RobotTracker.bot53 != null && RobotTracker.bot53.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.EAST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.EAST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.EAST;
                            }
                        }
                        break;
                    case 3:
                        if (canMove(Direction.EAST)) {
                            score = 0;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot64 != null && RobotTracker.bot64.team == team) score++;
                            if (RobotTracker.bot63 != null && RobotTracker.bot63.team == team) score++;
                            if (RobotTracker.bot62 != null && RobotTracker.bot62.team == team) score++;
                            if (RobotTracker.bot52 != null && RobotTracker.bot52.team == team) score++;
                            if (RobotTracker.bot42 != null && RobotTracker.bot42.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.EAST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.EAST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.EAST;
                            }
                        }
                        if (canMove(Direction.SOUTHEAST)) {
                            score = 0;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot64 != null && RobotTracker.bot64.team == team) score++;
                            if (RobotTracker.bot63 != null && RobotTracker.bot63.team == team) score++;
                            if (RobotTracker.bot62 != null && RobotTracker.bot62.team == team) score++;
                            if (RobotTracker.bot52 != null && RobotTracker.bot52.team == team) score++;
                            if (RobotTracker.bot42 != null && RobotTracker.bot42.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.SOUTHEAST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.SOUTHEAST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.SOUTHEAST;
                            }
                        }
                        break;
                    case 4:
                        if (canMove(Direction.SOUTHEAST)) {
                            score = 0;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot53 != null && RobotTracker.bot53.team == team) score++;
                            if (RobotTracker.bot52 != null && RobotTracker.bot52.team == team) score++;
                            if (RobotTracker.bot42 != null && RobotTracker.bot42.team == team) score++;
                            if (RobotTracker.bot32 != null && RobotTracker.bot32.team == team) score++;
                            if (RobotTracker.bot33 != null && RobotTracker.bot33.team == team) score++;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.SOUTHEAST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.SOUTHEAST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.SOUTHEAST;
                            }
                        }
                        if (canMove(Direction.SOUTH)) {
                            score = 0;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot53 != null && RobotTracker.bot53.team == team) score++;
                            if (RobotTracker.bot52 != null && RobotTracker.bot52.team == team) score++;
                            if (RobotTracker.bot42 != null && RobotTracker.bot42.team == team) score++;
                            if (RobotTracker.bot32 != null && RobotTracker.bot32.team == team) score++;
                            if (RobotTracker.bot33 != null && RobotTracker.bot33.team == team) score++;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.SOUTH));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.SOUTH).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.SOUTH;
                            }
                        }
                        break;
                    case 5:
                        if (canMove(Direction.SOUTH)) {
                            score = 0;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot42 != null && RobotTracker.bot42.team == team) score++;
                            if (RobotTracker.bot32 != null && RobotTracker.bot32.team == team) score++;
                            if (RobotTracker.bot22 != null && RobotTracker.bot22.team == team) score++;
                            if (RobotTracker.bot23 != null && RobotTracker.bot23.team == team) score++;
                            if (RobotTracker.bot24 != null && RobotTracker.bot24.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.SOUTH));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.SOUTH).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.SOUTH;
                            }
                        }
                        if (canMove(Direction.SOUTHWEST)) {
                            score = 0;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot42 != null && RobotTracker.bot42.team == team) score++;
                            if (RobotTracker.bot32 != null && RobotTracker.bot32.team == team) score++;
                            if (RobotTracker.bot22 != null && RobotTracker.bot22.team == team) score++;
                            if (RobotTracker.bot23 != null && RobotTracker.bot23.team == team) score++;
                            if (RobotTracker.bot24 != null && RobotTracker.bot24.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.SOUTHWEST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.SOUTHWEST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.SOUTHWEST;
                            }
                        }
                        break;
                    case 6:
                        if (canMove(Direction.SOUTHWEST)) {
                            score = 0;
                            if (RobotTracker.bot35 != null && RobotTracker.bot35.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot33 != null && RobotTracker.bot33.team == team) score++;
                            if (RobotTracker.bot23 != null && RobotTracker.bot23.team == team) score++;
                            if (RobotTracker.bot24 != null && RobotTracker.bot24.team == team) score++;
                            if (RobotTracker.bot25 != null && RobotTracker.bot25.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.SOUTHWEST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.SOUTHWEST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.SOUTHWEST;
                            }
                        }
                        if (canMove(Direction.WEST)) {
                            score = 0;
                            if (RobotTracker.bot35 != null && RobotTracker.bot35.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot33 != null && RobotTracker.bot33.team == team) score++;
                            if (RobotTracker.bot23 != null && RobotTracker.bot23.team == team) score++;
                            if (RobotTracker.bot24 != null && RobotTracker.bot24.team == team) score++;
                            if (RobotTracker.bot25 != null && RobotTracker.bot25.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.WEST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.WEST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.WEST;
                            }
                        }
                        break;
                    case 7:
                        if (canMove(Direction.WEST)) {
                            score = 0;
                            if (RobotTracker.bot36 != null && RobotTracker.bot36.team == team) score++;
                            if (RobotTracker.bot46 != null && RobotTracker.bot46.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            if (RobotTracker.bot24 != null && RobotTracker.bot24.team == team) score++;
                            if (RobotTracker.bot25 != null && RobotTracker.bot25.team == team) score++;
                            if (RobotTracker.bot26 != null && RobotTracker.bot26.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.WEST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.WEST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.WEST;
                            }
                        }
                        if (canMove(Direction.NORTHWEST)) {
                            score = 0;
                            if (RobotTracker.bot36 != null && RobotTracker.bot36.team == team) score++;
                            if (RobotTracker.bot46 != null && RobotTracker.bot46.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            if (RobotTracker.bot24 != null && RobotTracker.bot24.team == team) score++;
                            if (RobotTracker.bot25 != null && RobotTracker.bot25.team == team) score++;
                            if (RobotTracker.bot26 != null && RobotTracker.bot26.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.NORTHWEST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.NORTHWEST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.NORTHWEST;
                            }
                        }
                        break;
                }
            } else {
                int score = 0;
                switch (topDir.ordinal()) {
                    case 0:
                        if (canMove(Direction.NORTHWEST)) {
                            score = 0;
                            if (RobotTracker.bot46 != null && RobotTracker.bot46.team == team) score++;
                            if (RobotTracker.bot56 != null && RobotTracker.bot56.team == team) score++;
                            if (RobotTracker.bot55 != null && RobotTracker.bot55.team == team) score++;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            if (RobotTracker.bot35 != null && RobotTracker.bot35.team == team) score++;
                            if (RobotTracker.bot36 != null && RobotTracker.bot36.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.NORTHWEST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.NORTHWEST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.NORTHWEST;
                            }
                        }
                        if (canMove(Direction.NORTH)) {
                            score = 0;
                            if (RobotTracker.bot46 != null && RobotTracker.bot46.team == team) score++;
                            if (RobotTracker.bot56 != null && RobotTracker.bot56.team == team) score++;
                            if (RobotTracker.bot55 != null && RobotTracker.bot55.team == team) score++;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            if (RobotTracker.bot35 != null && RobotTracker.bot35.team == team) score++;
                            if (RobotTracker.bot36 != null && RobotTracker.bot36.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.NORTH));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.NORTH).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.NORTH;
                            }
                        }
                        if (canMove(Direction.NORTHEAST)) {
                            score = 0;
                            if (RobotTracker.bot46 != null && RobotTracker.bot46.team == team) score++;
                            if (RobotTracker.bot56 != null && RobotTracker.bot56.team == team) score++;
                            if (RobotTracker.bot55 != null && RobotTracker.bot55.team == team) score++;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            if (RobotTracker.bot35 != null && RobotTracker.bot35.team == team) score++;
                            if (RobotTracker.bot36 != null && RobotTracker.bot36.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.NORTHEAST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.NORTHEAST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.NORTHEAST;
                            }
                        }
                        break;
                    case 1:
                        if (canMove(Direction.NORTH)) {
                            score = 0;
                            if (RobotTracker.bot56 != null && RobotTracker.bot56.team == team) score++;
                            if (RobotTracker.bot66 != null && RobotTracker.bot66.team == team) score++;
                            if (RobotTracker.bot65 != null && RobotTracker.bot65.team == team) score++;
                            if (RobotTracker.bot64 != null && RobotTracker.bot64.team == team) score++;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            if (RobotTracker.bot46 != null && RobotTracker.bot46.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.NORTH));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.NORTH).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.NORTH;
                            }
                        }
                        if (canMove(Direction.NORTHEAST)) {
                            score = 0;
                            if (RobotTracker.bot56 != null && RobotTracker.bot56.team == team) score++;
                            if (RobotTracker.bot66 != null && RobotTracker.bot66.team == team) score++;
                            if (RobotTracker.bot65 != null && RobotTracker.bot65.team == team) score++;
                            if (RobotTracker.bot64 != null && RobotTracker.bot64.team == team) score++;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            if (RobotTracker.bot46 != null && RobotTracker.bot46.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.NORTHEAST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.NORTHEAST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.NORTHEAST;
                            }
                        }
                        if (canMove(Direction.EAST)) {
                            score = 0;
                            if (RobotTracker.bot56 != null && RobotTracker.bot56.team == team) score++;
                            if (RobotTracker.bot66 != null && RobotTracker.bot66.team == team) score++;
                            if (RobotTracker.bot65 != null && RobotTracker.bot65.team == team) score++;
                            if (RobotTracker.bot64 != null && RobotTracker.bot64.team == team) score++;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            if (RobotTracker.bot46 != null && RobotTracker.bot46.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.EAST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.EAST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.EAST;
                            }
                        }
                        break;
                    case 2:
                        if (canMove(Direction.NORTHEAST)) {
                            score = 0;
                            if (RobotTracker.bot55 != null && RobotTracker.bot55.team == team) score++;
                            if (RobotTracker.bot65 != null && RobotTracker.bot65.team == team) score++;
                            if (RobotTracker.bot64 != null && RobotTracker.bot64.team == team) score++;
                            if (RobotTracker.bot63 != null && RobotTracker.bot63.team == team) score++;
                            if (RobotTracker.bot53 != null && RobotTracker.bot53.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.NORTHEAST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.NORTHEAST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.NORTHEAST;
                            }
                        }
                        if (canMove(Direction.EAST)) {
                            score = 0;
                            if (RobotTracker.bot55 != null && RobotTracker.bot55.team == team) score++;
                            if (RobotTracker.bot65 != null && RobotTracker.bot65.team == team) score++;
                            if (RobotTracker.bot64 != null && RobotTracker.bot64.team == team) score++;
                            if (RobotTracker.bot63 != null && RobotTracker.bot63.team == team) score++;
                            if (RobotTracker.bot53 != null && RobotTracker.bot53.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.EAST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.EAST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.EAST;
                            }
                        }
                        if (canMove(Direction.SOUTHEAST)) {
                            score = 0;
                            if (RobotTracker.bot55 != null && RobotTracker.bot55.team == team) score++;
                            if (RobotTracker.bot65 != null && RobotTracker.bot65.team == team) score++;
                            if (RobotTracker.bot64 != null && RobotTracker.bot64.team == team) score++;
                            if (RobotTracker.bot63 != null && RobotTracker.bot63.team == team) score++;
                            if (RobotTracker.bot53 != null && RobotTracker.bot53.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.SOUTHEAST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.SOUTHEAST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.SOUTHEAST;
                            }
                        }
                        break;
                    case 3:
                        if (canMove(Direction.EAST)) {
                            score = 0;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot64 != null && RobotTracker.bot64.team == team) score++;
                            if (RobotTracker.bot63 != null && RobotTracker.bot63.team == team) score++;
                            if (RobotTracker.bot62 != null && RobotTracker.bot62.team == team) score++;
                            if (RobotTracker.bot52 != null && RobotTracker.bot52.team == team) score++;
                            if (RobotTracker.bot42 != null && RobotTracker.bot42.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.EAST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.EAST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.EAST;
                            }
                        }
                        if (canMove(Direction.SOUTHEAST)) {
                            score = 0;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot64 != null && RobotTracker.bot64.team == team) score++;
                            if (RobotTracker.bot63 != null && RobotTracker.bot63.team == team) score++;
                            if (RobotTracker.bot62 != null && RobotTracker.bot62.team == team) score++;
                            if (RobotTracker.bot52 != null && RobotTracker.bot52.team == team) score++;
                            if (RobotTracker.bot42 != null && RobotTracker.bot42.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.SOUTHEAST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.SOUTHEAST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.SOUTHEAST;
                            }
                        }
                        if (canMove(Direction.SOUTH)) {
                            score = 0;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot64 != null && RobotTracker.bot64.team == team) score++;
                            if (RobotTracker.bot63 != null && RobotTracker.bot63.team == team) score++;
                            if (RobotTracker.bot62 != null && RobotTracker.bot62.team == team) score++;
                            if (RobotTracker.bot52 != null && RobotTracker.bot52.team == team) score++;
                            if (RobotTracker.bot42 != null && RobotTracker.bot42.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.SOUTH));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.SOUTH).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.SOUTH;
                            }
                        }
                        break;
                    case 4:
                        if (canMove(Direction.SOUTHEAST)) {
                            score = 0;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot53 != null && RobotTracker.bot53.team == team) score++;
                            if (RobotTracker.bot52 != null && RobotTracker.bot52.team == team) score++;
                            if (RobotTracker.bot42 != null && RobotTracker.bot42.team == team) score++;
                            if (RobotTracker.bot32 != null && RobotTracker.bot32.team == team) score++;
                            if (RobotTracker.bot33 != null && RobotTracker.bot33.team == team) score++;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.SOUTHEAST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.SOUTHEAST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.SOUTHEAST;
                            }
                        }
                        if (canMove(Direction.SOUTH)) {
                            score = 0;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot53 != null && RobotTracker.bot53.team == team) score++;
                            if (RobotTracker.bot52 != null && RobotTracker.bot52.team == team) score++;
                            if (RobotTracker.bot42 != null && RobotTracker.bot42.team == team) score++;
                            if (RobotTracker.bot32 != null && RobotTracker.bot32.team == team) score++;
                            if (RobotTracker.bot33 != null && RobotTracker.bot33.team == team) score++;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.SOUTH));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.SOUTH).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.SOUTH;
                            }
                        }
                        if (canMove(Direction.SOUTHWEST)) {
                            score = 0;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot54 != null && RobotTracker.bot54.team == team) score++;
                            if (RobotTracker.bot53 != null && RobotTracker.bot53.team == team) score++;
                            if (RobotTracker.bot52 != null && RobotTracker.bot52.team == team) score++;
                            if (RobotTracker.bot42 != null && RobotTracker.bot42.team == team) score++;
                            if (RobotTracker.bot32 != null && RobotTracker.bot32.team == team) score++;
                            if (RobotTracker.bot33 != null && RobotTracker.bot33.team == team) score++;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.SOUTHWEST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.SOUTHWEST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.SOUTHWEST;
                            }
                        }
                        break;
                    case 5:
                        if (canMove(Direction.SOUTH)) {
                            score = 0;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot42 != null && RobotTracker.bot42.team == team) score++;
                            if (RobotTracker.bot32 != null && RobotTracker.bot32.team == team) score++;
                            if (RobotTracker.bot22 != null && RobotTracker.bot22.team == team) score++;
                            if (RobotTracker.bot23 != null && RobotTracker.bot23.team == team) score++;
                            if (RobotTracker.bot24 != null && RobotTracker.bot24.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.SOUTH));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.SOUTH).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.SOUTH;
                            }
                        }
                        if (canMove(Direction.SOUTHWEST)) {
                            score = 0;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot42 != null && RobotTracker.bot42.team == team) score++;
                            if (RobotTracker.bot32 != null && RobotTracker.bot32.team == team) score++;
                            if (RobotTracker.bot22 != null && RobotTracker.bot22.team == team) score++;
                            if (RobotTracker.bot23 != null && RobotTracker.bot23.team == team) score++;
                            if (RobotTracker.bot24 != null && RobotTracker.bot24.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.SOUTHWEST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.SOUTHWEST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.SOUTHWEST;
                            }
                        }
                        if (canMove(Direction.WEST)) {
                            score = 0;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot42 != null && RobotTracker.bot42.team == team) score++;
                            if (RobotTracker.bot32 != null && RobotTracker.bot32.team == team) score++;
                            if (RobotTracker.bot22 != null && RobotTracker.bot22.team == team) score++;
                            if (RobotTracker.bot23 != null && RobotTracker.bot23.team == team) score++;
                            if (RobotTracker.bot24 != null && RobotTracker.bot24.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.WEST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.WEST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.WEST;
                            }
                        }
                        break;
                    case 6:
                        if (canMove(Direction.SOUTHWEST)) {
                            score = 0;
                            if (RobotTracker.bot35 != null && RobotTracker.bot35.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot33 != null && RobotTracker.bot33.team == team) score++;
                            if (RobotTracker.bot23 != null && RobotTracker.bot23.team == team) score++;
                            if (RobotTracker.bot24 != null && RobotTracker.bot24.team == team) score++;
                            if (RobotTracker.bot25 != null && RobotTracker.bot25.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.SOUTHWEST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.SOUTHWEST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.SOUTHWEST;
                            }
                        }
                        if (canMove(Direction.WEST)) {
                            score = 0;
                            if (RobotTracker.bot35 != null && RobotTracker.bot35.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot33 != null && RobotTracker.bot33.team == team) score++;
                            if (RobotTracker.bot23 != null && RobotTracker.bot23.team == team) score++;
                            if (RobotTracker.bot24 != null && RobotTracker.bot24.team == team) score++;
                            if (RobotTracker.bot25 != null && RobotTracker.bot25.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.WEST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.WEST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.WEST;
                            }
                        }
                        if (canMove(Direction.NORTHWEST)) {
                            score = 0;
                            if (RobotTracker.bot35 != null && RobotTracker.bot35.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot43 != null && RobotTracker.bot43.team == team) score++;
                            if (RobotTracker.bot33 != null && RobotTracker.bot33.team == team) score++;
                            if (RobotTracker.bot23 != null && RobotTracker.bot23.team == team) score++;
                            if (RobotTracker.bot24 != null && RobotTracker.bot24.team == team) score++;
                            if (RobotTracker.bot25 != null && RobotTracker.bot25.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.NORTHWEST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.NORTHWEST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.NORTHWEST;
                            }
                        }
                        break;
                    case 7:
                        if (canMove(Direction.WEST)) {
                            score = 0;
                            if (RobotTracker.bot36 != null && RobotTracker.bot36.team == team) score++;
                            if (RobotTracker.bot46 != null && RobotTracker.bot46.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            if (RobotTracker.bot24 != null && RobotTracker.bot24.team == team) score++;
                            if (RobotTracker.bot25 != null && RobotTracker.bot25.team == team) score++;
                            if (RobotTracker.bot26 != null && RobotTracker.bot26.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.WEST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.WEST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.WEST;
                            }
                        }
                        if (canMove(Direction.NORTHWEST)) {
                            score = 0;
                            if (RobotTracker.bot36 != null && RobotTracker.bot36.team == team) score++;
                            if (RobotTracker.bot46 != null && RobotTracker.bot46.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            if (RobotTracker.bot24 != null && RobotTracker.bot24.team == team) score++;
                            if (RobotTracker.bot25 != null && RobotTracker.bot25.team == team) score++;
                            if (RobotTracker.bot26 != null && RobotTracker.bot26.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.NORTHWEST));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.NORTHWEST).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.NORTHWEST;
                            }
                        }
                        if (canMove(Direction.NORTH)) {
                            score = 0;
                            if (RobotTracker.bot36 != null && RobotTracker.bot36.team == team) score++;
                            if (RobotTracker.bot46 != null && RobotTracker.bot46.team == team) score++;
                            if (RobotTracker.bot45 != null && RobotTracker.bot45.team == team) score++;
                            if (RobotTracker.bot44 != null && RobotTracker.bot44.team == team) score++;
                            if (RobotTracker.bot34 != null && RobotTracker.bot34.team == team) score++;
                            if (RobotTracker.bot24 != null && RobotTracker.bot24.team == team) score++;
                            if (RobotTracker.bot25 != null && RobotTracker.bot25.team == team) score++;
                            if (RobotTracker.bot26 != null && RobotTracker.bot26.team == team) score++;
                            MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.NORTH));
                            if (info.getPaint() == PaintType.EMPTY) score++;
                            if (info.getPaint().isEnemy()) score *= 2;
                            if (cellPenalty != null) score += cellPenalty.apply(info);
                            score *= 1000000;
                            score += Game.pos.add(Direction.NORTH).distanceSquaredTo(target) * 10;
                            score += trng.nextInt(10);
                            if (score < bestScore) {
                                bestScore = score;
                                best = Direction.NORTH;
                            }
                        }
                        break;
                }
            }

            // end

            if(best != null) {
                indicate("HERE");
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
        if(alwaysLeftTurn && leftTurn == false) {
            stackSize = 0;
            topDir = bottomDir;
            leftTurn = true;
        }
        int iters = 0;
        while (!canMove(topDir))  {
            MapLocation nextLoc = Game.pos.add(topDir);
            // avoid following the edge of the map
            if(nextLoc.x < 0 || nextLoc.y < 0 || nextLoc.x >= mapWidth || nextLoc.y >= mapHeight) {
                leftTurn = !leftTurn;
                topDir = bottomDir;
                stackSize = 0;
                continue;
            }
            if (stackSize <= 1 && lastNonzeroStackTime < rc.getRoundNum() - 4) {
                leftTurn = trng.nextInt(0, 1) == 0;
                stackSize = 0;
                topDir = bottomDir;
            }
            if(alwaysLeftTurn) leftTurn = true;
            if (leftTurn) topDir = topDir.rotateLeft();
            else topDir = topDir.rotateRight();
            stackSize++;
            iters++;
            if (iters == 8) {
                return null;
            }
        }
        // assert canMove(topDir);
        // rc.setIndicatorLine(Game.pos, Game.pos.add(topDir), 0, 0, 255);
        // rc.setIndicatorString(topDir + " " + bottomDir + " " + leftTurn + " " + stackSize);
        return topDir;
    }

    public void emergencyMove(MapLocation to) throws GameActionException {
        // super jank workaround
        GamePredicate<MapInfo> opred = avoid;
        avoid = m -> false;
        Direction dir = getMove(to);
        if (dir != null && rc.canMove(dir)) {
            makeMove(dir);
        }
        avoid = opred;
    }

    public Direction makeMove(MapLocation to) throws GameActionException {
        Direction dir = null;
        if (rc.isMovementReady()) {
            boolean hasMove = false;
            for(Direction poss : directions) {
                MapLocation loc = rc.getLocation().add(poss);
                if(rc.canMove(poss) && !avoid.test(mapInfos[loc.x][loc.y])) {
                    hasMove = true;
                    break;
                }
            }
            if(hasMove) {
                makeMove(getMove(to));
            } else {
                // emergency!!!
                // System.out.println("emergency!!!");
                if (avoid.test(rc.senseMapInfo(Game.pos))) {
                    emergencyMove(to);
                }
            }
        }
        // rc.setIndicatorString(topDir.toString() + " " + bottomDir.toString() + " " + leftTurn + " " + stackSize);
        rc.setIndicatorLine(Game.pos, to, 0, 255, 0);
        return dir;
    }

    public void makeMove(Direction dir) throws GameActionException {
        if (dir != null && rc.canMove(dir)) {
            // if(stackSize > 0) {
            //     // indicate("LEFTTURNHIST " + Game.pos.toString() + " " + leftTurn);
            //     leftTurnHist.put(Game.pos, leftTurn);
            // }
            ((Agent) bot).move(dir);
        }
        expected = Game.pos;
        if(stackSize > 0) {
            lastNonzeroStackTime = rc.getRoundNum();
        }
        // indicate("LeftTurn: " + leftTurn);
    }

    public void reset() {
        reset = true;
    }
}
