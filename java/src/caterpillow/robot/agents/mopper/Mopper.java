package caterpillow.robot.agents.mopper;

import java.util.List;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.agents.Agent;
import static caterpillow.tracking.RobotTracker.getBestRobot;
import caterpillow.util.GamePredicate;
import static caterpillow.util.Util.isEnemyAgent;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.isInDanger;
import static caterpillow.util.Util.orthDirections;
import static caterpillow.util.Util.relatedDirections;

public class Mopper extends Agent {

    Mopper bot;
    List<MapLocation> enemyLocs;
    MapLocation spawnLoc;

    public RobotInfo getBestTarget(GamePredicate<RobotInfo> pred) throws GameActionException {
        return getBestRobot((a, b) -> {
            int a1 = a.getType().ordinal();
            int b1 = b.getType().ordinal();
            int h1 = a.getPaintAmount();
            int h2 = b.getPaintAmount();
            if (a1 == b1) {
                if (h1 > h2) return b;
                else return a;
            } else {
                if (a1 < b1) return a;
                else return b;
            }
        }, e -> !isFriendly(e) && e.getType().isRobotType() && pred.test(e));
    }

    public RobotInfo getBestTarget() throws GameActionException {
        return getBestTarget(e -> true);
    }

    public void doBestAttack(MapLocation target) throws GameActionException {
        if (!rc.isActionReady()) {
            return;
        }

        // try mop sweep
        Direction bestDirection = null;
        int bestCount = 1;
        for (Direction dir : orthDirections) {
            if (!rc.canMopSwing(dir)) {
                continue;
            }
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

        if (bestDirection != null) {
            rc.mopSwing(bestDirection);
            return;
        }

        if (target != null && rc.canAttack(target)) {
            rc.attack(target);
        } else {
            RobotInfo target1 = getBestTarget(e -> rc.canAttack(e.getLocation()) && rc.senseMapInfo(e.getLocation()).getPaint().isEnemy());
            if (target1 != null && rc.canAttack(target1.getLocation())) rc.attack(target1.getLocation());
        }
    }

    @Override
    public void init() throws GameActionException {
        super.init();
        pathfinder = new BugnavPathfinder(c -> c.getPaint().isEnemy() && !isInDanger(c.getMapLocation()));
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
