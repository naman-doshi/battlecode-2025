package caterpillow.robot.towers;

import battlecode.common.*;

import static caterpillow.Util.rng;

public class ChipTower extends Tower {
    @Override
    public void init(RobotController rc) {
        super.init(rc);
        types = new battlecode.common.UnitType[] {UnitType.LEVEL_ONE_MONEY_TOWER, UnitType.LEVEL_TWO_MONEY_TOWER, UnitType.LEVEL_THREE_MONEY_TOWER};
    }

    @Override
    public void runTick(RobotController rc) throws GameActionException {
        // Pick a direction to build in.
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation nextLoc = rc.getLocation().add(dir);
        // Pick a random robot type to build.
        int robotType = rng.nextInt(3);
        if (robotType == 0 && rc.canBuildRobot(UnitType.SOLDIER, nextLoc)){
            rc.buildRobot(UnitType.SOLDIER, nextLoc);
            System.out.println("BUILT A SOLDIER");
        }
        else if (robotType == 1 && rc.canBuildRobot(UnitType.MOPPER, nextLoc)){
//            rc.buildRobot(UnitType.MOPPER, nextLoc);
//            System.out.println("BUILT A MOPPER");
        }
        else if (robotType == 2 && rc.canBuildRobot(UnitType.SPLASHER, nextLoc)){
            // rc.buildRobot(UnitType.SPLASHER, nextLoc);
            // System.out.println("BUILT A SPLASHER");
            rc.setIndicatorString("SPLASHER NOT IMPLEMENTED YET");
        }

        // Read incoming messages
        Message[] messages = rc.readMessages(-1);
        for (Message m : messages) {
            System.out.println("Tower received message: '#" + m.getSenderID() + " " + m.getBytes());
        }
    }
}
