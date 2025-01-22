package caterpillow.robot.agents.mopper;

import java.util.ArrayList;
import java.util.List;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.agents.Agent;
import caterpillow.tracking.CellTracker;
import static caterpillow.util.Util.isEnemyAgent;
import static caterpillow.util.Util.isInDanger;

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
    

    public MapLocation doBestAttack() throws GameActionException {
        // im gonna kms
        MapLocation currentLoc = rc.getLocation();
        List<Direction> possiblemoves = possibleMovements();
        int currentX = currentLoc.x;
        int currentY = currentLoc.y;

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

            // move then attack if possible
            for (Direction dir : possiblemoves) {
                MapLocation nextLoc = currentLoc.add(dir);
                // chucking constants to save bytecode rip
                if (nextLoc.isWithinDistanceSquared(locLocation, 2) && rc.canMove(dir) && rc.isActionReady()) {
                    bot.move(dir);
                    rc.attack(locLocation);
                    return locLocation;
                }
            }

            // otherwise update our target loc
            targetLoc = locLocation;
        }


        // preprocess robots
        RobotInfo[] robots = rc.senseNearbyRobots();
        int[][] lookup = new int[9][9];
        int x = currentX - 4;
        int y = currentY - 4;
        for (RobotInfo robot : robots) {
            MapLocation robotLoc = robot.getLocation();
            if (isEnemyAgent(robot) && !CellTracker.mapInfos[robotLoc.x][robotLoc.y].getPaint().isAlly()) {
                int dx = robotLoc.x - x;
                int dy = robotLoc.y - y;
                lookup[dx][dy]++;
            }
        }

        // try mop sweep
        Direction bestDirection = null;
        Direction dirToMove = null;
        int bestCount = 0;
    

        // North
        int northCount = 0;
        // First layer
        northCount += lookup[4][5]; // North
        northCount += lookup[3][5]; // Northwest
        northCount += lookup[5][5]; // Northeast
        // Second layer
        northCount += lookup[4][6]; // North + North
        northCount += lookup[3][6]; // Northwest + North
        northCount += lookup[5][6]; // Northeast + North
        if (northCount > bestCount) {
            bestCount = northCount;
            bestDirection = Direction.NORTH;
        }

        // South
        int southCount = 0;
        // First layer
        southCount += lookup[4][3]; // South
        southCount += lookup[3][3]; // Southwest
        southCount += lookup[5][3]; // Southeast
        // Second layer
        southCount += lookup[4][2]; // South + South
        southCount += lookup[3][2]; // Southwest + South
        southCount += lookup[5][2]; // Southeast + South
        if (southCount > bestCount) {
            bestCount = southCount;
            bestDirection = Direction.SOUTH;
        }

        // East
        int eastCount = 0;
        // First layer
        eastCount += lookup[5][4]; // East
        eastCount += lookup[5][5]; // Northeast
        eastCount += lookup[5][3]; // Southeast
        // Second layer
        eastCount += lookup[6][4]; // East + East
        eastCount += lookup[6][5]; // Northeast + East
        eastCount += lookup[6][3]; // Southeast + East
        if (eastCount > bestCount) {
            bestCount = eastCount;
            bestDirection = Direction.EAST;
        }

        // West
        int westCount = 0;
        // First layer
        westCount += lookup[3][4]; // West
        westCount += lookup[3][5]; // Northwest
        westCount += lookup[3][3]; // Southwest
        // Second layer
        westCount += lookup[2][4]; // West + West
        westCount += lookup[2][5]; // Northwest + West
        westCount += lookup[2][3]; // Southwest + West
        if (westCount > bestCount) {
            bestCount = westCount;
            bestDirection = Direction.WEST;
        }

        // do the same but for every loc we can move to
        // for (Direction dir2 : possiblemoves) {
            
        //     MapLocation futureLoc = currentLoc.add(dir2);
        //     int futurex = futureLoc.x;
        //     int futurey = futureLoc.y;
        //     int dx = futurex - currentX;
        //     int dy = futurey - currentY;

        //     northCount = 0;
        //     // First layer
        //     northCount += lookup[4 + dx][5 + dy]; // North
        //     northCount += lookup[3 + dx][5 + dy]; // Northwest
        //     northCount += lookup[5 + dx][5 + dy]; // Northeast
        //     // Second layer
        //     northCount += lookup[4 + dx][6 + dy]; // North + North
        //     northCount += lookup[3 + dx][6 + dy]; // Northwest + North
        //     northCount += lookup[5 + dx][6 + dy]; // Northeast + North
        //     if (northCount > bestCount) {
        //         bestCount = northCount;
        //         bestDirection = Direction.NORTH;
        //         dirToMove = dir2; // Verbatim assignment
        //     }

        //     // South
        //     southCount = 0;
        //     // First layer
        //     southCount += lookup[4 + dx][3 + dy]; // South
        //     southCount += lookup[3 + dx][3 + dy]; // Southwest
        //     southCount += lookup[5 + dx][3 + dy]; // Southeast
        //     // Second layer
        //     southCount += lookup[4 + dx][2 + dy]; // South + South
        //     southCount += lookup[3 + dx][2 + dy]; // Southwest + South
        //     southCount += lookup[5 + dx][2 + dy]; // Southeast + South
        //     if (southCount > bestCount) {
        //         bestCount = southCount;
        //         bestDirection = Direction.SOUTH;
        //         dirToMove = dir2; // Verbatim assignment
        //     }

        //     // East
        //     eastCount = 0;
        //     // First layer
        //     eastCount += lookup[5 + dx][4 + dy]; // East
        //     eastCount += lookup[5 + dx][5 + dy]; // Northeast
        //     eastCount += lookup[5 + dx][3 + dy]; // Southeast
        //     // Second layer
        //     eastCount += lookup[6 + dx][4 + dy]; // East + East
        //     eastCount += lookup[6 + dx][5 + dy]; // Northeast + East
        //     eastCount += lookup[6 + dx][3 + dy]; // Southeast + East
        //     if (eastCount > bestCount) {
        //         bestCount = eastCount;
        //         bestDirection = Direction.EAST;
        //         dirToMove = dir2; // Verbatim assignment
        //     }

        //     // West
        //     westCount = 0;
        //     // First layer
        //     westCount += lookup[3 + dx][4 + dy]; // West
        //     westCount += lookup[3 + dx][5 + dy]; // Northwest
        //     westCount += lookup[3 + dx][3 + dy]; // Southwest
        //     // Second layer
        //     westCount += lookup[2 + dx][4 + dy]; // West + West
        //     westCount += lookup[2 + dx][5 + dy]; // Northwest + West
        //     westCount += lookup[2 + dx][3 + dy]; // Southwest + West
        //     if (westCount > bestCount) {
        //         bestCount = westCount;
        //         bestDirection = Direction.WEST;
        //         dirToMove = dir2; // Verbatim assignment
        //     }

        // }

        if (bestDirection != null && rc.canMopSwing(bestDirection)) {
            if (dirToMove == null) {
                rc.mopSwing(bestDirection);
                return targetLoc;
            } else if (rc.canMove(dirToMove)) {
                bot.move(dirToMove);
                rc.mopSwing(bestDirection);
                return targetLoc;
            }
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
            for (Direction dir : possiblemoves) {
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
