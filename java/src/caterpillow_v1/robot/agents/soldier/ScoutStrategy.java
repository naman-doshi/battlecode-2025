package caterpillow_v1.robot.agents.soldier;

import battlecode.common.*;
import caterpillow_v1.Config;
import caterpillow_v1.Game;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.robot.agents.Agent;
import caterpillow_v1.robot.agents.TraverseStrategy;
import caterpillow_v1.robot.agents.WeakRefillStrategy;
import caterpillow_v1.util.Pair;

import java.util.*;

import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;
import static caterpillow_v1.world.GameStage.MID;

public class ScoutStrategy extends Strategy {

    int skipCooldown = 0;
    ArrayList<MapLocation> visitedRuins;
    LinkedList<Pair<MapLocation, Integer>> skippedRuins;

    Agent bot;

    Random rng;
    HandleRuinStrategy handleRuinStrategy;
    WeakRefillStrategy refillStrategy;

    Strategy traverse;
    void move() throws GameActionException {
        while (traverse.isComplete()) {
            traverse = new TraverseStrategy(Config.genExplorationTarget(rng), VISION_RAD);
        }
        traverse.runTick();
    }

    public ScoutStrategy() {
        bot = (Agent) Game.bot;
        visitedRuins = new ArrayList<>();
        skippedRuins = new LinkedList<>();
        rng = new Random(seed);
        traverse = new TraverseStrategy(Config.genExplorationTarget(rng), VISION_RAD);
        skipCooldown = (rc.getMapHeight() + rc.getMapWidth()) / 2;
    }

    void refresh() {
        skippedRuins.removeIf(el -> time >= el.second + skipCooldown);
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        refresh();
        indicate("SCOUTING");
        if (handleRuinStrategy != null) {
            if (handleRuinStrategy.isComplete()) {
                if (handleRuinStrategy.didSkip()) {
                    skippedRuins.add(new Pair<>(handleRuinStrategy.target, time));
                } else {
                    visitedRuins.add(handleRuinStrategy.target);
                }
                handleRuinStrategy = null;
                runTick();
            } else {
                handleRuinStrategy.runTick();
            }
            return;
        }

        if (refillStrategy != null) {
            if (refillStrategy.isComplete()) {
                refillStrategy = null;
                runTick();
            } else {
                refillStrategy.runTick();
            }
            return;
        }

        if (isPaintBelowHalf()) {
            RobotInfo nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getPaintAmount() >= missingPaint());
            if (nearest != null) {
                refillStrategy = new WeakRefillStrategy(nearest.getLocation(), 0.1);
                runTick();
            }
        }

        if (gameStage.equals(MID)) {
            RobotInfo enemyTower = getNearestRobot(b -> b.getType().isTowerType() && !isFriendly(b));
            if (enemyTower != null) {
                bot.secondaryStrategy = new AttackTowerStrategy(enemyTower.getLocation());
                bot.runTick();
                return;
            }
        }

        MapInfo target = getNearestCell(c -> c.hasRuin() && !visitedRuins.contains(c.getMapLocation()) && rc.senseRobotAtLocation(c.getMapLocation()) == null && skippedRuins.stream().noneMatch(el -> el.first.equals(c.getMapLocation())));
        if (target != null) {
            println("starting handle ruin strat");
            handleRuinStrategy = new HandleRuinStrategy(target.getMapLocation(), Config.getNextType());
            runTick();
            return;
        }
        move();
    }
}
