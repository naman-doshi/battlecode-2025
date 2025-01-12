package caterpillow.robot.agents.soldier;

import battlecode.common.*;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.util.Pair;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

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

    MapLocation getPlaceCell() throws GameActionException {
        return getNearestCell(c -> isCellInTowerBounds(target, c.getMapLocation()) && !c.getPaint().isEnemy()).getMapLocation();
    }

    public TrollRuinStrategy(MapLocation target) {
        bot = (Soldier) Game.bot;
        this.target = target;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return isInView() && getNearestCell(c -> isCellInTowerBounds(target, c.getMapLocation()) && c.getPaint().isAlly()) != null;
    }

    @Override
    public void runTick() throws GameActionException {
        rc.setIndicatorString("TROLLING");
        if (!isInView()) {
            rc.move(bot.pathfinder.getMove(target));
        } else {
            MapLocation cell = getPlaceCell();
            rc.move(bot.pathfinder.getMove(cell));
            if (rc.canAttack(cell)) {
                bot.checkerboardAttack(cell);
            }
        }
    }
}
