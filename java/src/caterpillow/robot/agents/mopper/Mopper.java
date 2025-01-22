package caterpillow.robot.agents.mopper;

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
    
    private static RobotInfo comp1(RobotInfo a, RobotInfo b) {
        switch (a.getType().ordinal() * 3 + b.getType().ordinal()) {
            case 1:
                return b;
            case 2:
                return a;
            case 3:
                return a;
            case 5:
                return a;
            case 6:
                return b;
            case 7:
                return b;
        }
        if (a.getPaintAmount() >= b.getPaintAmount()) return b;
        else return a;
    }
    
    private static boolean pred1(RobotInfo e) throws GameActionException {
        return !Util.isFriendly(e) && rc.senseMapInfo(e.getLocation()).getPaint().isEnemy() && e.getType().isRobotType() && isBotAbleToAttack(e);
    }
    
    private static boolean pred2(RobotInfo e) throws GameActionException {
        return !Util.isFriendly(e) && e.getType().isRobotType() && isBotAbleToAttack(e);
    }

    // i dont think this updates after u mmove so be careful!!!
    public static RobotInfo getBestRobotSquare1(int x, int y) throws GameActionException {
        assert Game.bot instanceof Agent && ((Agent) Game.bot).lastMove;
        RobotInfo best = null, info;
        x += 3;
        y += 3;
        info = RobotTracker.exists[x][y];
        if (info != null && pred1(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        y++;
        info = RobotTracker.exists[x][y];
        if (info != null && pred1(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        y++;
        info = RobotTracker.exists[x][y];
        if (info != null && pred1(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        x++;
        info = RobotTracker.exists[x][y];
        if (info != null && pred1(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        y--;
        y--;
        info = RobotTracker.exists[x][y];
        if (info != null && pred1(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        x++;
        info = RobotTracker.exists[x][y];
        if (info != null && pred1(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        y++;
        info = RobotTracker.exists[x][y];
        if (info != null && pred1(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        y++;
        info = RobotTracker.exists[x][y];
        if (info != null && pred1(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        x++;
        return best;
    }

    public static RobotInfo getBestRobotSquare2(int x, int y) throws GameActionException {
        assert Game.bot instanceof Agent && ((Agent) Game.bot).lastMove;
        RobotInfo best = null, info;
        x += 3;
        y += 3;
        info = RobotTracker.exists[x][y];
        if (info != null && pred2(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        y++;
        info = RobotTracker.exists[x][y];
        if (info != null && pred2(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        y++;
        info = RobotTracker.exists[x][y];
        if (info != null && pred2(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        x++;
        info = RobotTracker.exists[x][y];
        if (info != null && pred2(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        y--;
        y--;
        info = RobotTracker.exists[x][y];
        if (info != null && pred2(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        x++;
        info = RobotTracker.exists[x][y];
        if (info != null && pred2(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        y++;
        info = RobotTracker.exists[x][y];
        if (info != null && pred2(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        y++;
        info = RobotTracker.exists[x][y];
        if (info != null && pred2(info)) {
            if (best == null) best = info;
            else best = comp1(best, info);
        }
        x++;
        return best;
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

    private static boolean isBotAbleToAttack(RobotInfo info) {
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

print("Team me = rc.getTeam();")
for x in range(7):
    for y in range(7):
        bot = f"RobotTracker.bot{x + 1}{y + 1}"
        print(f"int cell{x}{y} = ({bot} != null && {bot}.getTeam() != me && {bot}.getType().isRobotType() ? 1 : 0);")

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

    boolean dbg = false;
    public boolean doBestAttack() throws GameActionException {
        if (!rc.isActionReady()) return false;
        if (dbg) Profiler.begin();
        indicate("try attack");
        bot.lastMove = true;

        Direction[] dirs = new Direction[9];
        int dcnt = 0;
        for (Direction dir : Direction.values()) {
            if (dir != Direction.CENTER && rc.canMove(dir)) {
                dirs[dcnt++] = dir;
            }
        }

        RobotInfo target = null;
        Direction moveDir = null;

//        if (dbg) Profiler.begin();
        for (int i = dcnt - 1; i >= 0; i--) {
            Direction dir = dirs[i];
            if (dir == Direction.CENTER || rc.canMove(dir)) {
                // allow any move for the glorious mop + steal combo
                RobotInfo tmp = getBestRobotSquare1(dir.dx, dir.dy);
                if (tmp != null && (target == null || target != comp1(target, tmp))) {
                    target = tmp;
                    moveDir = dir;
                }
            }
        }
        if (dbg) Profiler.end("bruh moment");

        if (target != null) {
            if (moveDir != Direction.CENTER) bot.move(moveDir);
            rc.attack(target.getLocation());
            rc.setIndicatorDot(target.getLocation(), 255, 0, 0);
            indicate("double trouble attak");
//            if (dbg) Profiler.end("regular attacks");
            return true;
        }

        // just attack a dude case
        if (dbg) Profiler.begin();
        for (int i = dcnt - 1; i >= 0; i--) {
            Direction dir = dirs[i];
            if (dir == Direction.CENTER || rc.canMove(dir)) {
                // allow any move for steal combo
                // dont require enemy paint
                RobotInfo tmp = getBestRobotSquare2(dir.dx, dir.dy);
                if (tmp != null && (target == null || target != comp1(target, tmp))) {
                    target = tmp;
                    moveDir = dir;
                }
            }
        }
        if (dbg) Profiler.end("minor bruh moment");

//        if (dbg) Profiler.end("regular attacks");
        // begin cursed code
        if (dbg) Profiler.begin();

        Team me = rc.getTeam();
        int cell00 = (RobotTracker.bot11 != null && RobotTracker.bot11.getTeam() != me && RobotTracker.bot11.getType().isRobotType() ? 1 : 0);
        int cell01 = (RobotTracker.bot12 != null && RobotTracker.bot12.getTeam() != me && RobotTracker.bot12.getType().isRobotType() ? 1 : 0);
        int cell02 = (RobotTracker.bot13 != null && RobotTracker.bot13.getTeam() != me && RobotTracker.bot13.getType().isRobotType() ? 1 : 0);
        int cell03 = (RobotTracker.bot14 != null && RobotTracker.bot14.getTeam() != me && RobotTracker.bot14.getType().isRobotType() ? 1 : 0);
        int cell04 = (RobotTracker.bot15 != null && RobotTracker.bot15.getTeam() != me && RobotTracker.bot15.getType().isRobotType() ? 1 : 0);
        int cell05 = (RobotTracker.bot16 != null && RobotTracker.bot16.getTeam() != me && RobotTracker.bot16.getType().isRobotType() ? 1 : 0);
        int cell06 = (RobotTracker.bot17 != null && RobotTracker.bot17.getTeam() != me && RobotTracker.bot17.getType().isRobotType() ? 1 : 0);
        int cell10 = (RobotTracker.bot21 != null && RobotTracker.bot21.getTeam() != me && RobotTracker.bot21.getType().isRobotType() ? 1 : 0);
        int cell11 = (RobotTracker.bot22 != null && RobotTracker.bot22.getTeam() != me && RobotTracker.bot22.getType().isRobotType() ? 1 : 0);
        int cell12 = (RobotTracker.bot23 != null && RobotTracker.bot23.getTeam() != me && RobotTracker.bot23.getType().isRobotType() ? 1 : 0);
        int cell13 = (RobotTracker.bot24 != null && RobotTracker.bot24.getTeam() != me && RobotTracker.bot24.getType().isRobotType() ? 1 : 0);
        int cell14 = (RobotTracker.bot25 != null && RobotTracker.bot25.getTeam() != me && RobotTracker.bot25.getType().isRobotType() ? 1 : 0);
        int cell15 = (RobotTracker.bot26 != null && RobotTracker.bot26.getTeam() != me && RobotTracker.bot26.getType().isRobotType() ? 1 : 0);
        int cell16 = (RobotTracker.bot27 != null && RobotTracker.bot27.getTeam() != me && RobotTracker.bot27.getType().isRobotType() ? 1 : 0);
        int cell20 = (RobotTracker.bot31 != null && RobotTracker.bot31.getTeam() != me && RobotTracker.bot31.getType().isRobotType() ? 1 : 0);
        int cell21 = (RobotTracker.bot32 != null && RobotTracker.bot32.getTeam() != me && RobotTracker.bot32.getType().isRobotType() ? 1 : 0);
        int cell22 = (RobotTracker.bot33 != null && RobotTracker.bot33.getTeam() != me && RobotTracker.bot33.getType().isRobotType() ? 1 : 0);
        int cell23 = (RobotTracker.bot34 != null && RobotTracker.bot34.getTeam() != me && RobotTracker.bot34.getType().isRobotType() ? 1 : 0);
        int cell24 = (RobotTracker.bot35 != null && RobotTracker.bot35.getTeam() != me && RobotTracker.bot35.getType().isRobotType() ? 1 : 0);
        int cell25 = (RobotTracker.bot36 != null && RobotTracker.bot36.getTeam() != me && RobotTracker.bot36.getType().isRobotType() ? 1 : 0);
        int cell26 = (RobotTracker.bot37 != null && RobotTracker.bot37.getTeam() != me && RobotTracker.bot37.getType().isRobotType() ? 1 : 0);
        int cell30 = (RobotTracker.bot41 != null && RobotTracker.bot41.getTeam() != me && RobotTracker.bot41.getType().isRobotType() ? 1 : 0);
        int cell31 = (RobotTracker.bot42 != null && RobotTracker.bot42.getTeam() != me && RobotTracker.bot42.getType().isRobotType() ? 1 : 0);
        int cell32 = (RobotTracker.bot43 != null && RobotTracker.bot43.getTeam() != me && RobotTracker.bot43.getType().isRobotType() ? 1 : 0);
        int cell33 = (RobotTracker.bot44 != null && RobotTracker.bot44.getTeam() != me && RobotTracker.bot44.getType().isRobotType() ? 1 : 0);
        int cell34 = (RobotTracker.bot45 != null && RobotTracker.bot45.getTeam() != me && RobotTracker.bot45.getType().isRobotType() ? 1 : 0);
        int cell35 = (RobotTracker.bot46 != null && RobotTracker.bot46.getTeam() != me && RobotTracker.bot46.getType().isRobotType() ? 1 : 0);
        int cell36 = (RobotTracker.bot47 != null && RobotTracker.bot47.getTeam() != me && RobotTracker.bot47.getType().isRobotType() ? 1 : 0);
        int cell40 = (RobotTracker.bot51 != null && RobotTracker.bot51.getTeam() != me && RobotTracker.bot51.getType().isRobotType() ? 1 : 0);
        int cell41 = (RobotTracker.bot52 != null && RobotTracker.bot52.getTeam() != me && RobotTracker.bot52.getType().isRobotType() ? 1 : 0);
        int cell42 = (RobotTracker.bot53 != null && RobotTracker.bot53.getTeam() != me && RobotTracker.bot53.getType().isRobotType() ? 1 : 0);
        int cell43 = (RobotTracker.bot54 != null && RobotTracker.bot54.getTeam() != me && RobotTracker.bot54.getType().isRobotType() ? 1 : 0);
        int cell44 = (RobotTracker.bot55 != null && RobotTracker.bot55.getTeam() != me && RobotTracker.bot55.getType().isRobotType() ? 1 : 0);
        int cell45 = (RobotTracker.bot56 != null && RobotTracker.bot56.getTeam() != me && RobotTracker.bot56.getType().isRobotType() ? 1 : 0);
        int cell46 = (RobotTracker.bot57 != null && RobotTracker.bot57.getTeam() != me && RobotTracker.bot57.getType().isRobotType() ? 1 : 0);
        int cell50 = (RobotTracker.bot61 != null && RobotTracker.bot61.getTeam() != me && RobotTracker.bot61.getType().isRobotType() ? 1 : 0);
        int cell51 = (RobotTracker.bot62 != null && RobotTracker.bot62.getTeam() != me && RobotTracker.bot62.getType().isRobotType() ? 1 : 0);
        int cell52 = (RobotTracker.bot63 != null && RobotTracker.bot63.getTeam() != me && RobotTracker.bot63.getType().isRobotType() ? 1 : 0);
        int cell53 = (RobotTracker.bot64 != null && RobotTracker.bot64.getTeam() != me && RobotTracker.bot64.getType().isRobotType() ? 1 : 0);
        int cell54 = (RobotTracker.bot65 != null && RobotTracker.bot65.getTeam() != me && RobotTracker.bot65.getType().isRobotType() ? 1 : 0);
        int cell55 = (RobotTracker.bot66 != null && RobotTracker.bot66.getTeam() != me && RobotTracker.bot66.getType().isRobotType() ? 1 : 0);
        int cell56 = (RobotTracker.bot67 != null && RobotTracker.bot67.getTeam() != me && RobotTracker.bot67.getType().isRobotType() ? 1 : 0);
        int cell60 = (RobotTracker.bot71 != null && RobotTracker.bot71.getTeam() != me && RobotTracker.bot71.getType().isRobotType() ? 1 : 0);
        int cell61 = (RobotTracker.bot72 != null && RobotTracker.bot72.getTeam() != me && RobotTracker.bot72.getType().isRobotType() ? 1 : 0);
        int cell62 = (RobotTracker.bot73 != null && RobotTracker.bot73.getTeam() != me && RobotTracker.bot73.getType().isRobotType() ? 1 : 0);
        int cell63 = (RobotTracker.bot74 != null && RobotTracker.bot74.getTeam() != me && RobotTracker.bot74.getType().isRobotType() ? 1 : 0);
        int cell64 = (RobotTracker.bot75 != null && RobotTracker.bot75.getTeam() != me && RobotTracker.bot75.getType().isRobotType() ? 1 : 0);
        int cell65 = (RobotTracker.bot76 != null && RobotTracker.bot76.getTeam() != me && RobotTracker.bot76.getType().isRobotType() ? 1 : 0);
        int cell66 = (RobotTracker.bot77 != null && RobotTracker.bot77.getTeam() != me && RobotTracker.bot77.getType().isRobotType() ? 1 : 0);
        Pair<Direction, Direction> best = null;
        int bestScore = 0;
        int score = 0;
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
        if (dbg) Profiler.end("mop swing");

        if (target == null || (bestScore >= 3 && getPaintLevel() >= 0.5)) {
            if (best != null) {
                if (best.first != null && best.first != Direction.CENTER) bot.move(best.first);
                rc.mopSwing(best.second);
                indicate("mop swing!");
                rc.setIndicatorDot(Game.pos.add(best.second), 0, 255, 0);
                return true;
            }
        } else {
            if (moveDir != Direction.CENTER) bot.move(moveDir);
            rc.attack(target.getLocation());
            indicate("regular attak");
            rc.setIndicatorDot(target.getLocation(), 0, 0, 255);
            return true;
        }

        if (dbg) Profiler.begin();

        // just mop :(
        GameBinaryOperator<MapInfo> getBestCell = (c1, c2) -> {
            if (c1 == null) return c2;
            if (c2 == null) return c1;
            return c1;
        };

        GamePredicate<MapInfo> cellPred = c -> c.getPaint().isEnemy();

        MapInfo bestCell = null;
        moveDir = null;

        for (int i = dcnt - 1; i >= 0; i--) {
            Direction dir = dirs[i];
            if (dir == Direction.CENTER || rc.canMove(dir)) {
                MapInfo tmp = CellTracker.getBestCellSquare(getBestCell, cellPred, Game.pos.x + dir.dx, Game.pos.y + dir.dy);
                if (tmp != null || (bestCell == null || bestCell != getBestCell.apply(bestCell, tmp))) {
                    bestCell = tmp;
                    moveDir = dir;
                }
            }
        }

        if (dbg) Profiler.end("mop");

        if (bestCell != null) {
            if (moveDir != null && moveDir != Direction.CENTER) bot.move(moveDir);
            rc.attack(bestCell.getMapLocation());
            indicate("mop");
            rc.setIndicatorDot(bestCell.getMapLocation(), 0, 255, 255);
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
