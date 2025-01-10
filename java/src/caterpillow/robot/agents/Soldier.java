package caterpillow.robot.agents;

import battlecode.common.*;
import caterpillow.packet.packets.AdoptionPacket;
import caterpillow.packet.packets.TestPacket;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.agents.strategies.WanderStrategy;

import static caterpillow.Util.*;
import static caterpillow.Game.*;

public class Soldier extends Agent {

    @Override
    public void init() throws GameActionException {
        super.init();
        // by default, make sure u can talk to ur tower
        MapInfo info = rc.senseMapInfo(rc.getLocation());
        if (!info.getPaint().isAlly()) {
            assert rc.canAttack(rc.getLocation());
            // TODO: make this match the pattern to build towers
            rc.attack(rc.getLocation(), false);
        }

        RobotInfo nearest = getNearest(bot -> bot.getType().isTowerType());
        assert nearest != null;
        assert rc.senseMapInfo(rc.getLocation()).getPaint().isAlly();
        pm.send(nearest.getLocation(), new AdoptionPacket(rc.getID()));
        pathfinder = new BugnavPathfinder();

        primaryStrategy = new WanderStrategy();
    }

//    @Override
//    public void runTick() throws GameActionException {
//        super.runTick();
//        if (!rc.isActionReady()) return;
//
//        MapInfo[] nearbyTiles = rc.senseNearbyMapInfos();
//
//        // build if u can
//        for (MapInfo patternTile : nearbyTiles) {
//            if (patternTile.getMark() != patternTile.getPaint() && patternTile.getMark() != PaintType.EMPTY){
//                boolean useSecondaryColor = patternTile.getMark() == PaintType.ALLY_SECONDARY;
//                if (rc.canAttack(patternTile.getMapLocation())) {
//                    rc.attack(patternTile.getMapLocation(), useSecondaryColor);
//                    return;
//                }
//            }
//        }
//
//        // move if u can
//        for (MapInfo patternTile : nearbyTiles) {
//            if (patternTile.getMark() != patternTile.getPaint() && patternTile.getMark() != PaintType.EMPTY){
//                Direction dir = pathfinder.getMove(patternTile.getMapLocation());
//                if (dir != null)
//                    rc.move(dir);
//                return;
//            }
//        }
//
//        // search for ruins
//        MapInfo curRuin = null;
//        for (MapInfo tile : nearbyTiles) {
//            if (tile.hasRuin()){
//                curRuin = tile;
//            }
//        }
//
//        // found some random ass ruin
//        if (curRuin != null) {
//            // complete tower if u can
//            MapLocation targetLoc = curRuin.getMapLocation();
//            if (rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc)) {
//                rc.completeTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc);
//                rc.setTimelineMarker("Tower built", 0, 255, 0);
//                System.out.println("Built a tower at " + targetLoc + "!");
//                return;
//            }
//
//            if (rc.senseMapInfo(curRuin.getMapLocation()).getMark() == PaintType.EMPTY && rc.canMarkTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc)) {
//                rc.markTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc);
//                System.out.println("Trying to build a tower at " + targetLoc);
//                return;
//            }
//        }
//
//        // do jack shit
//        if (rng.nextInt(2) == 1) {
//            MapLocation goal = null;
//            for (int x = 0; x < rc.getMapWidth(); x++) {
//                for (int y = 0; y < rc.getMapHeight(); y++) {
//                    if (goal == null || rc.getLocation().distanceSquaredTo(new MapLocation(x, y)) > rc.getLocation().distanceSquaredTo(goal)) {
//                        goal = new MapLocation(x, y);
//                    }
//                }
//            }
//            Direction dir = pathfinder.getMove(goal);
//            if (dir != null)
//                rc.move(dir);
//        } else {
//            System.out.println("building\n");
//            MapInfo currentTile = rc.senseMapInfo(rc.getLocation());
//            if (!currentTile.getPaint().isAlly() && rc.canAttack(rc.getLocation())){
//                rc.attack(rc.getLocation());
//            }
//        }
//    }
}
