package caterpillow.robot.agents.soldier;

import java.util.*;

import battlecode.common.*;
import caterpillow.Config;
import static caterpillow.Config.canUpgrade;
import caterpillow.Game;

import static caterpillow.Game.gameStage;
import static caterpillow.Game.rc;
import static caterpillow.Game.seed;
import static caterpillow.Game.time;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.UpgradeTowerStrategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.robot.agents.roaming.ExplorationRoamStrategy;
import caterpillow.util.*;
import caterpillow.robot.agents.TraverseStrategy;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;
import static caterpillow.world.GameStage.MID;
import static java.lang.Math.*;

public class SRPStrategy extends Strategy {

    final int ATTACK_RAD = 4;

    public Soldier bot;

    HandleRuinStrategy handleRuinStrategy;
    ArrayList<MapLocation> visitedRuins;
    LinkedList<Pair<MapLocation, Integer>> skippedRuins;
    int towerStratCooldown;
    int skipCooldown;
    Random rng;

    Strategy roamStrategy;
    TraverseStrategy traverseStrategy;
    PaintSRPStrategy paintSRPStrategy;
    Strategy refillStrategy;
    Strategy attackTowerStrategy;
    Strategy upgradeTowerStrategy;

    public final int ignoreCooldownReset = 30;
    int[][] ignoreCooldown; // last time that this cell was verified to not be a valid centre
    boolean[][] wallProcessed; // whether we've processed this wall/other impassable square

    public SRPStrategy() throws GameActionException {
        bot = (Soldier) Game.bot;
        rng = new Random(seed);
        int w = rc.getMapWidth();
        int h = rc.getMapHeight();
        visitedRuins = new ArrayList<>();
        skippedRuins = new LinkedList<>();
        towerStratCooldown = 0;
        skipCooldown = (w + h) / 2;
        roamStrategy = new ExplorationRoamStrategy();
        Profiler.begin();
        ignoreCooldown = Initialiser.getIgnoreCooldown();
        Profiler.end("init ignoreCooldown");
        Profiler.begin();
        wallProcessed = new boolean[MAX_MAP_SIZE][MAX_MAP_SIZE];
        Profiler.end("init wallProcessed");
    }

    public void updateStates() throws GameActionException {
        MapLocation[] nearbyRuins = rc.senseNearbyRuins(VISION_RAD);
        for (int i = nearbyRuins.length - 1; i >= 0; i--) {
            MapLocation ruin = nearbyRuins[i];
            if (rc.senseRobotAtLocation(ruin) != null) {
                continue;
            }

            for (int x = max(0, ruin.x - 4); x <= min(rc.getMapWidth() - 1, ruin.x + 4); x++) {
                for (int y = max(0, ruin.y - 4); y <= min(rc.getMapHeight() - 1, ruin.y + 4); y++) {
                    ignoreCooldown[x][y] = max(ignoreCooldown[x][y], time);
                }
            }
        }

        // Profiler.begin();
        MapInfo[] cells = rc.senseNearbyMapInfos();
        for (int ci = cells.length - 1; ci >= 0; ci--) {
            MapLocation loc = cells[ci].getMapLocation();
            if(!cells[ci].isPassable() && !wallProcessed[loc.x][loc.y]) {
                for(int i = max(0, loc.x - 2); i <= min(rc.getMapWidth() - 1, loc.x + 2); i++) {
                    for(int j = max(0, loc.y - 2); j <= min(rc.getMapHeight() - 1, loc.y + 2); j++) {
                        ignoreCooldown[i][j] = 10000000;
                    }
                }
                wallProcessed[loc.x][loc.y] = true;
            }
            if(cells[ci].getMark().equals(PaintType.ALLY_PRIMARY) && !wallProcessed[loc.x][loc.y]) {
                rc.setIndicatorDot(loc, 0, 255, 255);
                for(int i = max(0, loc.x - 4); i <= min(rc.getMapWidth() - 1, loc.x + 4); i++) {
                    for(int j = max(0, loc.y - 4); j <= min(rc.getMapHeight() - 1, loc.y + 4); j++) {
                        if((i - loc.x) % 4 == 0 && (j - loc.y) % 4 == 0 || abs(i - loc.x) + abs(j - loc.y) == 7) continue; // tiling
                        ignoreCooldown[i][j] = 10000000;
                    }
                }
                wallProcessed[loc.x][loc.y] = true;
            }
            if(cells[ci].getPaint().isEnemy()) {
                if(rng.nextInt(0, 2) == 0) {
                    for(int i = max(0, loc.x - 2); i <= min(rc.getMapWidth() - 1, loc.x + 2); i++) {
                        for(int j = max(0, loc.y - 2); j <= min(rc.getMapHeight() - 1, loc.y + 2); j++) {
                            ignoreCooldown[i][j] = max(ignoreCooldown[i][j], time);
                        }
                    }
                }
            }
        }
        // Profiler.end();
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("SRP");
        skippedRuins.removeIf(el -> time >= el.second + skipCooldown);
        towerStratCooldown--;
        updateStates();

        if(refillStrategy == null && isPaintBelowHalf()) {
            RobotInfo nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getPaintAmount() >= missingPaint());
            if (nearest != null) {
                refillStrategy = new WeakRefillStrategy(nearest.getLocation(), 0.3);
            }
        }
        if (refillStrategy != null) {
            if(tryStrategy(refillStrategy)) return;
        }

