package caterpillow.robot.agents.mopper;

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
import static caterpillow.util.Util.orthDirections;
import static caterpillow.util.Util.relatedDirections;

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
        
        MapLocation targetLoc = null;
    

        // try to steal paint from enemy
        MapInfo loc = CellTracker.getNearestCell(c -> {
            if (!c.getPaint().isEnemy()) return false;
            RobotInfo nearbot = rc.senseRobotAtLocation(c.getMapLocation());
            if (nearbot == null) return false;
            if (isEnemyAgent(nearbot)) return true;
            return false;
        });

        if (loc != null) {
            if (rc.canAttack(loc.getMapLocation())) {
                rc.attack(loc.getMapLocation());
                return loc.getMapLocation();
            }
            targetLoc = loc.getMapLocation();
        }

        // try mop sweep
        Direction bestDirection = null;
        int bestCount = 0;
        for (Direction dir : orthDirections) {

            // stupid code
            int cnt = 0;
            for (Direction related : relatedDirections(dir)) {

                // first layer
                MapLocation possibleAttackLoc = rc.getLocation().add(related);
                if (rc.canSenseLocation(possibleAttackLoc)) {
                    RobotInfo robotThere = rc.senseRobotAtLocation(possibleAttackLoc);
                    if (robotThere != null && isEnemyAgent(robotThere)) cnt++;
                }

                // second layer
                possibleAttackLoc = possibleAttackLoc.add(dir);
                if (rc.canSenseLocation(possibleAttackLoc)) {
                    RobotInfo robotThere = rc.senseRobotAtLocation(possibleAttackLoc);
                    if (robotThere != null && isEnemyAgent(robotThere)) cnt++;
                }
            }

            if (cnt > bestCount) {
                bestCount = cnt;
                bestDirection = dir;
            }
        }

        if (bestDirection != null && rc.canMopSwing(bestDirection)) {
            rc.mopSwing(bestDirection);
            return targetLoc;
        }

        // just paint

        loc = CellTracker.getNearestCell(c -> c.getPaint().isEnemy());
        if (loc != null) {
            if (rc.canAttack(loc.getMapLocation())) {
                rc.attack(loc.getMapLocation());
                return loc.getMapLocation();
            }
            targetLoc = loc.getMapLocation();
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
