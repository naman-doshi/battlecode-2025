package caterpillow_v1.robot.agents.splasher;

import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow_v1.Game;
import static caterpillow_v1.Game.rc;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.robot.agents.WeakRefillStrategy;
import caterpillow_v1.util.GameSupplier;
import static caterpillow_v1.util.Util.getNearestRobot;
import static caterpillow_v1.util.Util.guessEnemyLocs;
import static caterpillow_v1.util.Util.isFriendly;
import static caterpillow_v1.util.Util.isPaintBelowHalf;
import static caterpillow_v1.util.Util.missingPaint;
import static caterpillow_v1.util.Util.project;
import static caterpillow_v1.util.Util.subtract;

public class SplasherAggroStrategy extends Strategy {

    public Splasher bot;

    // enemyLocs is more like "POI locs"
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;
    public List<GameSupplier<MapInfo>> suppliers;
    WeakRefillStrategy refillStrategy;

    public SplasherAggroStrategy() throws GameActionException {
        bot = (Splasher) Game.bot;
        this.enemyLocs = guessEnemyLocs(bot.home);
        this.enemy = enemyLocs.get(0);
        enemyLocs.addLast(bot.home);

        
    }

    public void safeMove(MapLocation loc) throws GameActionException {
        if (rc.canSenseLocation(loc) && rc.senseMapInfo(loc).getPaint().isEnemy()) {
            return;
        }
        // wait until andy's buffed pathfinder
        bot.pathfinder.makeMove(loc);
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        

        // just checking and updating enemy locs:

        if (rc.canSenseLocation(enemy)) {
            enemyLocs.removeFirst();

            while (enemyLocs.size() < 1) {
                Random rng = new Random();
                int x = rng.nextInt(0, rc.getMapWidth() - 1);
                int y = rng.nextInt(0, rc.getMapHeight() - 1);
                if (new MapLocation(x, y).distanceSquaredTo(rc.getLocation()) >= 9) {
                    MapLocation moveDir = subtract(new MapLocation(x, y), rc.getLocation());
                    enemyLocs.addLast(project(rc.getLocation(), moveDir, (double) (rc.getMapWidth() + rc.getMapHeight()) / 2));
                }
            }

            enemy = enemyLocs.get(0);
        }

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
                refillStrategy = new WeakRefillStrategy(nearest.getLocation(), 0.2);
                runTick();
            }
        }

        MapLocation target = bot.bestAttackLocation();
        if (target != null) {
            if (rc.canAttack(target)) {
                rc.attack(target);
            }
            safeMove(target);
        } else {
            safeMove(enemy);

        }

        
    }
}