        if (handleRuinStrategy == null && towerStratCooldown <= 0) {
            MapInfo target1 = getNearestCell(c -> c.hasRuin() && !visitedRuins.contains(c.getMapLocation()) && rc.senseRobotAtLocation(c.getMapLocation()) == null && skippedRuins.stream().noneMatch(el -> el.first.equals(c.getMapLocation())));
            if (target1 != null) {
                handleRuinStrategy = new HandleRuinStrategy(target1.getMapLocation(), Config.getNextType());
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
                towerStratCooldown = 30;
            } else {
                handleRuinStrategy.runTick();
            }
            return;
        }

        if (gameStage.equals(MID)) {
            RobotInfo enemyTower = getNearestRobot(b -> b.getType().isTowerType() && !isFriendly(b));
            if (enemyTower != null) {
                attackTowerStrategy = new AttackTowerStrategy(enemyTower.getLocation());
            }
        }
        if(attackTowerStrategy != null) {
            if(tryStrategy(attackTowerStrategy)) return;
        }

        if (gameStage.equals(MID)) {
            for (int level = 2; level <= 3; level++) {
                if (canUpgrade(level)) {
                    int finalLevel = level;
                    RobotInfo nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getType().level == finalLevel - 1);
                    if (nearest != null) {
                        upgradeTowerStrategy = new UpgradeTowerStrategy(nearest.getLocation(), level);
                    }
                }
            }
        }
        if(upgradeTowerStrategy != null) {
            if(tryStrategy(upgradeTowerStrategy)) return;
        }

        if (paintSRPStrategy != null) {
            if(paintSRPStrategy.isComplete() || ignoreCooldown[paintSRPStrategy.centre.x][paintSRPStrategy.centre.y] + ignoreCooldownReset >= time) {
                paintSRPStrategy = null;
            } else {
                paintSRPStrategy.runTick();
                return;
            }
        }

        GamePredicate<MapLocation> pred = loc -> {
            int x = loc.x;
            int y = loc.y;
            if(x < 2 || y < 2 || x >= rc.getMapWidth() - 2 || y >= rc.getMapHeight() - 2) {
                return false;
            }
            if(ignoreCooldown[x][y] + ignoreCooldownReset >= time) return false;
            if(rc.canSenseLocation(loc) && rc.senseMapInfo(loc).isResourcePatternCenter()) return false;
            return true;
        };

        if(traverseStrategy == null || !pred.test(traverseStrategy.target)) {
            MapInfo info = getNearestCell(c -> pred.test(c.getMapLocation()));
            if(info != null) traverseStrategy = new TraverseStrategy(info.getMapLocation(), 0);
            else traverseStrategy = null;
        }
        if(traverseStrategy != null && traverseStrategy.isComplete()) {
            indicate(traverseStrategy.target.toString() + " reached");
            rc.mark(traverseStrategy.target, false);
            paintSRPStrategy = new PaintSRPStrategy(traverseStrategy.target);
            paintSRPStrategy.runTick();
            traverseStrategy = null;
        }
        if(traverseStrategy != null) {
            traverseStrategy.runTick();
        } else {
            roamStrategy.runTick();
        }
    }
}
