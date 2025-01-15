package caterpillow_v1.robot.agents.soldier;

import battlecode.common.*;
import caterpillow_v1.Game;
import caterpillow_v1.robot.Strategy;

import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;

public class TrollRuinStrategy extends Strategy {

    // hella hacky solution
    public boolean didSkip;

    Soldier bot;
    MapLocation target;

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
        return getNearestCell(c -> isCellInTowerBounds(target, c.getMapLocation()) && isPaintable(c));
    }

    public TrollRuinStrategy(MapLocation target) {
        bot = (Soldier) Game.bot;
        this.target = target;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (rc.canSenseLocation(target) && rc.senseRobotAtLocation(target) != null) return true;
        if (hasBeenTrolled()) return true;
        if (!isInView()) return false;
        MapInfo cell = getPlaceCell();
        if (cell == null) return true;
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("TROLLING");
        if (!isInView()) {
            bot.pathfinder.makeMove(target);
        } else {
            MapInfo cell = getPlaceCell();
            assert cell != null : "how does this happen";
            bot.pathfinder.makeMove(cell.getMapLocation());
            if (rc.canAttack(cell.getMapLocation())) {
                bot.checkerboardAttack(cell.getMapLocation());
            }
        }
    }
}
