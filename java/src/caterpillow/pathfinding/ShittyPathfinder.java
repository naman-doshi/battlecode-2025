package caterpillow.pathfinding;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import static caterpillow.Util.*;
import static caterpillow.Game.*;

public class ShittyPathfinder extends AbstractPathfinder {

    public ShittyPathfinder(RobotController rc) {
        super(rc);
        System.out.println("poop");
    }

    @Override
    public Direction getMove(MapLocation to, boolean avoidFriendly, RobotController rc) {
        assert !avoidFriendly : "i havent implemented this";
        assert rc.isActionReady() : "bro u cant even move ???";
        Direction best = null;
        for (Direction dir : Direction.values()) {
            if (!rc.canMove(dir)) continue;
            MapLocation new_pos = pos.add(dir);
            if (best == null || new_pos.distanceSquaredTo(to) < pos.add(best).distanceSquaredTo(to)) {
                best = dir;
            }
        }
        if (best == null) return null;
        pos = pos.add(best);
        return best;
    }
}
