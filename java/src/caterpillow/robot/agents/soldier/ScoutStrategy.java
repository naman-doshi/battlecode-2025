package caterpillow.robot.agents.soldier;

import battlecode.common.*;
import caterpillow.Game;
import caterpillow.packet.Packet;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.TraverseStrategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.util.Pair;
import caterpillow.util.TowerTracker;

import java.util.*;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class ScoutStrategy extends Strategy {

    int skipCooldown = 0;
    ArrayList<MapLocation> visitedRuins;
    LinkedList<Pair<MapLocation, Integer>> skippedRuins;
    List<MapLocation> goals;

    Agent bot;
    UnitType towerPref;

    HandleRuinStrategy handleRuinStrategy;
    WeakRefillStrategy refillStrategy;

    Strategy traverse;
    void move() throws GameActionException {
        while (traverse.isComplete()) {
            goals.addLast(goals.getFirst());
            goals.removeFirst();
            traverse = new TraverseStrategy(goals.getFirst(), VISION_RAD);
        }
        traverse.runTick();
    }

    public ScoutStrategy(UnitType towerPref) {
        bot = (Agent) Game.bot;
        this.towerPref = towerPref;
        visitedRuins = new ArrayList<>();
        skippedRuins = new LinkedList<>();
        goals = new LinkedList<MapLocation>();
        // populate goals
        Random rng = new Random(seed);
        while (goals.size() < 5) {
            int x = rng.nextInt(0, rc.getMapWidth() - 1);
            int y = rng.nextInt(0, rc.getMapHeight() - 1);
            if (new MapLocation(x, y).distanceSquaredTo(rc.getLocation()) < 9) {
                continue;
            }
            MapLocation moveDir = subtract(new MapLocation(x, y), rc.getLocation());
            goals.add(project(rc.getLocation(), moveDir, (double) (rc.getMapWidth() + rc.getMapHeight()) / 2));
        }

        traverse = new TraverseStrategy(goals.getFirst(), VISION_RAD);
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

        MapInfo target = getNearestCell(c -> c.hasRuin() && !visitedRuins.contains(c.getMapLocation()) && rc.senseRobotAtLocation(c.getMapLocation()) == null && skippedRuins.stream().noneMatch(el -> el.first.equals(c.getMapLocation())));
        if (target != null) {
            println("starting handle ruin strat");
            handleRuinStrategy = new HandleRuinStrategy(target.getMapLocation(), TowerTracker.getNextType());
            runTick();
            return;
        }
        move();
    }
}
