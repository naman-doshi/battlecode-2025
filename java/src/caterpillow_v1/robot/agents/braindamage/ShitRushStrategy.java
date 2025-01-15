package caterpillow_v1.robot.agents.braindamage;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow_v1.Game;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.robot.agents.Agent;
import caterpillow_v1.robot.agents.soldier.AttackTowerStrategy;
import caterpillow_v1.robot.agents.TraverseStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;

public class ShitRushStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    ArrayList<MapLocation> todo;

    Strategy primary;
    Strategy secondary;

    public ShitRushStrategy() {
        bot = (Agent) Game.bot;
        MapLocation mvec = subtract(centre, origin);
        target = project(origin, mvec);
        todo = new ArrayList<>();

        MapLocation pivot = add(origin, scale(mvec, 0.75));
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

        indicate("SHIT RUSH STRATEGY");

        if (primary.isComplete()) {
            if (todo.isEmpty()) {
                primary = new ShitEverywhereStrategy();
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
