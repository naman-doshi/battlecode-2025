package fix_atk_micro.robot.agents.mopper;

import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import fix_atk_micro.Config;
import fix_atk_micro.Game;
import static fix_atk_micro.Game.*;
import fix_atk_micro.robot.Strategy;
import fix_atk_micro.robot.agents.WeakRefillStrategy;
import fix_atk_micro.robot.agents.roaming.AggroRoamStrategy;
import fix_atk_micro.tracking.RobotTracker;
import fix_atk_micro.util.GameSupplier;
import static fix_atk_micro.util.Util.indicate;
import static fix_atk_micro.util.Util.isAllyAgent;
import static fix_atk_micro.util.Util.isPaintBelowHalf;

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

        //suppliers = new ArrayList<>();
        // // mop and attack (in range)
        // suppliers.add(() -> getNearestCell(c -> {
        //     if (!isInAttackRange(c.getMapLocation()) || !c.getPaint().isEnemy()) {
        //         return false;
        //     }

        //     RobotInfo bot = rc.senseRobotAtLocation(c.getMapLocation());
        //     if (bot == null) {
        //         return false;
        //     }

        //     if (isEnemyAgent(bot) && bot.getPaintAmount() >= 10) {
        //         return true;
        //     }
        //     return false;
        // }));

        // // attack (anything visible)
        // suppliers.add(() -> {
        //     RobotInfo info = RobotTracker.getNearestRobot(b -> isEnemyAgent(b) && b.getPaintAmount() > 5);
        //     if (info == null) {
        //         return null;
        //     } else {
        //         return rc.senseMapInfo(info.getLocation());
        //     }
        // });
        // // mop cell near ruin
        // // TODO: FIX BYTECODE FOR THIS
        // //suppliers.add(() -> getNearestCell(c -> ticksExisted > 0 && c.getPaint().isEnemy() && CellTracker.isNearRuin[c.getMapLocation().x][c.getMapLocation().y]));
        // // chase enemy cell
        // suppliers.add(() -> getNearestCell(c -> c.getPaint().isEnemy()));
        roamStrategy = new AggroRoamStrategy();
    }


    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("OFFENCE MOPPER");

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

        // attack
        MapLocation target = bot.doBestAttack();
        if (target != null) {
            bot.pathfinder.makeMove(target);
        } else {
            roamStrategy.runTick();
        }
    }
}
