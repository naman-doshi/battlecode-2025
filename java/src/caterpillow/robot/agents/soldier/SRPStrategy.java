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
import caterpillow.util.Pair;
import caterpillow.util.Profiler;

import static caterpillow.util.Util.*;
import static caterpillow.world.GameStage.MID;
import static java.lang.Math.max;
import static java.lang.Math.min;

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

    public final int ignoreCooldownReset = 30;
    int[][] ignoreCooldown; // last time that this cell was verified to not be paintable

    public SRPStrategy() throws GameActionException {
        bot = (Soldier) Game.bot;
        rng = new Random(seed);
        visitedRuins = new ArrayList<>();
        skippedRuins = new LinkedList<>();
        towerStratCooldown = 0;
        skipCooldown = (rc.getMapHeight() + rc.getMapWidth()) / 2;
        roamStrategy = new ExplorationRoamStrategy();
        ignoreCooldown = new int[rc.getMapWidth()][rc.getMapHeight()];
        for (int i = 0; i < rc.getMapWidth(); i++) {
            for (int j = 0; j < rc.getMapHeight(); j++) {
                ignoreCooldown[i][j] = -10000000;
            }
        }
    }

    public boolean isPossibleTarget(MapInfo cell) {
        if (time - ignoreCooldown[cell.getMapLocation().x][cell.getMapLocation().y] <= ignoreCooldownReset) {
            return false;
        }
        if (cell.getPaint() != checkerboardPaint(cell.getMapLocation()) && cell.isPassable() && !cell.getPaint().isEnemy()) {
            return true;
        }
        return false;
    }

    List<MapLocation> ruins;

    public void update(MapLocation loc) throws GameActionException {
        for (int i = ruins.size() - 1; i >= 0; i--) {
            MapLocation ruin = ruins.get(i);
            if (isCellInTowerBounds(ruin, loc)) {
                ignoreCooldown[loc.x][loc.y] = time;
                return;
            }
        }
        if (loc.distanceSquaredTo(rc.getLocation()) <= ATTACK_RAD) {
            // guaranteed to be safe
            ignoreCooldown[loc.x][loc.y] = -10000000;
        }
    }

    public void updateStates() throws GameActionException {
        MapLocation[] nearbyRuins = rc.senseNearbyRuins(VISION_RAD);
        for (int i = nearbyRuins.length - 1; i >= 0; i--) {
            MapLocation ruin = nearbyRuins[i];
            if (rc.senseRobotAtLocation(ruin) != null) {
                continue;
            }

            for (int x = max(0, ruin.x - 2); x <= min(rc.getMapWidth() - 1, ruin.x + 2); x++) {
                for (int y = max(0, ruin.y - 2); y <= min(rc.getMapHeight() - 1, ruin.y + 2); y++) {
                    ignoreCooldown[x][y] = time;
                }
            }
        }

        MapInfo[] cells = rc.senseNearbyMapInfos(rc.getType().actionRadiusSquared);
        for (int i = cells.length - 1; i >= 0; i--) {
            MapLocation loc = cells[i].getMapLocation();
            if (ignoreCooldown[loc.x][loc.y] < time) {
                ignoreCooldown[loc.x][loc.y] = -10000000;
            }
        }
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
        if (handleRuinStrategy != null) {
            if (handleRuinStrategy.isComplete()) {
                if (handleRuinStrategy.didSkip()) {
                    skippedRuins.add(new Pair<>(handleRuinStrategy.target, time));
                } else {
                    visitedRuins.add(handleRuinStrategy.target);
                }
                handleRuinStrategy = null;
                towerStratCooldown = 30;
                runTick();
            } else {
                handleRuinStrategy.runTick();
            }
            return;
        }

        if (isPaintBelowHalf()) {
            RobotInfo nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getPaintAmount() >= missingPaint());
            if (nearest != null) {
                bot.secondaryStrategy = new WeakRefillStrategy(nearest.getLocation(), 0.3);
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

        if (gameStage.equals(MID)) {
            for (int level = 2; level <= 3; level++) {
                if (canUpgrade(level)) {
                    int finalLevel = level;
                    RobotInfo nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getType().level == finalLevel - 1);
                    if (nearest != null) {
                        bot.secondaryStrategy = new UpgradeTowerStrategy(nearest.getLocation(), level);
                        bot.runTick();
                        return;
                    }
                }
            }
        }

        if (towerStratCooldown <= 0) {
            MapInfo target1 = getNearestCell(c -> c.hasRuin() && !visitedRuins.contains(c.getMapLocation()) && rc.senseRobotAtLocation(c.getMapLocation()) == null && skippedRuins.stream().noneMatch(el -> el.first.equals(c.getMapLocation())));
            if (target1 != null) {
                handleRuinStrategy = new HandleRuinStrategy(target1.getMapLocation(), Config.getNextType());
                runTick();
                return;
            }
        }

        updateStates();
        MapInfo target = getNearestCell(this::isPossibleTarget);
        if (target != null) {
            if (rc.getLocation().distanceSquaredTo(target.getMapLocation()) <= ATTACK_RAD) {
                if (rc.canAttack(target.getMapLocation())) {
                    bot.checkerboardAttack(target.getMapLocation());
                }
            } else {
                bot.pathfinder.makeMove(target.getMapLocation());
                updateStates();
                if (rc.isActionReady()) {
                    MapInfo target2 = getNearestCell(this::isPossibleTarget, ATTACK_RAD);
                    if (target2 != null && rc.canAttack(target2.getMapLocation())) {
                        bot.checkerboardAttack(target2.getMapLocation());
                    }
                }
            }
            return;
        }

        roamStrategy.runTick();
    }
}
