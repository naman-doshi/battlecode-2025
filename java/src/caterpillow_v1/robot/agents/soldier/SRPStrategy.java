package caterpillow_v1.robot.agents.soldier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow_v1.Config;
import caterpillow_v1.Game;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.robot.agents.UpgradeTowerStrategy;
import caterpillow_v1.robot.agents.WeakRefillStrategy;
import caterpillow_v1.util.Pair;

import static caterpillow_v1.Game.*;
import static caterpillow_v1.Config.*;
import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.world.GameStage.MID;

public class SRPStrategy extends Strategy {

    public Soldier bot;

    // enemyLocs is more like "POI locs"
//    public List<MapLocation> enemyLocs;
    public MapLocation enemy;
    HandleRuinStrategy handleRuinStrategy;
    WeakRefillStrategy refillStrategy;
    int skipCooldown = 0;
    ArrayList<MapLocation> visitedRuins;
    LinkedList<Pair<MapLocation, Integer>> skippedRuins;
    int towerStratCooldown;
    Random rng;

    public SRPStrategy() throws GameActionException {
        bot = (Soldier) Game.bot;
        rng = new Random(seed);

        // cursed way to only keep the first elem but idc
//        this.enemyLocs = guessEnemyLocs(bot.home);
//        this.enemyLocs.removeLast();
//        this.enemyLocs.removeLast();
//
//        this.enemy = enemyLocs.get(0);
        enemy = genExplorationTarget(rng);


        visitedRuins = new ArrayList<>();
        skippedRuins = new LinkedList<>();
        towerStratCooldown = 0;

        skipCooldown = (rc.getMapHeight() + rc.getMapWidth()) / 2;
        
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    public void safeMove(MapLocation loc) throws GameActionException {
        if (rc.getLocation().isAdjacentTo(loc) && !rc.senseMapInfo(loc).getPaint().isAlly()) {
            return;
        }
        // wait until andy's buffed pathfinder
        bot.pathfinder.makeMove(loc);
    }

    void refresh() {
        skippedRuins.removeIf(el -> time >= el.second + skipCooldown);
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("SRP");
        refresh();
        towerStratCooldown--;
        // TODO: better scouting system!!!

        if (rc.canSenseLocation(enemy)) {
            // if we can see the enemy, just go to the next enemy loc.
//            enemyLocs.removeFirst();
//            enemyLocs.add(Config.genExplorationTarget());

            // procedurally gen the next one
//            while (enemyLocs.size() < 1) {
//                Random rng = new Random();
//                int x = rng.nextInt(0, rc.getMapWidth() - 1);
//                int y = rng.nextInt(0, rc.getMapHeight() - 1);
//                if (new MapLocation(x, y).distanceSquaredTo(rc.getLocation()) >= 9) {
//                    MapLocation moveDir = subtract(new MapLocation(x, y), rc.getLocation());
//                    enemyLocs.addLast(project(rc.getLocation(), moveDir));
//                }
//            }
            
//            enemy = enemyLocs.getFirst();
            enemy = Config.genExplorationTarget(rng);
            //indicate("NEW ENEMY LOC: " + enemy);
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
                //System.out.println("running refill strat");
            }
            return;
        }

        if (isPaintBelowHalf()) {
            RobotInfo nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getPaintAmount() >= missingPaint());
            if (nearest != null) {
                refillStrategy = new WeakRefillStrategy(nearest.getLocation(), 0.3);
                runTick();
            }
        }

        if (maxedTowers()) {
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

        // first: it obviously needs to be the wrong colour, non-enemy paint, and passable
        // second: if it's outside a ruin OR a neutral colour, obviously paint it
        // but if it's inside and painted w ally, only paint it if there's more than one SRP on it (since one of them will be the ruin's SRP)
        List<MapLocation> ruins = new ArrayList<>();
        List<MapLocation> towers = new ArrayList<>();
        for (MapInfo ruin : rc.senseNearbyMapInfos()) {
            if (ruin.hasRuin()) {
                RobotInfo info = rc.senseRobotAtLocation(rc.getLocation());
                if (info == null/* || !isFriendly(info)*/) {
                    ruins.add(ruin.getMapLocation());
                } else if (isFriendly(info)) {
                    towers.add(ruin.getMapLocation());
                }
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

        MapInfo target = null;
        for (MapInfo cell : rc.senseNearbyMapInfos()) {
            if (cell.getPaint() != checkerboardPaint(cell.getMapLocation()) && cell.isPassable() && !cell.getPaint().isEnemy()) {
                // needs painting and is paintable
                boolean b = true;
                for (MapLocation ruin : ruins) {
                    if (isCellInTowerBounds(ruin, cell.getMapLocation())) {
                        b = false;
                        break;
                    }
                }
                if (b) {
                    // dont block ourselves from building tower
                    if (target == null || target.getMapLocation().distanceSquaredTo(rc.getLocation()) > cell.getMapLocation().distanceSquaredTo(rc.getLocation())) {
                        target = cell;
                    }
                }
//                }
            }
        }



        //System.out.println("Left after target selection: " + Clock.getBytecodesLeft());
        
        if (target != null) {
            if (rc.canAttack(target.getMapLocation())) {
                bot.checkerboardAttack(target.getMapLocation());
            } else {
                safeMove(target.getMapLocation());
            }
        } else {
            safeMove(enemy);
            //indicate("moving to enemy");
        }

        // if (Clock.getBytecodesLeft() < 100) {
        //     System.out.println("Ran out of bytecodes");
        // }

        //System.out.println("Left after atk: " + Clock.getBytecodesLeft());
        if (towerStratCooldown > 0) {
            return;
        }

        MapInfo target1 = getNearestCell(c -> c.hasRuin() && !visitedRuins.contains(c.getMapLocation()) && rc.senseRobotAtLocation(c.getMapLocation()) == null && skippedRuins.stream().noneMatch(el -> el.first.equals(c.getMapLocation())));
        if (target1 != null) {
            println("starting handle ruin strat");
            handleRuinStrategy = new HandleRuinStrategy(target1.getMapLocation(), Config.getNextType());
            runTick();
            return;
        }

    }
}
