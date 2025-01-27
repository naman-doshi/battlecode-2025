package fix_atk_micro.robot.towers;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.UnitType;
import fix_atk_micro.Game;
import static fix_atk_micro.Game.centre;
import static fix_atk_micro.Game.rc;
import fix_atk_micro.tracking.RobotTracker;
import static fix_atk_micro.util.Util.getClosestNeighbourTo;

public class UnstuckStrategy extends TowerStrategy {

    Tower bot;
    int moppersSpawned = 0;

    public UnstuckStrategy() {
        bot = (Tower) Game.bot;
    }

    @Override
    public void runTick() throws GameActionException {
        if (RobotTracker.countNearbyFriendly(c -> c.type == UnitType.MOPPER) == 0) {
            MapInfo nearest = getClosestNeighbourTo(centre, c -> !c.getPaint().isEnemy() && c.getMapLocation().distanceSquaredTo(rc.getLocation()) == 1);
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
