package caterpillow.robot.agents.soldier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.Game.time;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.util.Pair;
import caterpillow.util.TowerTracker;
import static caterpillow.util.Util.cheapGetNearestCell;
import static caterpillow.util.Util.checkerboardPaint;
import static caterpillow.util.Util.decodeLoc;
import static caterpillow.util.Util.getNearestCell;
import static caterpillow.util.Util.getNearestRobot;
import static caterpillow.util.Util.getSRPIds;
import static caterpillow.util.Util.guessEnemyLocs;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.isPaintBelowHalf;
import static caterpillow.util.Util.isVisiblyWithinRuin;
import static caterpillow.util.Util.missingPaint;
import static caterpillow.util.Util.println;
import static caterpillow.util.Util.project;
import static caterpillow.util.Util.subtract;

public class SRPStrategy extends Strategy {

    public Soldier bot;

    // enemyLocs is more like "POI locs"
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;
    HandleRuinStrategy handleRuinStrategy;
    WeakRefillStrategy refillStrategy;
    UnitType towerPref;
    int skipCooldown = 0;
    ArrayList<MapLocation> visitedRuins;
    LinkedList<Pair<MapLocation, Integer>> skippedRuins;

    public SRPStrategy(UnitType towerPref) throws GameActionException {
        bot = (Soldier) Game.bot;

        // cursed way to only keep the first elem but idc
        this.enemyLocs = guessEnemyLocs(bot.home);
        this.enemyLocs.removeLast();
        this.enemyLocs.removeLast();
        
        this.enemy = enemyLocs.get(0);

        this.towerPref = towerPref;
        visitedRuins = new ArrayList<>();
        skippedRuins = new LinkedList<>();

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
        indicate("srp");
        refresh();
        // TODO: better scouting system!!!

        // get some paint from our towers if we're nearby one
        
        if (rc.canSenseLocation(enemy)) {
            // if we can see the enemy, just go to the next enemy loc.
            enemyLocs.removeFirst();

            // procedurally gen the next one
            while (enemyLocs.size() < 1) {
                Random rng = new Random();
                int x = rng.nextInt(0, rc.getMapWidth() - 1);
                int y = rng.nextInt(0, rc.getMapHeight() - 1);
                if (new MapLocation(x, y).distanceSquaredTo(rc.getLocation()) >= 9) {
                    MapLocation moveDir = subtract(new MapLocation(x, y), rc.getLocation());
                    enemyLocs.add(project(rc.getLocation(), moveDir, (double) (rc.getMapWidth() + rc.getMapHeight()) / 2));
                    MapLocation xy = new MapLocation(x, y);
                    enemyLocs.add(xy);
                }
            }
            
            enemy = enemyLocs.getFirst();
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

        // first: it obviously needs to be the wrong colour, non-enemy paint, and passable
        // second: if it's outside a ruin OR a neutral colour, obviously paint it
        // but if it's inside and painted w ally, only paint it if there's more than one SRP on it (since one of them will be the ruin's SRP)
        MapInfo target = cheapGetNearestCell(c -> c.getPaint()!=checkerboardPaint(c.getMapLocation()) && c.isPassable() && !c.getPaint().isEnemy() && 
        (!isVisiblyWithinRuin(c.getMapLocation()) || c.getPaint()==PaintType.EMPTY || getSRPIds(c.getMapLocation()).size() > 1));

        //System.out.println("Left after target selection: " + Clock.getBytecodesLeft());
        
        if (target != null) {
            if (rc.canAttack(target.getMapLocation())) {
                bot.checkerboardAttack(target.getMapLocation());
                // try complete the SRP i just attacked
                List<Integer> srpIDs = getSRPIds(target.getMapLocation());
                for (int id : srpIDs) {
                    MapLocation srpLoc = decodeLoc(id);
                    if (rc.canCompleteResourcePattern(srpLoc)) {
                        rc.completeResourcePattern(srpLoc);
                    }
                }
            } else {
                safeMove(target.getMapLocation());
                //indicate("moving to target " + target.getMapLocation());
            }
        } else {
            safeMove(enemy);
            //indicate("moving to enemy");
        }

        //System.out.println("Left after atk: " + Clock.getBytecodesLeft());

        MapInfo target1 = getNearestCell(c -> c.hasRuin() && !visitedRuins.contains(c.getMapLocation()) && rc.senseRobotAtLocation(c.getMapLocation()) == null && skippedRuins.stream().noneMatch(el -> el.first.equals(c.getMapLocation())));
        if (target1 != null) {
            println("starting handle ruin strat");
            handleRuinStrategy = new HandleRuinStrategy(target1.getMapLocation(), TowerTracker.getNextType());
            runTick();
            return;
        }

    }
}
