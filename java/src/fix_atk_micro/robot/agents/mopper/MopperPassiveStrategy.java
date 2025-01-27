package fix_atk_micro.robot.agents.mopper;

import java.util.ArrayList;
import java.util.List;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import fix_atk_micro.Config;
import fix_atk_micro.Game;
import static fix_atk_micro.Game.rc;
import fix_atk_micro.pathfinding.BugnavPathfinder;
import fix_atk_micro.robot.Strategy;
import fix_atk_micro.robot.agents.WeakRefillStrategy;
import fix_atk_micro.robot.agents.roaming.PassiveRoamStrategy;
import static fix_atk_micro.tracking.CellTracker.getNearestCell;
import static fix_atk_micro.tracking.RobotTracker.getNearestRobot;
import fix_atk_micro.util.GameSupplier;
import static fix_atk_micro.util.Util.getPaintLevel;
import static fix_atk_micro.util.Util.indicate;
import static fix_atk_micro.util.Util.isAllyAgent;
import static fix_atk_micro.util.Util.isCellInTowerBounds;
import static fix_atk_micro.util.Util.isEnemyAgent;
import static fix_atk_micro.util.Util.isInAttackRange;

public class MopperPassiveStrategy extends Strategy {

    public Mopper bot;

    public List<GameSupplier<MapInfo>> suppliers;
    Strategy refillStrategy;
    Strategy rescueStrategy;
    Strategy teamRefillStrategy;
    Strategy roamStrategy;

    public MopperPassiveStrategy() throws GameActionException {
        bot = (Mopper) Game.bot;
        bot.pathfinder = new BugnavPathfinder(c -> !c.getPaint().isAlly());
        roamStrategy = new PassiveRoamStrategy();

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
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("PASSIVE MOPPER");
        // just checking and updating enemy locs:

        if (rescueStrategy == null) {
            RobotInfo nearest = getNearestRobot(b -> isAllyAgent(b) && Config.shouldRescue(b));
            if (nearest != null) {
                rescueStrategy = new RescueStrategy(nearest.getLocation());
            }
        }
        if (tryStrategy(rescueStrategy)) return;
        rescueStrategy = null;

        for (GameSupplier<MapInfo> pred : suppliers) {
            MapInfo res = pred.get();
            if (res != null) {
                // go towards, and attack if possible
                bot.pathfinder.makeMove(res.getMapLocation());
                bot.doBestAttack();
                return;
            }
        }

        if (refillStrategy == null && getPaintLevel() < 0.8) {
            refillStrategy = new WeakRefillStrategy(0.2);
        }
        if (tryStrategy(refillStrategy)) return;
        refillStrategy = null;

        if (teamRefillStrategy == null) {
            RobotInfo nearest = getNearestRobot(b -> isAllyAgent(b) && Config.shouldRefill(b));
            if (nearest != null) {
                teamRefillStrategy = new RefillStrategy(nearest);
            }
        }
        if (tryStrategy(teamRefillStrategy)) return;
        teamRefillStrategy = null;

        MapInfo nearest = getNearestCell(c -> c.getPaint().isEnemy());
        if (nearest != null) {
            if (rc.canAttack(nearest.getMapLocation())) {
                rc.attack(nearest.getMapLocation());
            } else {
                bot.pathfinder.makeMove(nearest.getMapLocation());
                if (rc.canAttack(nearest.getMapLocation())) {
                    rc.attack(nearest.getMapLocation());
                }
                return;
            }
        }

        roamStrategy.runTick();
    }
}
