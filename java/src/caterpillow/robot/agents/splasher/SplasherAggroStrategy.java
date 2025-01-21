package caterpillow.robot.agents.splasher;

import java.util.List;

import battlecode.common.*;
import caterpillow.robot.agents.roaming.AggroRoamStrategy;
import caterpillow.Game;
import static caterpillow.Game.*;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.WeakRefillStrategy;

import static caterpillow.tracking.RobotTracker.getNearestRobot;
import caterpillow.util.GameSupplier;
import static caterpillow.util.Util.*;
import caterpillow.util.Pair;

public class SplasherAggroStrategy extends Strategy {

    public Splasher bot;

    // enemyLocs is more like "POI locs"
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;
    public List<GameSupplier<MapInfo>> suppliers;
    MapLocation lastSeenTower;

    Strategy refillStrategy;
    Strategy roamStrategy;

    public SplasherAggroStrategy() throws GameActionException {
        bot = (Splasher) Game.bot;
        //assert (Game.origin != null) : "origin is null";
        roamStrategy = new AggroRoamStrategy(); // test\
        lastSeenTower = origin;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {

        // don't delete this, i want to test whether it should retreat to lastSeenTower or Game.origin on actual scrims
        RobotInfo nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType());
        if (nearest != null) {
            lastSeenTower = nearest.getLocation();
        }

        if (refillStrategy == null && getPaintLevel() < 0.3) {
            refillStrategy = new WeakRefillStrategy(0.4);
        }
        if (tryStrategy(refillStrategy)) return;
        refillStrategy = null;

        Pair<MapLocation, Boolean> res = bot.bestAttackLocation();
        MapLocation target = res.first;
        boolean paintType = res.second;
        if (target != null) {
            if(rc.canAttack(target)) {
                rc.attack(target, paintType);
                // move towards next target (too bytecode expensive)
                // target = bot.bestAttackLocation().first;
                // if(target != null) bot.pathfinder.makeMove(target);
            } else {
                bot.pathfinder.makeLastMove(bot.pathfinder.getMove(target));
                if(rc.canAttack(target)) rc.attack(target, paintType);
            }
        }
        roamStrategy.runTick();
    }
}
