package caterpillow.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.tracking.TowerTracker;

import java.util.ArrayList;

import static caterpillow.Game.*;
import static caterpillow.util.Util.*;
import static java.lang.Math.max;

/*

will go towards target location, trying to refill from there
will refill as much as possible, or deem not worth

*/
public class StrongRefillStrategy extends Strategy {

    MapLocation target;
    Agent bot;
    double minPaintCapacity;
    boolean hasRefilled;
    ArrayList<MapLocation> skipped;

    int minPaintCapacity() {
        return (int) ((double) rc.getType().paintCapacity * minPaintCapacity);
    }

    public StrongRefillStrategy(double minPaintCapacity) {
        println("tryna refill");
        bot = (Agent) Game.bot;
        this.minPaintCapacity = minPaintCapacity;
        hasRefilled = false;
        skipped = new ArrayList<>();
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (rc.getPaint() >= minPaintCapacity()) {
            println("enough paint");
            return true;
        }

        target = TowerTracker.getNearestFriendlyPaintTowerGlobal(c -> !skipped.contains(c));

        RobotInfo pot = TowerTracker.getNearestVisibleTower(c -> {
            if (isFriendly(c) && (c.getPaintAmount() > minPaintCapacity() - rc.getPaint() || c.getPaintAmount() > 69) && !skipped.contains(c.getLocation())) {
                return true;
            }
            return false;
        });

        if (pot != null && target != null && 2 * Math.sqrt(pot.getLocation().distanceSquaredTo(rc.getLocation())) < Math.sqrt(target.distanceSquaredTo(rc.getLocation()))) {
            target = pot.getLocation();
        }
        if (target == null) {
            target = TowerTracker.getNearestFriendlyNonPaintTowerGlobal(c -> !skipped.contains(c));
            if (target == null) {
                // ff
                println("wrap it up bud");
                return true;
            }
        }
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("STRONG REFILL");
        bot.pathfinder.makeMove(target);
        if (rc.canSenseLocation(target) && rc.senseRobotAtLocation(target) != null && rc.canTransferPaint(target, -1)) {
            bot.refill(rc.senseRobotAtLocation(target));
            println("added target to skipped " + target);
            skipped.add(target);
        }
        rc.setIndicatorLine(rc.getLocation(), target, 0, 255, 0);
    }
}
