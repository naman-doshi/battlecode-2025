package caterpillow.robot.agents.mopper;

import java.util.ArrayList;
import java.util.List;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.util.GameSupplier;
import static caterpillow.util.Util.getNearestCell;
import static caterpillow.util.Util.getNearestRobot;
import static caterpillow.util.Util.guessEnemyLocs;
import static caterpillow.util.Util.isCellInTowerBounds;
import static caterpillow.util.Util.isEnemyAgent;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.isInAttackRange;
import static caterpillow.util.Util.isPaintBelowHalf;
import static caterpillow.util.Util.missingPaint;

public class MopperPassiveStrategy extends Strategy {

    public Mopper bot;

    // enemyLocs is more like "POI locs"
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;
    public List<GameSupplier<MapInfo>> suppliers;
    WeakRefillStrategy refillStrategy;

    public MopperPassiveStrategy() throws GameActionException {
        bot = (Mopper) Game.bot;
        this.enemyLocs = guessEnemyLocs(bot.home);
        this.enemy = enemyLocs.get(0);
        enemyLocs.addLast(bot.home);

        suppliers = new ArrayList<>();
        // mop and attack (in range)
        suppliers.add(() -> getNearestCell(c -> isInAttackRange(c.getMapLocation()) && rc.senseRobotAtLocation(c.getMapLocation()) != null && isEnemyAgent(rc.senseRobotAtLocation(c.getMapLocation())) && c.getPaint().isEnemy()));
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
        if (rc.getLocation().isAdjacentTo(loc) && rc.senseMapInfo(loc).getPaint().isEnemy()) {
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
            // if we can see the enemy, just go to the next enemy loc. it's kinda cyclic for now
            enemyLocs.addLast(enemy);
            enemyLocs.removeFirst();
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

        // fill bots we can see, if possible
        RobotInfo nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isRobotType() && isPaintBelowHalf(b) && rc.getLocation().distanceSquaredTo(b.getLocation()) <= 2 && b.getType() != UnitType.MOPPER);
        if (nearest != null && rc.canTransferPaint(nearest.getLocation(), 10)) {
            rc.transferPaint(nearest.getLocation(), 10);
        }

        for (GameSupplier<MapInfo> pred : suppliers) {
            MapInfo res = pred.get();
            if (res != null) {
                // go towards, and attack if possible
                safeMove(res.getMapLocation());
                if (rc.canAttack(res.getMapLocation())) {
                    rc.attack(res.getMapLocation());
                }
                return;
            }
        }

        // run towards goal
        if (rc.isMovementReady()) {
            safeMove(enemy);
        }
    }
}
