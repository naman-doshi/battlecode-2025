package caterpillow.pathfinding;

import battlecode.common.*;
import static caterpillow.Game.*;
import caterpillow.util.GamePredicate;
import static caterpillow.tracking.CellTracker.*;
import caterpillow.util.Profiler;

class StrictBugnavPathfinder extends BugnavPathfinder {
    @Override
    public void emergencyMove(MapLocation to) throws GameActionException {
        GamePredicate<MapInfo> opred = avoid;
        avoid = m -> false;
        MapInfo closestGood = getNearestCell(c -> !opred.test(c));
        if(closestGood != null) to = closestGood.getMapLocation();
        Direction dir = getMove(to);
        if (dir != null && rc.canMove(dir)) {
            makeMove(dir);
        }
        avoid = opred;
    }
}
