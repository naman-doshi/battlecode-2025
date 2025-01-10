package bugnav.robot.agents;

import battlecode.common.*;
import bugnav.pathfinding.BugnavPathfinder;

import static bugnav.Util.rng;

public class Soldier extends Agent {
    @Override
    public void init(RobotController rc) {
        super.init(rc);
    }

    public MapLocation target = null;

    @Override
    public void runTick() throws GameActionException {
        if (!rc.isActionReady()) return;
        if(target == null) {
            target = new MapLocation(rng.nextInt(rc.getMapWidth()), rng.nextInt(rc.getMapHeight()));
        }
        rc.move(pathfinder.getMove(target));
        rc.setIndicatorString(((BugnavPathfinder)pathfinder).stackSize + "");
        rc.setIndicatorLine(rc.getLocation(), target, 0, 255, 0);
    }
}
