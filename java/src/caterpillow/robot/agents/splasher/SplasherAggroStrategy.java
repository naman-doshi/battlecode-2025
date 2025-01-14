package caterpillow.robot.agents.splasher;

import java.util.List;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.robot.agents.roaming.StrongAggroRoamStrategy;
import caterpillow.util.GameSupplier;
import static caterpillow.util.Util.getNearestRobot;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.isPaintBelowHalf;
import static caterpillow.util.Util.missingPaint;

public class SplasherAggroStrategy extends Strategy {

    public Splasher bot;

    // enemyLocs is more like "POI locs"
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;
    public List<GameSupplier<MapInfo>> suppliers;
    WeakRefillStrategy refillStrategy;

    Strategy roamStrategy;

    public SplasherAggroStrategy() throws GameActionException {
        bot = (Splasher) Game.bot;
        //assert (Game.origin != null) : "origin is null";
        roamStrategy = new StrongAggroRoamStrategy(); // test
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {

        if (isPaintBelowHalf()) {
            RobotInfo nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getPaintAmount() >= missingPaint());
            if (nearest != null) {
                bot.secondaryStrategy = new WeakRefillStrategy(nearest.getLocation(), 0.4);
                bot.runTick();
                return;
            }
        }

        MapLocation target = bot.bestAttackLocation();
        if (target != null) {
            
            if (rc.canAttack(target)) {
                rc.attack(target);
            }

            // target would only be a ruin if it's an enemy tower
            // BUT we dont want to approach it otherwise it'll attack us
            if (rc.canSenseLocation(target) && !rc.senseMapInfo(target).hasRuin())  {
                bot.pathfinder.makeMove(target);
            }
            
        } else {
            roamStrategy.runTick();
        }
    }
}
