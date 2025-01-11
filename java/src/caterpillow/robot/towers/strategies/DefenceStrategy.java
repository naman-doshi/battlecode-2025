package caterpillow.robot.towers.strategies;

import battlecode.common.*;
import caterpillow.robot.Strategy;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class DefenceStrategy extends Strategy {

    private boolean isInDanger() throws GameActionException {
        return getNearestRobot(bot -> !isFriendly(bot)) != null;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return !isInDanger();
    }

    @Override
    public void runTick() throws GameActionException {
        RobotInfo nearest = getNearestRobot(bot -> !isFriendly(bot));
        assert nearest != null;
        int enemyCount = countNearbyBots(bot -> !isFriendly(bot));
        int friendlyCount = countNearbyBots(bot -> bot.getType().equals(UnitType.MOPPER) && isFriendly(bot));
        // dont want to spawn too many moppers
        // limit moppers <= aggressors
        if (friendlyCount < enemyCount) {
            MapInfo spawn = getClosestNeighbourTo(nearest.getLocation(), cell -> rc.canBuildRobot(UnitType.MOPPER, cell.getMapLocation()));
            if (spawn != null) {
                rc.buildRobot(UnitType.MOPPER, spawn.getMapLocation());
            }
        }
    }
}
