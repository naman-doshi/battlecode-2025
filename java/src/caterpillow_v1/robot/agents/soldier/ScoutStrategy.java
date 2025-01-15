package caterpillow_v1.robot.agents.soldier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import battlecode.common.RobotInfo;
import caterpillow_v1.Config;
import caterpillow_v1.Game;
import static caterpillow_v1.Game.gameStage;
import static caterpillow_v1.Game.rc;
import static caterpillow_v1.Game.seed;
import static caterpillow_v1.Game.time;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.robot.agents.WeakRefillStrategy;
import caterpillow_v1.robot.agents.roaming.WeakAggroRoamStrategy;
import caterpillow_v1.util.Pair;
import static caterpillow_v1.util.Util.getNearestCell;
import static caterpillow_v1.util.Util.getNearestRobot;
import static caterpillow_v1.util.Util.indicate;
import static caterpillow_v1.util.Util.isFriendly;
import static caterpillow_v1.util.Util.isPaintBelowHalf;
import static caterpillow_v1.util.Util.maxedTowers;
import static caterpillow_v1.util.Util.missingPaint;
import static caterpillow_v1.util.Util.paintLevel;
import static caterpillow_v1.util.Util.println;
import static caterpillow_v1.world.GameStage.MID;

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
