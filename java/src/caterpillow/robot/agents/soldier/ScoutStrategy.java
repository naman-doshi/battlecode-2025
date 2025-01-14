package caterpillow.robot.agents.soldier;

import battlecode.common.*;
import caterpillow.Config;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.robot.agents.roaming.StrongAggroRoamStrategy;
import caterpillow.robot.agents.roaming.WeakAggroRoamStrategy;
import caterpillow.util.Pair;

import java.util.*;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;
import static caterpillow.world.GameStage.MID;

public class ScoutStrategy extends Strategy {

    int skipCooldown = 0;
    ArrayList<MapLocation> visitedRuins;
    LinkedList<Pair<MapLocation, Integer>> skippedRuins;

    Soldier bot;

    Random rng;
    HandleRuinStrategy handleRuinStrategy;
    WeakRefillStrategy refillStrategy;

    Strategy roamStrategy;

    public ScoutStrategy() throws GameActionException {
        bot = (Soldier) Game.bot;
        visitedRuins = new ArrayList<>();
        skippedRuins = new LinkedList<>();
        rng = new Random(seed);
        roamStrategy = new WeakAggroRoamStrategy();
        skipCooldown = (rc.getMapHeight() + rc.getMapWidth()) / 2;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("SCOUTING");
        skippedRuins.removeIf(el -> time >= el.second + skipCooldown);

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

        if (isPaintBelowHalf()) {
            RobotInfo nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getPaintAmount() >= missingPaint());
            if (nearest != null) {
                bot.secondaryStrategy = new WeakRefillStrategy(nearest.getLocation(), 0.1);
                bot.runTick();
                return;
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

        if (!maxedTowers()) {
            MapInfo target = getNearestCell(c -> c.hasRuin() && !visitedRuins.contains(c.getMapLocation()) && rc.senseRobotAtLocation(c.getMapLocation()) == null && skippedRuins.stream().noneMatch(el -> el.first.equals(c.getMapLocation())));
            if (target != null) {
                println("starting handle ruin strat");
                handleRuinStrategy = new HandleRuinStrategy(target.getMapLocation(), Config.getNextType());
                runTick();
                return;
            }
        }

        MapInfo nearest = getNearestCell(c -> c.getPaint().equals(PaintType.EMPTY) && rc.canAttack(c.getMapLocation()) && paintLevel() > 0.7);
        if (nearest != null) {
            bot.checkerboardAttack(nearest.getMapLocation());
        }
        roamStrategy.runTick();
    }
}
