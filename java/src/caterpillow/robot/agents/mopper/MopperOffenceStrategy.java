package caterpillow.robot.agents.mopper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.util.Util.*;

import caterpillow.robot.Strategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.robot.agents.roaming.StrongAggroRoamStrategy;
import caterpillow.util.GameSupplier;

public class MopperOffenceStrategy extends Strategy {

    public Mopper bot;

    // enemyLocs is more like "POI locs"
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;
    public List<GameSupplier<MapInfo>> suppliers;
    Random rng;
    public MapLocation lastSeenRuin;
    WeakRefillStrategy refillStrategy;
    Strategy roamStrategy;

    public MopperOffenceStrategy() throws GameActionException {
        bot = (Mopper) Game.bot;
        this.enemyLocs = guessEnemyLocs(bot.home);
        this.enemy = enemyLocs.get(0);
        enemyLocs.addLast(bot.home);
        rng = new Random();

        suppliers = new ArrayList<>();
        // mop and attack (in range)
        suppliers.add(() -> getNearestCell(c -> {
            if (!isInAttackRange(c.getMapLocation())) {
                return false;
            }
            if (c.getPaint().isAlly()) {
                return false;
            }
            RobotInfo bot = rc.senseRobotAtLocation(c.getMapLocation());
            if (bot == null) {
                return false;
            }
            if (isEnemyAgent(bot) && bot.getPaintAmount() > 0 && countNearbyMoppers(bot.getLocation()) <= 3) {
                return true;
            }
            return false;
        }));
//         attack (anything visible)
        suppliers.add(() -> {
            RobotInfo info = getNearestRobot(b -> isEnemyAgent(b) && b.getPaintAmount() > 0 && countNearbyMoppers(b.getLocation()) <= 3);
            if (info == null) {
                return null;
            } else {
                return rc.senseMapInfo(info.getLocation());
            }
        });
        // mop cell near ruin
        suppliers.add(() -> {
            ArrayList<MapLocation> ruins = new ArrayList<>();
            for (MapInfo c : rc.senseNearbyMapInfos()) {
                if (c.hasRuin()) {
                    ruins.add(c.getMapLocation());
                }
            }
            return getNearestCell(c -> {
                if (!c.getPaint().isEnemy() || isInAttackRange(c.getMapLocation())) {
                    return false;
                }
                for (MapLocation ruin : ruins) {
                    if (isCellInTowerBounds(ruin, c.getMapLocation())) {
                        return true;
                    }
                }
                return false;
            });
        });
        // chase enemy cell
        suppliers.add(() -> getNearestCell(c -> c.getPaint().isEnemy()));
        roamStrategy = new StrongAggroRoamStrategy();
    }

//    public void safeMove(MapLocation loc) throws GameActionException {
//        Direction move = bot.pathfinder.getMove(loc);
//        if (move == null) {
//            return;
//        }
//        MapLocation newLoc = rc.getLocation().add(move);
//        PaintType paint = rc.senseMapInfo(newLoc).getPaint();
//
//        if (!paint.isEnemy()) {
//            bot.pathfinder.makeMove(loc);
//            return;
//        }
//
//        if (rc.canAttack(newLoc)) {
//            rc.attack(newLoc);
//            bot.pathfinder.makeMove(loc);
//            return;
//        }
//
//        // check if there's legit anywhere i can go thats better than rn
//        if (!rc.senseMapInfo(rc.getLocation()).getPaint().isAlly()) {
//            MapInfo[] surrounds = rc.senseNearbyMapInfos(2);
//            for (MapInfo c : surrounds) {
//                if (!c.getPaint().isEnemy()) {
//                    bot.pathfinder.makeMove(c.getMapLocation());
//                    return;
//                }
//            }
//            // beeline towards nearest ally paint
//            MapInfo ally = getNearestCell(c -> c.getPaint().isAlly());
//            if (ally != null) {
//                bot.pathfinder.makeMove(ally.getMapLocation());
//                return;
//            }
//
//            MapInfo neutral = getNearestCell(c -> c.getPaint()==PaintType.EMPTY);
//            if (neutral != null) {
//                bot.pathfinder.makeMove(neutral.getMapLocation());
//            }
//        }
//    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("OFFENCE MOPPER");

        // just checking and updating enemy locs:
        MapInfo ruin = getNearestCell(c -> c.hasRuin());
        if (ruin != null) {
            lastSeenRuin = ruin.getMapLocation();
        }

        if (rc.canSenseLocation(enemy)) {
            // if we can see the enemy, just go to the next enemy loc. it's kinda cyclic for now
            enemyLocs.removeFirst();
            if (enemyLocs.isEmpty()) {
                enemyLocs = guessEnemyLocs(lastSeenRuin);
            }
            enemy = enemyLocs.getFirst();
        }

        if (isPaintBelowHalf()) {
            RobotInfo nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getPaintAmount() >= missingPaint());
            if (nearest != null) {
                bot.secondaryStrategy = new WeakRefillStrategy(nearest.getLocation(), 0.3);
                bot.runTick();
                return;
            }
        }

        // move
        for (GameSupplier<MapInfo> pred : suppliers) {
            MapInfo res = pred.get();
            if (res != null) {
                // go towards, and attack if possible
                bot.pathfinder.makeMove(res.getMapLocation());
//                safeMove(res.getMapLocation());
                return;
            }
        }

        // run towards goal
//        if (rc.isMovementReady()) {
//            safeMove(enemy);
//        }
        roamStrategy.runTick();
    }
}
