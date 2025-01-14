package caterpillow_v1.robot.agents.mopper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import battlecode.common.RobotInfo;
import caterpillow_v1.Game;
import static caterpillow_v1.Game.rc;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.robot.agents.WeakRefillStrategy;
import caterpillow_v1.util.GameSupplier;
import static caterpillow_v1.util.Util.getNearestCell;
import static caterpillow_v1.util.Util.getNearestRobot;
import static caterpillow_v1.util.Util.guessEnemyLocs;
import static caterpillow_v1.util.Util.isCellInTowerBounds;
import static caterpillow_v1.util.Util.isEnemyAgent;
import static caterpillow_v1.util.Util.isFriendly;
import static caterpillow_v1.util.Util.isInAttackRange;
import static caterpillow_v1.util.Util.isPaintBelowHalf;
import static caterpillow_v1.util.Util.missingPaint;

public class MopperOffenceStrategy extends Strategy {

    public Mopper bot;

    // enemyLocs is more like "POI locs"
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;
    public List<GameSupplier<MapInfo>> suppliers;
    Random rng;
    public MapLocation lastSeenRuin;
    WeakRefillStrategy refillStrategy;

    public MopperOffenceStrategy() throws GameActionException {
        bot = (Mopper) Game.bot;
        this.enemyLocs = guessEnemyLocs(bot.home);
        this.enemy = enemyLocs.get(0);
        enemyLocs.addLast(bot.home);
        rng = new Random();
        

        suppliers = new ArrayList<>();
        // mop and attack (in range)
        suppliers.add(() -> getNearestCell(c -> isInAttackRange(c.getMapLocation()) && rc.senseRobotAtLocation(c.getMapLocation()) != null && isEnemyAgent(rc.senseRobotAtLocation(c.getMapLocation())) && !c.getPaint().isAlly()));
//         attack (anything visible)
        suppliers.add(() -> getNearestCell(c -> rc.senseRobotAtLocation(c.getMapLocation()) != null && isEnemyAgent(rc.senseRobotAtLocation(c.getMapLocation())) && !c.getPaint().isAlly()));
        // mop cell near ruin
        suppliers.add(() -> {
            ArrayList<MapLocation> ruins = new ArrayList<>();
            for (MapInfo c : rc.senseNearbyMapInfos()) {
                if (c.hasRuin()) {
                    ruins.add(c.getMapLocation());
                }
            }
            return getNearestCell(c -> {
                if (!c.getPaint().isEnemy()) {
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
    }

    public void safeMove(MapLocation loc) throws GameActionException {
        Direction move = bot.pathfinder.getMove(loc);
        MapLocation newLoc = rc.getLocation().add(move);
        boolean canSense = rc.canSenseLocation(newLoc);
        PaintType paint = canSense ? rc.senseMapInfo(newLoc).getPaint() : null;

        if (canSense && !paint.isEnemy()) {
            bot.pathfinder.makeMove(loc);
            return;
        }   

        if (canSense && paint.isEnemy() && rc.canAttack(newLoc)) {
            rc.attack(newLoc);
            bot.pathfinder.makeMove(loc);
            return;
        }

        // check if there's legit anywhere i can go thats better than rn
        if (!rc.senseMapInfo(rc.getLocation()).getPaint().isAlly()) {
            MapInfo[] surrounds = rc.senseNearbyMapInfos(2);
            for (MapInfo c : surrounds) {
                if (!c.getPaint().isEnemy()) {
                    bot.pathfinder.makeMove(c.getMapLocation());
                    return;
                }
            }
            // beeline towards nearest ally paint
            MapLocation ally = getNearestCell(c -> c.getPaint().isAlly()).getMapLocation();
            if (ally != null) {
                bot.pathfinder.makeMove(ally);
                return;
            }

            MapLocation neutral = getNearestCell(c -> c.getPaint()==PaintType.EMPTY).getMapLocation();
            if (neutral != null) {
                bot.pathfinder.makeMove(neutral);
            }
            
        }


    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {

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
            enemy = enemyLocs.get(0);
        }

        // try refill ourselves
        if (refillStrategy != null) {
            if (refillStrategy.isComplete()) {
                refillStrategy = null;
                runTick();
            } else {
                refillStrategy.runTick();
                //System.out.println("running refill strat");
            }
            return;
        }

        if (isPaintBelowHalf()) {
            RobotInfo nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getPaintAmount() >= missingPaint());
            if (nearest != null) {
                refillStrategy = new WeakRefillStrategy(nearest.getLocation(), 0.3);
                runTick();
            }
        }

        
        // move
        for (GameSupplier<MapInfo> pred : suppliers) {
            MapInfo res = pred.get();
            if (res != null) {
                // go towards, and attack if possible
                safeMove(res.getMapLocation());
                return;
            }
        }

        // run towards goal
        if (rc.isMovementReady()) {
            safeMove(enemy);
        }
    }
}
