package caterpillow.robot.agents.splasher;

import java.util.List;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.StrongRefillStrategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.robot.agents.roaming.StrongAggroRoamStrategy;
import caterpillow.util.GameSupplier;
import static caterpillow.tracking.RobotTracker.getNearestRobot;
import static caterpillow.util.Util.*;
import static caterpillow.util.Util.getPaintLevel;

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
        lastSeenTower = Game.origin;
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

        if (refillStrategy == null && getPaintLevel() < 0.8) {
            if (getPaintLevel() < 0.5) {
                refillStrategy = new StrongRefillStrategy(0.8);
            } else {
                refillStrategy = new WeakRefillStrategy(0.2);
            }
        }
        if (tryStrategy(refillStrategy)) return;
        refillStrategy = null;

        MapLocation target = bot.bestAttackLocation();
        if (target != null) {
            indicate("attacking "+target);
            
            if (rc.canAttack(target)) {
                rc.attack(target, true);
            }

            bot.pathfinder.makeMove(target);
            
        } else {
            roamStrategy.runTick();
            // indicate("roaming");
        }
    }
}
