package caterpillow.robot.agents.mopper;

import java.util.ArrayList;
import java.util.List;

import battlecode.common.*;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.util.Util.*;

import caterpillow.packet.packets.StrategyPacket;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.agents.Agent;
import caterpillow.tracking.CellTracker;
import caterpillow.tracking.RobotTracker;
import caterpillow.util.*;

public class Mopper extends Agent {

    Mopper bot;
    List<MapLocation> enemyLocs;
    MapLocation spawnLoc;

    // public RobotInfo getBestTarget(GamePredicate<RobotInfo> pred) throws GameActionException {
    //     return getBestRobot((a, b) -> {
    //             int a1 = a.getType().ordinal();
    //             int b1 = b.getType().ordinal();
    //             int h1 = a.getPaintAmount();
    //             int h2 = b.getPaintAmount();
    //             if (a1 == b1) {
    //                 if (h1 > h2) return b;
    //                 else return a;
    //             } else {
    //                 if (a1 < b1) return a;
    //                 else return b;
    //             }
    //         }, e -> !isFriendly(e) && e.getType().isRobotType() && pred.test(e));
    // }

    // public RobotInfo getBestTarget() throws GameActionException {
    //     return getBestTarget(e -> true);
    // }

    public List<Direction> possibleMovements() throws GameActionException {
        List<Direction> dirs = new ArrayList<>();
        if (!rc.isMovementReady()) return dirs;


        // less bytecode than a loop??? idk
        if (rc.canMove(Direction.NORTH)) dirs.add(Direction.NORTH);
        if (rc.canMove(Direction.SOUTH)) dirs.add(Direction.SOUTH);
        if (rc.canMove(Direction.EAST)) dirs.add(Direction.EAST);
        if (rc.canMove(Direction.WEST)) dirs.add(Direction.WEST);
        if (rc.canMove(Direction.NORTHWEST)) dirs.add(Direction.NORTHWEST);
        if (rc.canMove(Direction.NORTHEAST)) dirs.add(Direction.NORTHEAST);
        if (rc.canMove(Direction.SOUTHWEST)) dirs.add(Direction.SOUTHWEST);
        if (rc.canMove(Direction.SOUTHEAST)) dirs.add(Direction.SOUTHEAST);
        return dirs;
    }

