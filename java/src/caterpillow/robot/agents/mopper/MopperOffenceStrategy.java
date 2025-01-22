package caterpillow.robot.agents.mopper;

import java.util.List;
import java.util.Random;

import battlecode.common.*;
import caterpillow.Config;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.robot.agents.roaming.AggroRoamStrategy;
import caterpillow.tracking.RobotTracker;
import caterpillow.util.GameSupplier;
import caterpillow.util.Profiler;

import static caterpillow.util.Util.*;

public class MopperOffenceStrategy extends Strategy {

    public Mopper bot;

    Random rng;
    public MapLocation lastSeenRuin;
    Strategy rescueStrategy;
    Strategy refillStrategy;
    Strategy roamStrategy;

    public MopperOffenceStrategy() throws GameActionException {
        bot = (Mopper) Game.bot;
        rng = new Random();
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
        System.out.println("remainnig bytecodes: " + Clock.getBytecodesLeft());
        Profiler.begin();
        boolean bruh = bot.doBestAttack();
        Profiler.end("bytecodes ");
        Profiler.begin();
        if (!bruh) {
            roamStrategy.runTick();
        }
        Profiler.end("rem");
    }
}
