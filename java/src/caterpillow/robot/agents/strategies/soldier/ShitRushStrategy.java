package caterpillow.robot.agents.strategies.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.strategies.AttackTowerStrategy;
import caterpillow.robot.agents.strategies.HomeStrategy;
import caterpillow.robot.agents.strategies.TraverseStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class ShitRushStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    ArrayList<MapLocation> todo;

    Strategy primary;
    Strategy secondary;

    public ShitRushStrategy() {
        bot = (Agent) Game.bot;
        MapLocation mvec = subtract(centre, rc.getLocation());
        MapLocation pivot = add(rc.getLocation(), scale(mvec, 0.75));
        target = project(rc.getLocation(), mvec);
        todo = new ArrayList<>();

        // add backup targets
        todo.add(project(pivot, rotl(mvec)));
        todo.add(project(pivot, rotr(mvec)));

        Collections.shuffle(todo, new Random(seed));

        // starting strategy
        primary = new TraverseStrategy(target, rc.getType().actionRadiusSquared);
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        if (primary instanceof ShitEverywhereStrategy) {
            primary.runTick();
            return;
        }

        if (secondary == null) {
            RobotInfo nearest = getNearestRobot(b -> !isFriendly(b) && b.getType().isTowerType());
            if (nearest != null) {
                secondary = new AttackTowerStrategy(nearest.getLocation());
            }
        }

        if (secondary != null) {
            if (secondary.isComplete()) {
                // broken tower
                // immediately start shitting everywhere
                secondary = null;
                primary = new ShitEverywhereStrategy();
                runTick();
                return;
            } else {
                secondary.runTick();
                return;
            }
        }

        rc.setIndicatorString("SHIT RUSH STRATEGY");

        if (primary.isComplete()) {
            if (todo.isEmpty()) {
                // start going home
                target = null;
                runTick();
                return;
            }
            target = todo.getLast();
            todo.removeLast();
            primary = new TraverseStrategy(target, rc.getType().actionRadiusSquared);
        }
        primary.runTick();
    }
}
