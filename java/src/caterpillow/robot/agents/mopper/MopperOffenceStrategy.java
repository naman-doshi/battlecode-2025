package caterpillow.robot.agents.mopper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.*;
import caterpillow.Config;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.robot.agents.roaming.StrongAggroRoamStrategy;
import static caterpillow.tracking.CellTracker.getNearestCell;
import caterpillow.tracking.RobotTracker;
import caterpillow.util.GameSupplier;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.isAllyAgent;
import static caterpillow.util.Util.isEnemyAgent;
import static caterpillow.util.Util.isInAttackRange;
import static caterpillow.util.Util.isPaintBelowHalf;

public class MopperOffenceStrategy extends Strategy {

    public Mopper bot;

    public List<GameSupplier<MapInfo>> suppliers;
    Random rng;
    public MapLocation lastSeenRuin;
    Strategy rescueStrategy;
    Strategy refillStrategy;
    Strategy roamStrategy;


    public MopperOffenceStrategy() throws GameActionException {
        bot = (Mopper) Game.bot;
        rng = new Random();

        suppliers = new ArrayList<>();
        // mop and attack (in range)
        suppliers.add(() -> getNearestCell(c -> {
            if (!isInAttackRange(c.getMapLocation())) {
                return false;
            }
            if (c.getPaint().isAlly() || c.getPaint().equals(PaintType.EMPTY)) {
                return false;
            }
            RobotInfo bot = rc.senseRobotAtLocation(c.getMapLocation());
            if (bot == null) {
                return false;
            }
            if (isEnemyAgent(bot) && bot.getPaintAmount() > 0) {
                return true;
            }
            return false;
        }));
//         attack (anything visible)
        suppliers.add(() -> {
            RobotInfo info = RobotTracker.getNearestRobot(b -> isEnemyAgent(b) && b.getPaintAmount() > 0 && RobotTracker.countNearbyFriendly(c -> c.type == UnitType.MOPPER) <= 3 && (rc.senseMapInfo(b.getLocation()).getPaint().isEnemy() || isInAttackRange(b.getLocation())));
            if (info == null) {
                return null;
            } else {
                return rc.senseMapInfo(info.getLocation());
            }
        });
        // mop cell near ruin
        // TODO: FIX BYTECODE FOR THIS
        //suppliers.add(() -> getNearestCell(c -> ticksExisted > 0 && c.getPaint().isEnemy() && CellTracker.isNearRuin[c.getMapLocation().x][c.getMapLocation().y]));
        // chase enemy cell
        suppliers.add(() -> getNearestCell(c -> c.getPaint().isEnemy()));
        roamStrategy = new StrongAggroRoamStrategy();
    }


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

        if (rescueStrategy == null) {
            RobotInfo nearest = RobotTracker.getNearestRobot(b -> isAllyAgent(b) && Config.shouldRescue(b));
            if (nearest != null) {
                rescueStrategy = new RescueStrategy(nearest.getLocation());
            }
        }

        if (tryStrategy(rescueStrategy)) return;
        rescueStrategy = null;

        if (refillStrategy == null && isPaintBelowHalf()) {
            refillStrategy = new WeakRefillStrategy(0.2);
        }

        if (tryStrategy(refillStrategy)) return;
        refillStrategy = null;

        // move
        for (int i = 0; i < suppliers.size(); i++) {
            GameSupplier<MapInfo> pred = suppliers.get(i);
            MapInfo res = pred.get();
            if (res != null) {
                rc.setIndicatorDot(res.getMapLocation(), 255, 0, 0);
                bot.doBestAttack(res.getMapLocation());
                bot.pathfinder.makeMove(res.getMapLocation());
                return;
            }
        }

        // run towards goal
        roamStrategy.runTick();
        bot.doBestAttack(null);
    }
}