    private int priority(UnitType type) {
        switch (type.ordinal()) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 0;
            default:
                throw new IllegalArgumentException("Invalid unit type");
        }
    }

    private boolean isBotAbleToAttack(RobotInfo info) {
        switch (info.getType().ordinal()) {
            case 0:
                return info.getPaintAmount() >= 5;
            case 1:
                return info.getPaintAmount() >= 50;
            case 2:
                return info.getPaintAmount() > 0;
            default:
                throw new IllegalArgumentException("Invalid unit type");
        }
    }

    /*

orth_directions = [
    (1, 0, "EAST"),
    (0, 1, "NORTH"),
    (-1, 0, "WEST"),
    (0, -1, "SOUTH")
]

directions = [
    (0, 0, "CENTER"),
    (1, 0, "EAST"),
    (1, 1, "NORTHEAST"),
    (0, 1, "NORTH"),
    (-1, 1, "NORTHWEST"),
    (-1, 0, "WEST"),
    (-1, -1, "SOUTHWEST"),
    (0, -1, "SOUTH"),
    (1, -1, "SOUTHEAST")
]



for x in range(7):
    for y in range(7):
        print(f"int cell{x}{y} = 0;")

print("int x = Game.pos.x - 3;")
print("int y = Game.pos.y - 3;")
x = -3
y = -3
for i in range(-3, 4):
    for j in range(-3, 4):
        if x != 0 or y != 0:
            checks = []
            if x > 0:
                checks.append(f"x < Game.mapWidth")
            elif x < 0:
                checks.append(f"x >= 0")

            if y > 0:
                checks.append(f"y < Game.mapHeight")
            elif y < 0:
                checks.append(f"y >= 0")

            print(f"if ({' && '.join(checks)}) {{")

            #

            print(f"\tRobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));")
            print("\tif (info != null && info.getTeam() == rc.getTeam() && info.getType().isRobotType()) {")
            print(f"\t\tcell{x + 3}{y + 3} = 1;")
            print("\t}")

            #

            print("}")

        if j < 3:
            if i % 2 == 1:
                print("y++;")
                y += 1
            else:
                print("y--;")
                y -= 1
        else:
            print("x++;")
            x += 1

print("Pair<Direction, Direction> best = null;")
print("int bestScore = 0;")

print("int score = 0;")
for dx, dy, dir_str in directions:
    print(f"if (rc.canMove(Direction.{dir_str})) {{")
    for i in range(4):
        dx2, dy2, dir_str2 = orth_directions[i]
        dx3, dy3, dir_str3 = orth_directions[(i + 1) % 4] # turn left
        dx4, dy4, dir_str4 = orth_directions[(i + 3) % 4] # turn right

        cells = []
        x = dx + dx2
        y = dy + dy2
        cells.append((x, y))
        cells.append((x + dx3, y + dy3))
        cells.append((x + dx4, y + dy4))
        x += dx2
        y += dy2
        cells.append((x, y))
        cells.append((x + dx3, y + dy3))
        cells.append((x + dx4, y + dy4))

        print(f"score = {" + ".join([f"cell{x + 3}{y + 3}" for x, y in cells])};")
        print("if (score > bestScore) {")
        print("\tbestScore = score;")
        print(f"\tbest = new Pair<>(Direction.{dir_str}, Direction.{dir_str2});")
        print("}")
    print("}")


     */

    public boolean doBestAttack() throws GameActionException {
        if (!rc.isActionReady()) return false;
//        Profiler.begin();
        indicate("try attack");
        bot.lastMove = true;

        GameBinaryOperator<RobotInfo> getBest = (a, b) -> {
            if (a == null) return b;
            if (b == null) return a;
            int p1 = priority(a.getType());
            int p2 = priority(b.getType());
            int h1 = a.getPaintAmount();
            int h2 = b.getPaintAmount();
            if (p1 == p2) {
                // prioritise first argument
                if (h1 >= h2) return b;
                else return a;
            } else {
                if (h1 >= h2) return a;
                else return b;
            }
        };

        RobotInfo target = null;
        Direction moveDir = null;


//        Profiler.begin();
        for (Direction dir : Direction.values()) {
            if (dir == Direction.CENTER || rc.canMove(dir)) {
                // allow any move for the glorious mop + steal combo
                MapLocation curPos = Game.pos.add(dir);
                GamePredicate<RobotInfo> pred = e -> !Util.isFriendly(e) && e.getLocation().distanceSquaredTo(curPos) <= 2 && rc.senseMapInfo(e.getLocation()).getPaint().isEnemy() && e.getType().isRobotType() && isBotAbleToAttack(e);
                RobotInfo tmp = RobotTracker.getBestRobotSquare(getBest, pred, dir.dx, dir.dy);
                if (target != getBest.apply(target, tmp)) {
                    target = tmp;
                    moveDir = dir;
                }
            }
        }
//        Profiler.end("bruh moment");

        if (target != null) {
            if (moveDir != Direction.CENTER) bot.move(moveDir);
            rc.attack(target.getLocation());
            indicate("double trouble attak");
//            Profiler.end("regular attacks");
            return true;
        }

        // just attack a dude case
//        Profiler.begin();
        for (Direction dir : Direction.values()) {
            if (dir == Direction.CENTER || rc.canMove(dir)) {
                // allow any move for steal combo
                MapLocation curPos = Game.pos.add(dir);
                // dont require enemy paint
                GamePredicate<RobotInfo> pred = e -> !Util.isFriendly(e) && e.getLocation().distanceSquaredTo(curPos) <= 2 && e.getType().isRobotType() && isBotAbleToAttack(e);
                RobotInfo tmp = RobotTracker.getBestRobotSquare(getBest, pred, dir.dx, dir.dy);
                if (target != getBest.apply(target, tmp)) {
                    target = tmp;
                    moveDir = dir;
                }
            }
        }
//        Profiler.end("minor bruh moment");

//        Profiler.end("regular attacks");
        // begin cursed code
//        Profiler.begin();

        int cell00 = 0;
        int cell01 = 0;
        int cell02 = 0;
        int cell03 = 0;
        int cell04 = 0;
        int cell05 = 0;
        int cell06 = 0;
        int cell10 = 0;
        int cell11 = 0;
        int cell12 = 0;
        int cell13 = 0;
        int cell14 = 0;
        int cell15 = 0;
        int cell16 = 0;
        int cell20 = 0;
        int cell21 = 0;
        int cell22 = 0;
        int cell23 = 0;
        int cell24 = 0;
        int cell25 = 0;
        int cell26 = 0;
        int cell30 = 0;
        int cell31 = 0;
        int cell32 = 0;
        int cell33 = 0;
        int cell34 = 0;
        int cell35 = 0;
        int cell36 = 0;
        int cell40 = 0;
        int cell41 = 0;
        int cell42 = 0;
        int cell43 = 0;
        int cell44 = 0;
        int cell45 = 0;
        int cell46 = 0;
        int cell50 = 0;
        int cell51 = 0;
        int cell52 = 0;
        int cell53 = 0;
        int cell54 = 0;
        int cell55 = 0;
        int cell56 = 0;
        int cell60 = 0;
        int cell61 = 0;
        int cell62 = 0;
        int cell63 = 0;
        int cell64 = 0;
        int cell65 = 0;
        int cell66 = 0;
        int x = Game.pos.x - 3;
        int y = Game.pos.y - 3;
        if (x >= 0 && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell00 = 1;
            }
        }
        y++;
        if (x >= 0 && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell01 = 1;
            }
        }
        y++;
        if (x >= 0 && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell02 = 1;
            }
        }
        y++;
        if (x >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell03 = 1;
            }
        }
        y++;
        if (x >= 0 && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell04 = 1;
            }
        }
        y++;
        if (x >= 0 && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell05 = 1;
            }
        }
        y++;
        if (x >= 0 && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell06 = 1;
            }
        }
        x++;
        if (x >= 0 && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell16 = 1;
            }
        }
        y--;
        if (x >= 0 && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell15 = 1;
            }
        }
        y--;
        if (x >= 0 && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell14 = 1;
            }
        }
        y--;
        if (x >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell13 = 1;
            }
        }
        y--;
        if (x >= 0 && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell12 = 1;
            }
        }
        y--;
        if (x >= 0 && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell11 = 1;
            }
        }
        y--;
        if (x >= 0 && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell10 = 1;
            }
        }
        x++;
        if (x >= 0 && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell20 = 1;
            }
        }
        y++;
        if (x >= 0 && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell21 = 1;
            }
        }
        y++;
        if (x >= 0 && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell22 = 1;
            }
        }
        y++;
        if (x >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell23 = 1;
            }
        }
        y++;
        if (x >= 0 && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell24 = 1;
            }
        }
        y++;
        if (x >= 0 && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell25 = 1;
            }
        }
        y++;
        if (x >= 0 && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell26 = 1;
            }
        }
        x++;
        if (y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell36 = 1;
            }
        }
        y--;
        if (y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell35 = 1;
            }
        }
        y--;
        if (y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell34 = 1;
            }
        }
        y--;
        y--;
        if (y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell32 = 1;
            }
        }
        y--;
        if (y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell31 = 1;
            }
        }
        y--;
        if (y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell30 = 1;
            }
        }
        x++;
        if (x < Game.mapWidth && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell40 = 1;
            }
        }
        y++;
        if (x < Game.mapWidth && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell41 = 1;
            }
        }
        y++;
        if (x < Game.mapWidth && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell42 = 1;
            }
        }
        y++;
        if (x < Game.mapWidth) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell43 = 1;
            }
        }
        y++;
        if (x < Game.mapWidth && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell44 = 1;
            }
        }
        y++;
        if (x < Game.mapWidth && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell45 = 1;
            }
        }
        y++;
        if (x < Game.mapWidth && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell46 = 1;
            }
        }
        x++;
        if (x < Game.mapWidth && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell56 = 1;
            }
        }
        y--;
        if (x < Game.mapWidth && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell55 = 1;
            }
        }
        y--;
        if (x < Game.mapWidth && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell54 = 1;
            }
        }
        y--;
        if (x < Game.mapWidth) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell53 = 1;
            }
        }
        y--;
        if (x < Game.mapWidth && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell52 = 1;
            }
        }
        y--;
        if (x < Game.mapWidth && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell51 = 1;
            }
        }
        y--;
        if (x < Game.mapWidth && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell50 = 1;
            }
        }
        x++;
        if (x < Game.mapWidth && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell60 = 1;
            }
        }
        y++;
        if (x < Game.mapWidth && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell61 = 1;
            }
        }
        y++;
        if (x < Game.mapWidth && y >= 0) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell62 = 1;
            }
        }
        y++;
        if (x < Game.mapWidth) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell63 = 1;
            }
        }
        y++;
        if (x < Game.mapWidth && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell64 = 1;
            }
        }
        y++;
        if (x < Game.mapWidth && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell65 = 1;
            }
        }
        y++;
        if (x < Game.mapWidth && y < Game.mapHeight) {
            RobotInfo info = rc.senseRobotAtLocation(new MapLocation(x, y));
            if (info != null && info.getTeam() != rc.getTeam() && info.getType().isRobotType()) {
                cell66 = 1;
            }
        }
        x++;
        Pair<Direction, Direction> best = null;
        int bestScore = 0;
        int score = 0;
        if (rc.canMove(Direction.CENTER)) {
            score = cell43 + cell44 + cell42 + cell53 + cell54 + cell52;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.CENTER, Direction.EAST);
            }
            score = cell34 + cell24 + cell44 + cell35 + cell25 + cell45;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.CENTER, Direction.NORTH);
            }
            score = cell23 + cell22 + cell24 + cell13 + cell12 + cell14;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.CENTER, Direction.WEST);
            }
            score = cell32 + cell42 + cell22 + cell31 + cell41 + cell21;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.CENTER, Direction.SOUTH);
            }
        }
        if (rc.canMove(Direction.EAST)) {
            score = cell53 + cell54 + cell52 + cell63 + cell64 + cell62;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.EAST, Direction.EAST);
            }
            score = cell44 + cell34 + cell54 + cell45 + cell35 + cell55;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.EAST, Direction.NORTH);
            }
            score = cell33 + cell32 + cell34 + cell23 + cell22 + cell24;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.EAST, Direction.WEST);
            }
            score = cell42 + cell52 + cell32 + cell41 + cell51 + cell31;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.EAST, Direction.SOUTH);
            }
        }
        if (rc.canMove(Direction.NORTHEAST)) {
            score = cell54 + cell55 + cell53 + cell64 + cell65 + cell63;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.NORTHEAST, Direction.EAST);
            }
            score = cell45 + cell35 + cell55 + cell46 + cell36 + cell56;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.NORTHEAST, Direction.NORTH);
            }
            score = cell34 + cell33 + cell35 + cell24 + cell23 + cell25;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.NORTHEAST, Direction.WEST);
            }
            score = cell43 + cell53 + cell33 + cell42 + cell52 + cell32;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.NORTHEAST, Direction.SOUTH);
            }
        }
        if (rc.canMove(Direction.NORTH)) {
            score = cell44 + cell45 + cell43 + cell54 + cell55 + cell53;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.NORTH, Direction.EAST);
            }
            score = cell35 + cell25 + cell45 + cell36 + cell26 + cell46;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.NORTH, Direction.NORTH);
            }
            score = cell24 + cell23 + cell25 + cell14 + cell13 + cell15;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.NORTH, Direction.WEST);
            }
            score = cell33 + cell43 + cell23 + cell32 + cell42 + cell22;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.NORTH, Direction.SOUTH);
            }
        }
        if (rc.canMove(Direction.NORTHWEST)) {
            score = cell34 + cell35 + cell33 + cell44 + cell45 + cell43;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.NORTHWEST, Direction.EAST);
            }
            score = cell25 + cell15 + cell35 + cell26 + cell16 + cell36;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.NORTHWEST, Direction.NORTH);
            }
            score = cell14 + cell13 + cell15 + cell04 + cell03 + cell05;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.NORTHWEST, Direction.WEST);
            }
            score = cell23 + cell33 + cell13 + cell22 + cell32 + cell12;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.NORTHWEST, Direction.SOUTH);
            }
        }
        if (rc.canMove(Direction.WEST)) {
            score = cell33 + cell34 + cell32 + cell43 + cell44 + cell42;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.WEST, Direction.EAST);
            }
            score = cell24 + cell14 + cell34 + cell25 + cell15 + cell35;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.WEST, Direction.NORTH);
            }
            score = cell13 + cell12 + cell14 + cell03 + cell02 + cell04;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.WEST, Direction.WEST);
            }
            score = cell22 + cell32 + cell12 + cell21 + cell31 + cell11;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.WEST, Direction.SOUTH);
            }
        }
        if (rc.canMove(Direction.SOUTHWEST)) {
            score = cell32 + cell33 + cell31 + cell42 + cell43 + cell41;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.SOUTHWEST, Direction.EAST);
            }
            score = cell23 + cell13 + cell33 + cell24 + cell14 + cell34;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.SOUTHWEST, Direction.NORTH);
            }
            score = cell12 + cell11 + cell13 + cell02 + cell01 + cell03;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.SOUTHWEST, Direction.WEST);
            }
            score = cell21 + cell31 + cell11 + cell20 + cell30 + cell10;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.SOUTHWEST, Direction.SOUTH);
            }
        }
        if (rc.canMove(Direction.SOUTH)) {
            score = cell42 + cell43 + cell41 + cell52 + cell53 + cell51;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.SOUTH, Direction.EAST);
            }
            score = cell33 + cell23 + cell43 + cell34 + cell24 + cell44;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.SOUTH, Direction.NORTH);
            }
            score = cell22 + cell21 + cell23 + cell12 + cell11 + cell13;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.SOUTH, Direction.WEST);
            }
            score = cell31 + cell41 + cell21 + cell30 + cell40 + cell20;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.SOUTH, Direction.SOUTH);
            }
        }
        if (rc.canMove(Direction.SOUTHEAST)) {
            score = cell52 + cell53 + cell51 + cell62 + cell63 + cell61;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.SOUTHEAST, Direction.EAST);
            }
            score = cell43 + cell33 + cell53 + cell44 + cell34 + cell54;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.SOUTHEAST, Direction.NORTH);
            }
            score = cell32 + cell31 + cell33 + cell22 + cell21 + cell23;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.SOUTHEAST, Direction.WEST);
            }
            score = cell41 + cell51 + cell31 + cell40 + cell50 + cell30;
            if (score > bestScore) {
                bestScore = score;
                best = new Pair<>(Direction.SOUTHEAST, Direction.SOUTH);
            }
        }

        // end cursed code
