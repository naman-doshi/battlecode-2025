package caterpillow.robot.towers;

import battlecode.common.*;
import caterpillow.Game;
import static caterpillow.Game.centre;
import static caterpillow.Game.rc;

import caterpillow.tracking.CellTracker;
import caterpillow.tracking.RobotTracker;

import static caterpillow.tracking.RobotTracker.getNearestRobot;
import static caterpillow.util.Util.getClosestNeighbourTo;
import static caterpillow.util.Util.isFriendly;

public class NotDefenceTowerDefenceStrategy extends TowerStrategy {

    Tower bot;
    int moppersSpawned = 0;

    public NotDefenceTowerDefenceStrategy() {
        bot = (Tower) Game.bot;
    }

    @Override
    public void runTick() throws GameActionException {
        boolean enableNewMethod = false;
        if (enableNewMethod) {
            if (Game.time <= 3) return;
            if (RobotTracker.countNearbyBots(c -> isFriendly(c) && c.type == UnitType.MOPPER) == 0) {
                RobotInfo inc = getNearestRobot(b -> b.getType().isRobotType() && !isFriendly(b));
                if (inc != null) {
                    MapLocation loc = getClosestNeighbourTo(inc.getLocation(), c -> true).getMapLocation();
                    if (rc.canBuildRobot(UnitType.MOPPER, loc)) {
                        bot.build(UnitType.MOPPER, loc);
                    }
                } else {
                    MapInfo nearest = CellTracker.getNearestCell(c -> !c.getPaint().isEnemy());
                    if (nearest != null) {
                        MapLocation loc = getClosestNeighbourTo(nearest.getMapLocation(), c -> true).getMapLocation();
                        if (rc.canBuildRobot(UnitType.MOPPER, loc)) {
                            bot.build(UnitType.MOPPER, loc);
                        }
                    }
                }
            }
        } else {
            if (Game.time <= 3) return;
            if (RobotTracker.countNearbyBots(c -> isFriendly(c) && c.type == UnitType.MOPPER) == 0) {
                MapInfo nearest = getClosestNeighbourTo(centre, c -> !c.getPaint().isEnemy());
                if (nearest == null && moppersSpawned < 2) {
                    MapLocation loc = getClosestNeighbourTo(centre, c -> true).getMapLocation();
                    if (rc.canBuildRobot(UnitType.MOPPER, loc)) {
                        bot.build(UnitType.MOPPER, loc);
                        moppersSpawned++;
                    }
                }
            }
        }
    }
}
