package caterpillow.robot.agents;

import battlecode.common.*;
import caterpillow.pathfinding.ShittyPathfinder;

import static caterpillow.Util.*;
import static caterpillow.Game.*;

public class Soldier extends Agent {

    @Override
    public void init() {
        pathfinder = new ShittyPathfinder(rc);
    }

    @Override
    public void runTick() throws GameActionException {
        if (!rc.isActionReady()) return;

        MapInfo[] nearbyTiles = rc.senseNearbyMapInfos();

        // build if u can
        for (MapInfo patternTile : nearbyTiles) {
            if (patternTile.getMark() != patternTile.getPaint() && patternTile.getMark() != PaintType.EMPTY){
                boolean useSecondaryColor = patternTile.getMark() == PaintType.ALLY_SECONDARY;
                if (rc.canAttack(patternTile.getMapLocation())) {
                    rc.attack(patternTile.getMapLocation(), useSecondaryColor);
                    return;
                }
            }
        }

        // move if u can
        for (MapInfo patternTile : nearbyTiles) {
            if (patternTile.getMark() != patternTile.getPaint() && patternTile.getMark() != PaintType.EMPTY){
                Direction dir = pathfinder.getMove(patternTile.getMapLocation(), rc);
                if (dir != null)
                    rc.move(dir);
                return;
            }
        }

        // search for ruins
        MapInfo curRuin = null;
        for (MapInfo tile : nearbyTiles) {
            if (tile.hasRuin()){
                curRuin = tile;
            }
        }

        // found some random ass ruin
        if (curRuin != null) {
            // complete tower if u can
            MapLocation targetLoc = curRuin.getMapLocation();
            if (rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc)) {
                rc.completeTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc);
                rc.setTimelineMarker("Tower built", 0, 255, 0);
                System.out.println("Built a tower at " + targetLoc + "!");
                return;
            }

            if (rc.senseMapInfo(curRuin.getMapLocation()).getMark() == PaintType.EMPTY && rc.canMarkTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc)) {
                rc.markTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc);
                System.out.println("Trying to build a tower at " + targetLoc);
                return;
            }
        }

        // do jack shit
        if (rng.nextInt(2) == 1) {
            MapLocation goal = null;
            for (int x = 0; x < rc.getMapWidth(); x++) {
                for (int y = 0; y < rc.getMapHeight(); y++) {
                    if (goal == null || rc.getLocation().distanceSquaredTo(new MapLocation(x, y)) > rc.getLocation().distanceSquaredTo(goal)) {
                        goal = new MapLocation(x, y);
                    }
                }
            }
            Direction dir = pathfinder.getMove(goal, rc);
            if (dir != null)
                rc.move(dir);
        } else {
            System.out.println("building\n");
            MapInfo currentTile = rc.senseMapInfo(rc.getLocation());
            if (!currentTile.getPaint().isAlly() && rc.canAttack(rc.getLocation())){
                rc.attack(rc.getLocation());
            }
        }
    }
}
