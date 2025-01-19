package caterpillow.robot.agents.soldier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import battlecode.common.RobotInfo;
import caterpillow.Game;

import static caterpillow.Game.rc;
import static caterpillow.Game.seed;
import static caterpillow.Game.time;
import static caterpillow.tracking.CellTracker.getNearestCell;
import static caterpillow.util.Util.*;

import caterpillow.robot.Strategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.robot.agents.roaming.WeakAggroRoamStrategy;
import caterpillow.tracking.RobotTracker;
import caterpillow.util.Pair;

public class ScoutStrategy extends Strategy {

    int skipCooldown = 0;
    ArrayList<MapLocation> visitedRuins;
    LinkedList<Pair<MapLocation, Integer>> skippedRuins;

    Soldier bot;

    Random rng;
    HandleRuinStrategy handleRuinStrategy;
    WeakRefillStrategy refillStrategy;
    Strategy attackTowerStrategy;

    WeakAggroRoamStrategy roamStrategy;

    public ScoutStrategy() throws GameActionException {
        bot = (Soldier) Game.bot;
        visitedRuins = new ArrayList<>();
        skippedRuins = new LinkedList<>();
        rng = new Random(seed);
        roamStrategy = new WeakAggroRoamStrategy();
        skipCooldown = (rc.getMapHeight() + rc.getMapWidth()) / 2;
    }
    public ScoutStrategy(MapLocation target) throws GameActionException {
        this();
        roamStrategy = new WeakAggroRoamStrategy(target);
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("SCOUTING " + isInDanger(rc.getLocation()) + " " + rc.getLocation().toString());
        skippedRuins.removeIf(el -> time >= el.second + skipCooldown);

        if (refillStrategy == null && getPaintLevel() < 0.8) {
            RobotInfo nearest = RobotTracker.getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getPaintAmount() >= missingPaint());
            if (nearest != null) {
                if (rc.canTransferPaint(nearest.getLocation(), -1)) {
                    bot.refill(nearest);
                } else if (getPaintLevel() < 0.5) {
                    refillStrategy = new WeakRefillStrategy(nearest.getLocation(), 0.1);
                }
            }
        }
        if(tryStrategy(refillStrategy)) return;

        if (handleRuinStrategy == null) {
            MapInfo target1 = getNearestCell(c ->
                                             isRuin(c.getMapLocation())
                                             && (c.getMapLocation().distanceSquaredTo(roamStrategy.target) <= rc.getLocation().distanceSquaredTo(roamStrategy.target)
                                                 || !rc.canSenseLocation(c.getMapLocation().add(Direction.SOUTH))
                                                 || !rc.senseMapInfo(c.getMapLocation().add(Direction.SOUTH)).getMark().equals(PaintType.ALLY_SECONDARY))
                                             && !visitedRuins.contains(c.getMapLocation())
                                             && skippedRuins.stream().noneMatch(el -> el.first.equals(c.getMapLocation())));
            if (target1 != null) {
                handleRuinStrategy = new HandleRuinStrategy(target1.getMapLocation());
            }
        }
        if (handleRuinStrategy != null) {
            if (handleRuinStrategy.isComplete()) {
                if (handleRuinStrategy.didSkip()) {
                    skippedRuins.add(new Pair<>(handleRuinStrategy.target, time));
                } else {
                    visitedRuins.add(handleRuinStrategy.target);
                }
                handleRuinStrategy = null;
            } else {
                handleRuinStrategy.runTick();
                return;
            }
        }

        if(attackTowerStrategy == null) {
            RobotInfo enemyTower = RobotTracker.getNearestRobot(b -> b.getType().isTowerType() && !isFriendly(b));
            if (enemyTower != null) {
                attackTowerStrategy = new AttackTowerStrategy(enemyTower.getLocation());
            }
        }
        if(tryStrategy(attackTowerStrategy)) return;

        roamStrategy.runTick();
        MapInfo nearest = getNearestCell(c -> c.getPaint().equals(PaintType.EMPTY) && rc.canAttack(c.getMapLocation()) && paintLevel() > 0.7);
        if (nearest != null ) {
            bot.checkerboardAttack(nearest.getMapLocation());
        }
    }
}
