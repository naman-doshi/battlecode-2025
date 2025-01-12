package caterpillow.robot.agents.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.UnitType;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.TraverseStrategy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class ScoutStrategy extends Strategy {

    ArrayList<MapLocation> visitedRuins;
    ArrayList<MapLocation> skippedRuins;
    List<MapLocation> goals;

    Agent bot;
    MapLocation target;

    HandleRuinStrategy handleRuinStrategy;

    Strategy traverse;
    void move() throws GameActionException {
        while (traverse.isComplete()) {
            goals.addLast(goals.getFirst());
            goals.removeFirst();
            traverse = new TraverseStrategy(goals.getFirst(), VISION_RAD);
        }
        traverse.runTick();
    }

    public ScoutStrategy() {
        bot = (Agent) Game.bot;

        visitedRuins = new ArrayList<>();
        skippedRuins = new ArrayList<>();
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
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        rc.setIndicatorString("SCOUTING");
        if (handleRuinStrategy!= null) {
            if (handleRuinStrategy.isComplete()) {
                visitedRuins.add(handleRuinStrategy.target);
                if (handleRuinStrategy.didSkip()) {
                    skippedRuins.add(handleRuinStrategy.target);
                }
                handleRuinStrategy = null;
                runTick();
            } else {
                handleRuinStrategy.runTick();
            }
            return;
        }

        MapInfo target = getNearestCell(c -> c.hasRuin() && !visitedRuins.contains(c.getMapLocation()) && rc.senseRobotAtLocation(c.getMapLocation()) == null);
        if (target != null) {
            handleRuinStrategy = new HandleRuinStrategy(target.getMapLocation());
            runTick();
            return;
        }
        move();
    }
}