//        Profiler.end("mop swing");

        if (target == null || (bestScore >= 2 && getPaintLevel() >= 0.5)) {
            if (best != null) {
                if (best.first != null && best.first != Direction.CENTER) bot.move(best.first);
                rc.mopSwing(best.second);
                indicate("mop swing!");
                return true;
            }
        } else {
            if (moveDir != Direction.CENTER) bot.move(moveDir);
            rc.attack(target.getLocation());
            indicate("regular attak");
            return true;
        }

//        Profiler.begin();

        // just mop :(
        GameBinaryOperator<MapInfo> getBestCell = (c1, c2) -> {
            if (c1 == null) return c2;
            if (c2 == null) return c1;
            return c1;
        };

        GamePredicate<MapInfo> cellPred = c -> c.getPaint().isEnemy();

        MapInfo bestCell = null;
        moveDir = null;

        for (Direction dir : Direction.values()) {
            if (dir == Direction.CENTER || rc.canMove(dir)) {
                MapInfo tmp = CellTracker.getBestCellSquare(getBestCell, cellPred, Game.pos.x + dir.dx, Game.pos.y + dir.dy);
                if (bestCell != getBestCell.apply(bestCell, tmp)) {
                    bestCell = tmp;
                    moveDir = dir;
                }
            }
        }

