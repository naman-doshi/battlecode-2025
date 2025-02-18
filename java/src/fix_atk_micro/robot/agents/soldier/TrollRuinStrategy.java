package fix_atk_micro.robot.agents.soldier;

import battlecode.common.*;
import fix_atk_micro.Game;
import fix_atk_micro.robot.Strategy;
import fix_atk_micro.tracking.CellTracker;

import static fix_atk_micro.util.Util.*;
import static fix_atk_micro.Game.*;

public class TrollRuinStrategy extends Strategy {

    // hella hacky solution
    public boolean didSkip;

    Soldier bot;
    MapLocation target;
    boolean done = false;

    boolean isInView() {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (!rc.canSenseLocation(new MapLocation(target.x + dx, target.y + dy))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean hasBeenTrolled() throws GameActionException {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                MapLocation loc = target.translate(dx, dy);
                if (rc.canSenseLocation(loc) && rc.senseMapInfo(loc).getPaint().isAlly()) {
                    return true;
                }
            }
        }
        return false;
    }

    MapInfo getPlaceCell() throws GameActionException {
        return CellTracker.getNearestCell(c -> isCellInTowerBounds(target, c.getMapLocation()) && isPaintable(c));
    }

    public TrollRuinStrategy(MapLocation target) {
        bot = (Soldier) Game.bot;
        this.target = target;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (done == true) return true;
        if (rc.canSenseLocation(target) && rc.senseRobotAtLocation(target) != null) return true;
        if (hasBeenTrolled()) return true;
        if (!isInView()) return false;
        MapInfo cell = getPlaceCell();
        if (cell == null) return true;
        return false;
    }


    // cbb check if the ruin has already been trolled, might as well troll it twice to make it harder for the enemy
    // it makes bots much slower to scout bc they have to get up really close to check if it's alr been trolled
    @Override
    public void runTick() throws GameActionException {
        indicate("TROLLING RUIN AT " + target);
        bot.pathfinder.makeMove(target);
        MapInfo cell = getPlaceCell();
        if (cell == null) return;
        if (rc.canAttack(cell.getMapLocation())) {
            bot.checkerboardAttack(cell.getMapLocation());
            done = true;
        }
    }
}
