package caterpillow.robot.agents.strategies.braindamage;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.strategies.AttackTowerStrategy;
import caterpillow.robot.agents.strategies.HomeStrategy;
import caterpillow.robot.agents.strategies.TraverseStrategy;
import caterpillow.robot.agents.strategies.soldier.BuildTowerStrategy;
import caterpillow.util.Pair;

import java.util.ArrayList;
import java.util.Collections;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class SnipeAndBuildStrategy extends Strategy {

    Agent bot;
    MapLocation target;

    // REMEMBER WE TRY TARGETS FROM THE BACK
    ArrayList<MapLocation> todo;

    Strategy primary;
    Strategy secondary;

    public SnipeAndBuildStrategy() {
        bot = (Agent) Game.bot;
        target = null;
        todo = new ArrayList<>();

        todo.add(flipHor(bot.home));
        todo.add(rot180(bot.home));
        todo.add(flipVer(bot.home));

        // if vertical flip is less likely than horizontal flip, reverse the array
        Pair<Double, Double> dists = relativeDistsToCentre(bot.home);
        if (dists.first > dists.second) {
            Collections.reverse(todo);
        }

        // starting strategy
        target = todo.getLast();
        todo.removeLast();
        primary = new TraverseStrategy(target, VISION_RAD);
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return primary instanceof HomeStrategy && primary.isComplete();
    }

    @Override
    public void runTick() throws GameActionException {
        rc.setIndicatorString("SNIPE AND BUILD");
        // go home if its run out of things to do (which is unlikely since itll probably die first)
        if (primary instanceof ShitEverywhereStrategy || primary instanceof BuildTowerStrategy) {
            primary.runTick();
            return;
        }

        if (secondary == null) {
            if (rc.canSenseLocation(target)) {
                RobotInfo info = rc.senseRobotAtLocation(target);
                if (info != null && !isFriendly(info) && info.getType().isTowerType()) {
                    // found target
                    secondary = new AttackTowerStrategy(info.getLocation());
                    secondary.runTick();
                    return;
                }
            }
        }

        if (secondary != null) {
            if (secondary.isComplete()) {
                println("\ntime to build a tower!\n");
                secondary = null;
                primary = new BuildTowerStrategy(target, UnitType.LEVEL_ONE_MONEY_TOWER);
                runTick();
            } else {
                secondary.runTick();
            }
            return;
        }

        if (primary.isComplete()) {
            if (todo.isEmpty()) {
                target = null;
                primary = new ShitEverywhereStrategy();
                runTick();
                return;
            }
            target = todo.getLast();
            todo.removeLast();
            primary = new TraverseStrategy(target, VISION_RAD);
        }
        primary.runTick();
    }
}
