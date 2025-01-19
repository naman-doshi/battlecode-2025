package caterpillow.robot.towers;

import battlecode.common.*;
import caterpillow.tracking.RobotTracker;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class DefenceStrategy extends TowerStrategy {

    private boolean isInDanger() throws GameActionException {
        return RobotTracker.getNearestRobot(bot -> !isFriendly(bot)) != null;
    }

    @Override
    public void runTick() throws GameActionException {
        if (!isInDanger()) {
            return;
        }
        RobotInfo nearest = RobotTracker.getNearestRobot(bot -> !isFriendly(bot));
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
        RobotInfo info = RobotTracker.getBestRobot((a, b) -> {
            boolean a1 = a.getType().equals(UnitType.SOLDIER);
            boolean b1 = b.getType().equals(UnitType.SOLDIER);
            int h1 = a.getHealth();
            int h2 = b.getHealth();
            if (a1 == b1) {
                if (h1 > h2) return b;
                else return a;
            } else {
                if (a1) return a;
                else return b;
            }
        }, e -> !isFriendly(e) && e.getType().isRobotType() && rc.canAttack(e.getLocation()));
        if (info != null) {
            rc.attack(info.getLocation());
        }
    }
}
