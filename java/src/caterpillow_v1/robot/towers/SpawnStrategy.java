package caterpillow_v1.robot.towers;

import battlecode.common.*;
import caterpillow_v1.Game;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.world.GameStage;

import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;

public class SpawnStrategy extends TowerStrategy {

    // in case we get rushed
    Tower bot;
    int minPaint;

    public SpawnStrategy() {
        bot = (Tower) Game.bot;
    }

    @Override
    public void runTick() throws GameActionException {
        if (isStarter && gameStage.equals(GameStage.EARLY)) {
            minPaint = 0;
        } else {
            minPaint = 100;
        }

        switch (gameStage) {
            // TODO: implement strategies for all game times
            // currently just spam soldiers
            case LATE:
            case MID:
            case EARLY:
                // build soldiers
                if (rc.getPaint() - UnitType.SOLDIER.paintCost >= minPaint) {
                    // this might be computationally expensive
                    // try to spawn as close as possible to the centre
//                    MapInfo spawn = getClosestCellTo(centre, cell -> connectedByPaint(cell.getMapLocation(), rc.getLocation(), true) && rc.canBuildRobot(UnitType.SOLDIER, cell.getMapLocation()));
//                    if (spawn != null) {
//                        bot.build(UnitType.SOLDIER, spawn.getMapLocation());
//                        return;
//                    }
                    // just spawn adjacent
                    MapInfo spawn = getClosestNeighbourTo(rc.getLocation(), cell -> cell.getMapLocation().distanceSquaredTo(rc.getLocation()) == 1 && !cell.getPaint().isEnemy() && rc.canBuildRobot(UnitType.SOLDIER, cell.getMapLocation()));
                    if (spawn != null) {
                        bot.build(UnitType.SOLDIER, spawn.getMapLocation());
                        return;
                    }
                }
                break;
        }
    }
}