//        Profiler.end("mop");

        if (bestCell != null) {
            if (moveDir != null && moveDir != Direction.CENTER) bot.move(moveDir);
            rc.attack(bestCell.getMapLocation());
            indicate("mop");
            return true;
        }
        indicate("no attack :(");
        return false;
    }

    @Override
    public void init() throws GameActionException {
        super.init();
        pathfinder = new BugnavPathfinder(c -> c.getPaint().isEnemy() || isInDanger(c.getMapLocation()));
        primaryStrategy = new EmptyStrategy();
        bot = (Mopper) Game.bot;
    }

    @Override
    public void runTick() throws GameActionException {
        super.runTick();
        // if spawn is surrounded by enemy paint (i.e. no messaging) spawn some moppers to clean it up
        // TODO: make this a proper fix
        if (primaryStrategy instanceof EmptyStrategy && Game.time > 4) {
            if (home != null) {
                Game.origin = home;
            } else {
                Game.origin = rc.getLocation();
            }
            primaryStrategy = new MopperOffenceStrategy();
        }

        // all moppers should donate. splashers 1st priority, soldiers 2nd
        RobotInfo[] bots = rc.senseNearbyRobots(2);
        for (RobotInfo bot : bots) {
            if (bot.getType()==UnitType.SPLASHER && bot.getPaintAmount() < bot.getType().paintCapacity) {
                donate(bot);
            }
        }
        for (RobotInfo bot : bots) {
            if (bot.getType()==UnitType.SOLDIER && bot.getPaintAmount() < bot.getType().paintCapacity) {
                donate(bot);
            }
        }
    }

    public static final int DEFENCE_STRAT = 0, OFFENCE_STRAT = 1, RESPAWN_STRAT = 2, PASSIVE_STRAT = 3;

    @Override
    public void handleStrategyPacket(StrategyPacket packet, int senderID) throws GameActionException {
        super.handleStrategyPacket(packet, senderID);
        switch (packet.strategyID) {
        case DEFENCE_STRAT:
            primaryStrategy = new MopperDefenceStrategy();
            break;
        case OFFENCE_STRAT:
            primaryStrategy = new MopperOffenceStrategy();
            break;
        case RESPAWN_STRAT:
            primaryStrategy = new MopperOffenceStrategy();
            secondaryStrategy = new MopperRespawnStrategy();
            break;
        case PASSIVE_STRAT:
            primaryStrategy = new MopperPassiveStrategy();
            break;
        }
    }
}
