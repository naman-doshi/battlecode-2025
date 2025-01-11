package lmkaepillow.robot.towers;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
public class PaintTower extends Tower {

    public int spawned;
    @Override
    public void init(RobotController rc) {
        super.init(rc);
        types = new battlecode.common.UnitType[] {UnitType.LEVEL_ONE_PAINT_TOWER, UnitType.LEVEL_TWO_PAINT_TOWER, UnitType.LEVEL_THREE_PAINT_TOWER};
        spawned = 0;
    }
    
    
    @Override
    public void runTick(RobotController rc) throws GameActionException {
        // Pick a direction to build in.
        Direction dir = directions[0];
        MapLocation nextLoc = rc.getLocation().add(dir);
        // Pick a random robot type to build.
        //int robotType = rng.nextInt(3);
        
        if ((spawned < 2 || spawned % 2 == 0) && rc.canBuildRobot(UnitType.SOLDIER, nextLoc)){
            rc.buildRobot(UnitType.SOLDIER, nextLoc);
            System.out.println("BUILT A SOLDIER");
            spawned++;
        } else if (spawned % 2 == 1 && rc.canBuildRobot(UnitType.SPLASHER, nextLoc)){
            rc.buildRobot(UnitType.SPLASHER, nextLoc);
            System.out.println("BUILT A SPLASHER");
            spawned++;
        }

        // do aoe attack
        if (rc.canAttack(null)) rc.attack(null);

        // do single target attack on bots (soldier > splasher > mopper)
        // take the lowest hp of each type first
        RobotInfo[] nearbyRobots = rc.senseNearbyRobots();
        RobotInfo target = null;
        for (RobotInfo robot : nearbyRobots) {
            if (robot.getTeam() != rc.getTeam()) {
                if (target == null) {
                    target = robot;
                } else {
                    if (robot.getType() == UnitType.SOLDIER && (target.getType() != UnitType.SOLDIER || (target.getType() == UnitType.SOLDIER && robot.health < target.health))) {
                        target = robot;
                    } else if (robot.getType() == UnitType.SPLASHER && target.getType() != UnitType.SOLDIER && robot.health < target.health) {
                        target = robot;
                    } else if (robot.getType() == UnitType.MOPPER && target.getType() != UnitType.SOLDIER && target.getType() != UnitType.SPLASHER && robot.health < target.health) {
                        target = robot;
                    }
                }
            }
        }
        if (target != null && rc.canAttack(target.getLocation())) rc.attack(target.getLocation());



//         else if (robotType == 1 && rc.canBuildRobot(UnitType.MOPPER, nextLoc)){
// //            rc.buildRobot(UnitType.MOPPER, nextLoc);
// //            System.out.println("BUILT A MOPPER");
//         }
//         else if (robotType == 2 && rc.canBuildRobot(UnitType.SPLASHER, nextLoc)){
//             // rc.buildRobot(UnitType.SPLASHER, nextLoc);
//             // System.out.println("BUILT A SPLASHER");
//             rc.setIndicatorString("SPLASHER NOT IMPLEMENTED YET");
//         }

        // Read incoming messages
        Message[] messages = rc.readMessages(-1);
        for (Message m : messages) {
            System.out.println("Tower received message: '#" + m.getSenderID() + " " + m.getBytes());
        }
    }
}
