package fix_atk_micro.robot.agents.mopper;

import java.util.List;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.Team;
import battlecode.common.UnitType;
import fix_atk_micro.Game;
import static fix_atk_micro.Game.rc;
import fix_atk_micro.packet.packets.StrategyPacket;
import fix_atk_micro.pathfinding.BugnavPathfinder;
import fix_atk_micro.robot.EmptyStrategy;
import fix_atk_micro.robot.agents.Agent;
import fix_atk_micro.tracking.CellTracker;
import fix_atk_micro.tracking.RobotTracker;
import fix_atk_micro.util.Pair;
import static fix_atk_micro.util.Util.indicate;
import static fix_atk_micro.util.Util.isEnemyAgent;
import static fix_atk_micro.util.Util.isInDanger;

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



    public MapLocation doBestAttack() throws GameActionException {
        // im gonna kms
        MapLocation currentLoc = rc.getLocation();
        int currentX = currentLoc.x;
        int currentY = currentLoc.y;

        bot.lastMove = true;

        Direction[] dirs = new Direction[9];
        int dcnt = 0;
        for (Direction dir : Direction.values()) {
            if (dir == Direction.CENTER || rc.canMove(dir)) {
                dirs[dcnt++] = dir;
            }
        }

        // targetloc is a good place to move to
        MapLocation targetLoc = null;

        // first try steal paint from enemy
        // greedy bc i cbb and i doubt it makes a real difference

        // try to steal paint from enemy
        MapInfo loc = CellTracker.getNearestCell(c -> {
                if (c.getPaint().isAlly()) return false;
                RobotInfo nearbot = rc.senseRobotAtLocation(c.getMapLocation());
                if (nearbot == null) return false;
                if (isEnemyAgent(nearbot)) return true;
                return false;
            });



        if (loc != null) {

            MapLocation locLocation = loc.getMapLocation();

            // if u can attack rn obv do that
            if (rc.canAttack(locLocation)) {
                rc.attack(locLocation);
                return locLocation;
            }

            for (int i = dcnt - 1; i >= 0; i--) {
                Direction dir = dirs[i];
                if (dir != Direction.CENTER && rc.canMove(dir)) {
                    MapLocation nextLoc = currentLoc.add(dir);
                    // chucking constants to save bytecode rip
                    if (nextLoc.isWithinDistanceSquared(locLocation, 2) && rc.canMove(dir) && rc.isActionReady()) {
                        bot.move(dir);
                        rc.attack(locLocation);
                        return locLocation;
                    }
                }
            }

            // otherwise update our target loc
            targetLoc = locLocation;
        }



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


        if (best != null && rc.canMopSwing(best.second)) {
            if (best.first != null && best.first != Direction.CENTER) bot.move(best.first);
            rc.mopSwing(best.second);
            indicate("mop swing!");
            rc.setIndicatorDot(Game.pos.add(best.second), 0, 255, 0);
            return targetLoc;
        }

        // just paint

        loc = CellTracker.getNearestCell(c -> c.getPaint().isEnemy());
        if (loc != null) {

            // not moving
            MapLocation locLocation = loc.getMapLocation();
            if (rc.canAttack(locLocation)) {
                rc.attack(locLocation);
                return targetLoc;
            }

            // moving?
            for (int i = dcnt - 1; i >= 0; i--) {
                Direction dir = dirs[i];
                MapLocation nextLoc = currentLoc.add(dir);
                if (nextLoc.isWithinDistanceSquared(locLocation, 2) && rc.canMove(dir) && rc.isActionReady()) {
                    bot.move(dir);
                    rc.attack(locLocation);
                    return targetLoc;
                }
            }

            if (targetLoc == null) targetLoc = locLocation;
        }

        return targetLoc;
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
        // if spawn is surrounded by enemy paint (i.e. no messaging) spawn some moppers to clean it up
        // TODO: make this a proper fix
        if (primaryStrategy instanceof EmptyStrategy) {
            if (home != null) {
                Game.origin = home;
            } else {
                Game.origin = rc.getLocation();
            }
            primaryStrategy = new MopperOffenceStrategy();
        }

        super.runTick();

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
