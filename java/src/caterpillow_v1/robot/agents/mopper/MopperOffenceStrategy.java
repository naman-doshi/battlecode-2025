package caterpillow_v1.robot.agents.mopper;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow_v1.Game;
import static caterpillow_v1.Game.rc;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.util.GamePredicate;
import caterpillow_v1.util.GameSupplier;

import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;

public class MopperOffenceStrategy extends Strategy {

    public Mopper bot;

    // enemyLocs is more like "POI locs"
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;
    public List<GameSupplier<MapInfo>> suppliers;

    public MopperOffenceStrategy() throws GameActionException {
        bot = (Mopper) Game.bot;
        this.enemyLocs = guessEnemyLocs(bot.home);
        this.enemy = enemyLocs.get(0);
        enemyLocs.addLast(bot.home);

        suppliers = new ArrayList<>();
        // mop and attack (in range)
        suppliers.add(() -> getNearestCell(c -> isInAttackRange(c.getMapLocation()) && rc.senseRobotAtLocation(c.getMapLocation()) != null && isEnemyAgent(rc.senseRobotAtLocation(c.getMapLocation())) && c.getPaint().isEnemy()));
//         attack (anything visible)
        suppliers.add(() -> getNearestCell(c -> rc.senseRobotAtLocation(c.getMapLocation()) != null && isEnemyAgent(rc.senseRobotAtLocation(c.getMapLocation()))));
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
