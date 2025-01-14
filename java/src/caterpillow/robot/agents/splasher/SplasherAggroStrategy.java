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
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.robot.agents.roaming.StrongAggroRoamStrategy;
import caterpillow.util.GameSupplier;
import static caterpillow.util.Util.getNearestRobot;
import static caterpillow.util.Util.indicate;
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
    MapLocation lastSeenTower;

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

        if (isPaintBelowHalf()) {
            nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getPaintAmount() >= missingPaint());
            if (nearest != null) {
                bot.secondaryStrategy = new WeakRefillStrategy(nearest.getLocation(), 0.2);
                bot.runTick();
                return;
            } else if (rc.getPaint() < 50) {
                indicate("retreating with paint " + rc.getPaint() + " and bytecode " + Clock.getBytecodeNum());
                bot.pathfinder.makeMove(Game.origin);
                return;
            }
        } 

        MapLocation target = bot.bestAttackLocation();
        if (target != null) {
            indicate("attacking "+target);
            
            if (rc.canAttack(target)) {
                rc.attack(target);
            }

            bot.pathfinder.makeMove(target);
            
        } else {
            roamStrategy.runTick();
            indicate("roaming");
        }
    }
}
