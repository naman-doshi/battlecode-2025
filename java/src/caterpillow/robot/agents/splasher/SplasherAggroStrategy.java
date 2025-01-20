package caterpillow.robot.agents.splasher;

import java.util.List;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.*;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.robot.agents.roaming.StrongAggroRoamStrategy;
import static caterpillow.tracking.RobotTracker.getNearestRobot;
import caterpillow.util.GameSupplier;
import static caterpillow.util.Util.*;

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
        roamStrategy = new StrongAggroRoamStrategy(); // test\
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

        MapLocation target = bot.bestAttackLocation();
        if (target != null) {
            if(rc.canAttack(target)) {
                rc.attack(target);
                // move towards next target
                target = bot.bestAttackLocation();
                if(target != null) bot.pathfinder.makeMove(target);
            } else {
                bot.pathfinder.makeMove(target);
                if(rc.canAttack(target)) rc.attack(target);
            }
        }
        roamStrategy.runTick();
    }
}
