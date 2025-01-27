package caterpillow.robot.agents.mopper;

import java.util.List;
import caterpillow.util.CustomRandom;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Config;
import caterpillow.Game;
import static caterpillow.Game.*;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.robot.agents.roaming.AggroRoamStrategy;
import caterpillow.tracking.RobotTracker;
import caterpillow.util.GameSupplier;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.isAllyAgent;
import static caterpillow.util.Util.isPaintBelowHalf;

public class MopperOffenceStrategy extends Strategy {

    public Mopper bot;

    public List<GameSupplier<MapInfo>> suppliers;
    CustomRandom rng;
    public MapLocation lastSeenRuin;
    Strategy rescueStrategy;
    Strategy refillStrategy;
    Strategy roamStrategy;

    public MopperOffenceStrategy() throws GameActionException {
        bot = (Mopper) Game.bot;
        rng = new CustomRandom();

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
