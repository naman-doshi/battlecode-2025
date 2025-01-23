package caterpillow.robot.agents.soldier;

import static caterpillow.Game.*;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.LinkedList;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import static caterpillow.Config.canUpgrade;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.StrongRefillStrategy;
import caterpillow.robot.agents.TraverseStrategy;
import caterpillow.robot.agents.UpgradeTowerStrategy;
import caterpillow.robot.agents.roaming.ExplorationRoamStrategy;
import caterpillow.tracking.CellTracker;
import caterpillow.tracking.TowerTracker;
import caterpillow.util.GamePredicate;
import caterpillow.util.Pair;

import static caterpillow.util.Util.getPaintLevel;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.isOccupied;
import  static caterpillow.world.GameStage.MID;

public class SRPStrategy extends Strategy {

    final int ATTACK_RAD = 4;

    public Soldier bot;

    HandleRuinStrategy handleRuinStrategy;
    LinkedList<Pair<MapLocation, Integer>> visitedRuins;
    int towerStratCooldown;
    int skipCooldown;
    int refillCooldown;
    Random rng;

    Strategy roamStrategy;
    TraverseStrategy traverseStrategy;
    PaintSRPStrategy paintSRPStrategy;
    Strategy refillStrategy;
    Strategy attackTowerStrategy;
    Strategy upgradeTowerStrategy;

    int w = mapWidth;
    int h = mapHeight;

    public SRPStrategy() throws GameActionException {
        bot = (Soldier) Game.bot;
        rng = new Random(seed);
        visitedRuins = new LinkedList<>();
        towerStratCooldown = 0;
        skipCooldown = (w + h) / 2;
        roamStrategy = new ExplorationRoamStrategy(true);
        refillCooldown = -100000;
    }

    public SRPStrategy(MapLocation target) throws GameActionException {
        this();
        roamStrategy = new ExplorationRoamStrategy(target, true);
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("SRP");
        visitedRuins.removeIf(el -> time >= el.second + skipCooldown);
        towerStratCooldown--;

        if (gameStage.equals(MID)) {
            RobotInfo enemyTower = TowerTracker.getNearestVisibleTower(b -> !isFriendly(b));
            if (enemyTower != null) {
                attackTowerStrategy = new AttackTowerStrategy(enemyTower.getLocation());
            }
        }

        if (tryStrategy(attackTowerStrategy)) return;
        attackTowerStrategy = null;

        if (handleRuinStrategy == null && towerStratCooldown <= 0) {
            MapLocation target1 = CellTracker.getNearestRuin(c -> !isOccupied(c) && visitedRuins.stream().noneMatch(el -> el.first.equals(c)));
            if (target1 != null) {
                handleRuinStrategy = new HandleRuinStrategy(target1);
            }
        }

        if (handleRuinStrategy != null) {
            if (handleRuinStrategy.isComplete()) {
                visitedRuins.add(new Pair<>(handleRuinStrategy.target, time));
                handleRuinStrategy = null;
                towerStratCooldown = 30;
            } else {
                handleRuinStrategy.runTick();
                return;
            }
        }

        if (gameStage.equals(MID)) {
            for (int level = 2; level <= 3; level++) {
                if (canUpgrade(level)) {
                    int finalLevel = level;
                    RobotInfo nearest = TowerTracker.getNearestVisibleTower(b -> isFriendly(b) && b.getType().level == finalLevel - 1);
                    if (nearest != null) {
                        upgradeTowerStrategy = new UpgradeTowerStrategy(nearest.getLocation(), level);
                    }
                }
            }
        }

        if (tryStrategy(upgradeTowerStrategy)) return;
        upgradeTowerStrategy = null;

        if (paintSRPStrategy != null) {
            indicate("ignoreCooldown: " + CellTracker.ignoreCooldown[paintSRPStrategy.centre.x][paintSRPStrategy.centre.y]);
            if(paintSRPStrategy.isComplete() || CellTracker.ignoreCooldown[paintSRPStrategy.centre.x][paintSRPStrategy.centre.y] >= time) {
                paintSRPStrategy = null;
            } else {
                paintSRPStrategy.runTick();
                return;
            }
        }

        if (refillStrategy == null && getPaintLevel() < 0.8) {
            if (handleRuinStrategy == null && getPaintLevel() < 0.5 && time - refillCooldown > 40) {
                refillStrategy = new StrongRefillStrategy(0.7);
            } else {
                RobotInfo nearest = TowerTracker.getNearestVisibleTower(b -> isFriendly(b) && rc.canTransferPaint(b.getLocation(), -1));
                if (nearest != null) {
                    bot.refill(nearest);
                }
            }
        }

        if (tryStrategy(refillStrategy)) return;
        if (refillStrategy != null && !((StrongRefillStrategy) refillStrategy).success) {
            refillCooldown = time;
        }
        refillStrategy = null;

        if (CellTracker.hasInitSRP) {
            GamePredicate<MapLocation> pred = loc -> {
                int x = loc.x;
                int y = loc.y;
                if (x < 2 || y < 2 || x >= rc.getMapWidth() - 2 || y >= rc.getMapHeight() - 2) {
                    return false;
                }
                if (CellTracker.ignoreCooldown[x][y] + CellTracker.ignoreCooldownReset >= time) return false;
                if(CellTracker.mapInfos[x][y].hasRuin()) return false;
                if (rc.canSenseLocation(loc) && rc.senseMapInfo(loc).isResourcePatternCenter()) return false;
                return true;
            };

            if (traverseStrategy == null || !pred.test(traverseStrategy.target)) {
                MapInfo info = CellTracker.getNearestCell(c -> pred.test(c.getMapLocation()));
                if (info != null) traverseStrategy = new TraverseStrategy(info.getMapLocation(), 0);
                else traverseStrategy = null;
            }
            if (traverseStrategy != null && traverseStrategy.isComplete()) {
                indicate(traverseStrategy.target.toString() + " reached");
                rc.mark(traverseStrategy.target, false);
                paintSRPStrategy = new PaintSRPStrategy(traverseStrategy.target);
                paintSRPStrategy.runTick();
                traverseStrategy = null;
            }
        }
        bot.lastMove = true;
        if(traverseStrategy != null) {
            traverseStrategy.runTick();
        } else {
            roamStrategy.runTick();
        }
    }
}
