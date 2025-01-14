package caterpillow_v1.robot.agents.braindamage;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow_v1.Game;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.robot.agents.Agent;
import caterpillow_v1.robot.agents.soldier.AttackTowerStrategy;
import caterpillow_v1.robot.agents.HomeStrategy;
import caterpillow_v1.robot.agents.TraverseStrategy;
import caterpillow_v1.robot.agents.soldier.BuildTowerStrategy;
import caterpillow_v1.util.Pair;

import java.util.ArrayList;
import java.util.Collections;

import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;

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
        return primary instanceof BuildTowerStrategy && primary.isComplete();
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("SNIPE AND BUILD");
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
