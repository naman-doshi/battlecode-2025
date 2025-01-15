package caterpillow_v1.robot.agents.mopper;

import java.util.ArrayList;
import java.util.List;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow_v1.Config;
import caterpillow_v1.Game;
import static caterpillow_v1.Game.rc;
import static caterpillow_v1.util.Util.*;

import caterpillow_v1.pathfinding.BugnavPathfinder;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.robot.agents.WeakRefillStrategy;
import caterpillow_v1.robot.agents.roaming.PassiveRoamStrategy;
import caterpillow_v1.util.GameSupplier;

public class MopperPassiveStrategy extends Strategy {

    public Mopper bot;

    public List<GameSupplier<MapInfo>> suppliers;
    WeakRefillStrategy refillStrategy;
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
        // chase enemy cell
        suppliers.add(() -> getNearestCell(c -> c.getPaint().isEnemy()));
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("PASSIVE MOPPER");
        // just checking and updating enemy locs:

        if (isPaintBelowHalf()) {
            RobotInfo nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getPaintAmount() >= missingPaint());
            if (nearest != null) {
                bot.secondaryStrategy = new WeakRefillStrategy(nearest.getLocation(), 0.3);
                bot.runTick();
                return;
            }
        }

        RobotInfo nearest = getNearestRobot(b -> isAllyAgent(b) && Config.shouldRescue(b));
        if (nearest != null) {
            bot.secondaryStrategy = new RescueStrategy(nearest.getLocation());
            bot.runTick();
            return;
        }

        nearest = getNearestRobot(b -> isAllyAgent(b) && Config.shouldRefill(b));
        if (nearest != null) {
            bot.secondaryStrategy = new RefillStrategy(nearest);
            bot.runTick();
            return;
        }

        for (GameSupplier<MapInfo> pred : suppliers) {
            MapInfo res = pred.get();
            if (res != null) {
                // go towards, and attack if possible
                bot.pathfinder.makeMove(res.getMapLocation());
                if (rc.canAttack(res.getMapLocation())) {
                    rc.attack(res.getMapLocation());
                }
                return;
            }
        }

        roamStrategy.runTick();
    }
}
