package caterpillow.robot.towers;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.UnitType;
import caterpillow.Game;
import static caterpillow.Game.*;
import caterpillow.tracking.RobotTracker;
import static caterpillow.util.Util.getClosestNeighbourTo;

public class UnstuckStrategy extends TowerStrategy {

    Tower bot;
    int moppersSpawned = 0;

    public UnstuckStrategy() {
        bot = (Tower) Game.bot;
    }

    @Override
    public void runTick() throws GameActionException {
        if(time <= 3) return;
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
