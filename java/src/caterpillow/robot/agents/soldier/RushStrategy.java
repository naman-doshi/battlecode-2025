package caterpillow.robot.agents.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.HomeStrategy;
import caterpillow.robot.agents.TraverseStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

// pathfinding testing
public class RushStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    ArrayList<MapLocation> todo;

    Strategy primary;
    Strategy secondary;

    public RushStrategy() {
        bot = (Agent) Game.bot;
        target = rot180(bot.home);
        todo = new ArrayList<>();

        // add backup targets
        todo.add(flipHor(bot.home));
        todo.add(flipVer(bot.home));

        Collections.shuffle(todo, new Random(seed));

        // starting strategy
        primary = new TraverseStrategy(target, rc.getType().actionRadiusSquared);
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return primary instanceof HomeStrategy && primary.isComplete();
    }

    @Override
    public void runTick() throws GameActionException {
        rc.setIndicatorString("RUSHING");
        // go home if its run out of things to do (which is unlikely since itll probably die first)
        if (target == null && todo.isEmpty()) {
            if (!(primary instanceof HomeStrategy)) {
                primary = new HomeStrategy();
            }
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
                secondary = null;
            } else {
                secondary.runTick();
                return;
            }
        }

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
