package fix_atk_micro.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import fix_atk_micro.Game;
import fix_atk_micro.robot.Strategy;
import fix_atk_micro.tracking.CellTracker;
import fix_atk_micro.tracking.TowerTracker;

import java.util.ArrayList;

import static fix_atk_micro.Game.*;
import static fix_atk_micro.util.Util.*;
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

    public boolean success;

    int minPaintCapacity() {
        return (int) ((double) rc.getType().paintCapacity * minPaintCapacity);
    }

    public StrongRefillStrategy(double minPaintCapacity) {
        println("tryna refill");
        bot = (Agent) Game.bot;
        this.minPaintCapacity = minPaintCapacity;
        hasRefilled = false;
        skipped = new ArrayList<>();
        success = false;
    }


    @Override
    public boolean isComplete() throws GameActionException {
        if (rc.getPaint() >= minPaintCapacity()) {
            println("enough paint");
            success = true;
            return true;
        }

        while (true) {
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
                    success = false;
                    return true;
                }
            }

            if (rc.canSenseLocation(target)) {
                RobotInfo info = rc.senseRobotAtLocation(target);
                assert info != null;
                if (info.getPaintAmount() < 10) {
                    skipped.add(target);
                    continue;
                }
            }
            break;
        }
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("STRONG REFILL");
        bot.pathfinder.makeMove(target);
        if (rc.canSenseLocation(target) && rc.senseRobotAtLocation(target) != null) {
            if (rc.canTransferPaint(target, -1)) {
                bot.refill(rc.senseRobotAtLocation(target));
                println("added target to skipped " + target);
                skipped.add(target);
            }
        }
        rc.setIndicatorLine(rc.getLocation(), target, 0, 255, 0);
    }
}
