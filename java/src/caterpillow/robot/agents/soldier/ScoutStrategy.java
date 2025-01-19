package caterpillow.robot.agents.soldier;

import java.util.LinkedList;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import battlecode.common.RobotInfo;
import caterpillow.Game;

import static caterpillow.Game.rc;
import static caterpillow.Game.seed;
import static caterpillow.Game.time;
import static caterpillow.util.Util.*;

import caterpillow.robot.Strategy;
import caterpillow.robot.agents.StrongRefillStrategy;
import caterpillow.robot.agents.roaming.WeakAggroRoamStrategy;
import caterpillow.tracking.CellTracker;
import caterpillow.tracking.TowerTracker;
import caterpillow.util.Pair;

public class ScoutStrategy extends Strategy {

    int skipCooldown = 0;
    LinkedList<Pair<MapLocation, Integer>> visitedRuins;

    Soldier bot;

    Random rng;
    HandleRuinStrategy handleRuinStrategy;
    Strategy refillStrategy;
    Strategy attackTowerStrategy;

    WeakAggroRoamStrategy roamStrategy;

    public ScoutStrategy() throws GameActionException {
        bot = (Soldier) Game.bot;
        visitedRuins = new LinkedList<>();
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
        visitedRuins.removeIf(el -> time >= el.second + skipCooldown);

        if (refillStrategy == null && getPaintLevel() < 0.8) {
            // we need a solid amount of paint
            if (handleRuinStrategy == null && getPaintLevel() < 0.7) {
                refillStrategy = new StrongRefillStrategy(0.8);
            } else {
                RobotInfo nearest = TowerTracker.getNearestTower(b -> isFriendly(b) && rc.canTransferPaint(b.getLocation(), -1));
                if (nearest != null) {
                    bot.refill(nearest);
                }
//                refillStrategy = new WeakRefillStrategy(0.2);
            }
        }

        if (tryStrategy(refillStrategy)) return;
        refillStrategy = null;

        if (handleRuinStrategy == null) {
            MapLocation target1 = CellTracker.getNearestRuin(c -> !isOccupied(c) && visitedRuins.stream().noneMatch(el -> el.first.equals(c)));
            if (target1 != null) {
                handleRuinStrategy = new HandleRuinStrategy(target1);
            }
        }
        if (handleRuinStrategy != null) {
            if (handleRuinStrategy.isComplete()) {
                visitedRuins.add(new Pair<>(handleRuinStrategy.target, time));
                handleRuinStrategy = null;
            } else {
                handleRuinStrategy.runTick();
                return;
            }
        }

        if (attackTowerStrategy == null) {
            RobotInfo enemyTower = TowerTracker.getNearestTower(b -> !isFriendly(b));
            if (enemyTower != null) {
                attackTowerStrategy = new AttackTowerStrategy(enemyTower.getLocation());
            }
        }

        if (tryStrategy(attackTowerStrategy)) return;
        attackTowerStrategy = null;

        roamStrategy.runTick();
        if (getPaintLevel() > 0.7 && rc.senseMapInfo(rc.getLocation()).getPaint().equals(PaintType.EMPTY)) {
            bot.checkerboardAttack(rc.getLocation());
        }
        // MapInfo nearest = getNearestCell(c -> c.getPaint().equals(PaintType.EMPTY) && rc.canAttack(c.getMapLocation()) && paintLevel() > 0.7);
        // if (nearest != null) {
        //     bot.checkerboardAttack(nearest.getMapLocation());
        // }
    }
}
