package lmkaepillow.robot.agents;

import java.util.Arrays;
import java.util.List;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import battlecode.common.RobotController;
import battlecode.common.UnitType;
import static lmkaepillow.Game.rc;
import lmkaepillow.pathfinding.BugnavPathfinder;

public class Soldier extends Agent {

    int enemyX = 0;
    int enemyY = 0;
    boolean attackMode = true;
    boolean enemyFound = false;
    int spawnX = 0;
    int spawnY = 0;
    List<MapLocation> enemyLocs = new java.util.LinkedList<MapLocation>();
    MapLocation spawnLoc = null;

    public MapLocation reflectHor(MapLocation loc) {
        return new MapLocation(rc.getMapWidth() - 1 - loc.x, loc.y);
    }

    public MapLocation reflectVert(MapLocation loc) {
        return new MapLocation(loc.x, rc.getMapHeight() - 1 - loc.y);
    }

    public MapLocation reflectRot(MapLocation loc) {
        return new MapLocation(rc.getMapWidth() - 1 - loc.x, rc.getMapHeight() - 1 - loc.y);
    }

    @Override
    public void init(RobotController rc) {
        pathfinder = new BugnavPathfinder();
        spawnX = rc.getLocation().x;
        spawnY = rc.getLocation().y - 1;
        spawnLoc = new MapLocation(spawnX, spawnY);
        System.out.println("Spawn is at " + spawnX + ", " + spawnY);
        System.out.println("Map is " + rc.getMapWidth() + " by " + rc.getMapHeight());
        
        // is the map mirrored hor or vert?
        int dist_hormiddle = Math.abs(spawnX - rc.getMapWidth() / 2);
        int dist_vertmiddle = Math.abs(spawnY - rc.getMapHeight() / 2);
        System.out.println("Distances are " + dist_hormiddle + " and " + dist_vertmiddle);
        if (dist_hormiddle > dist_vertmiddle) {
            enemyX = rc.getMapWidth() - 1 - spawnX;
            enemyY = spawnY;
            enemyLocs.addLast(reflectHor(spawnLoc));
            // second is the rotation one.
            enemyLocs.addLast(reflectRot(spawnLoc));
            // third is the vert ref one
            enemyLocs.addLast(reflectVert(spawnLoc));
        } else if (dist_hormiddle < dist_vertmiddle) {
            // first is vert ref
            enemyX = spawnX;
            enemyY = rc.getMapHeight() - 1 - spawnY;
            enemyLocs.addLast(reflectVert(spawnLoc));
            // second is the rotation one.
            enemyLocs.addLast(reflectRot(spawnLoc));
            // third is the hor ref one
            enemyLocs.addLast(reflectHor(spawnLoc));
        } else {
            // first is hor ref
            enemyX = rc.getMapWidth() - 1 - spawnX;
            enemyY = spawnY;
            enemyLocs.addLast(reflectHor(spawnLoc));
            // second is vert ref
            enemyLocs.addLast(reflectVert(spawnLoc));
            // third is the rotation one
            enemyLocs.addLast(reflectRot(spawnLoc));
        }
        System.out.println("Enemy is at " + enemyX + ", " + enemyY);
    }

    @Override
    public void runTick(RobotController rc) throws GameActionException {

        MapLocation enemy = new MapLocation(enemyX, enemyY);
        // list of towers as unit types
        UnitType[] towers = new UnitType[] {UnitType.LEVEL_ONE_PAINT_TOWER, UnitType.LEVEL_TWO_PAINT_TOWER, UnitType.LEVEL_THREE_PAINT_TOWER, 
            UnitType.LEVEL_ONE_DEFENSE_TOWER, UnitType.LEVEL_TWO_DEFENSE_TOWER, UnitType.LEVEL_THREE_DEFENSE_TOWER,
            UnitType.LEVEL_ONE_MONEY_TOWER, UnitType.LEVEL_TWO_MONEY_TOWER, UnitType.LEVEL_THREE_MONEY_TOWER};
        
        List<UnitType> towerList = Arrays.asList(towers);

        // if enemy hasn't been found, and we can sense it but it isn't there, update the enemy loc to the next one
        if (!enemyFound && rc.canSenseLocation(enemy) && (rc.senseRobotAtLocation(enemy) == null || (rc.senseRobotAtLocation(enemy) != null && !towerList.contains(rc.senseRobotAtLocation(enemy).getType())))) {
            if (rc.senseRobotAtLocation(enemy) != null) {
                System.out.println(rc.senseRobotAtLocation(enemy).getType());
            }
            enemyLocs.removeFirst();
            enemy = enemyLocs.getFirst();
            enemyX = enemy.x;
            enemyY = enemy.y;
        }

        // if it hasn't been found and we can sense it and it is there, set enemyFound to true
        if (!enemyFound && rc.canSenseLocation(enemy) && rc.senseRobotAtLocation(enemy) != null && towerList.contains(rc.senseRobotAtLocation(enemy).getType())) {
            enemyFound = true;
        }

        // if we can attack it, do so
        if (enemyFound && rc.canAttack(enemy)) {
            rc.attack(enemy);
        }

        // if we found it and it's not there anymore (we killed it), go out of attack mode
        if (enemyFound && rc.canSenseLocation(enemy) && rc.senseRobotAtLocation(enemy) == null) {
            attackMode = false;
        }

        // if  attack mode is on, move towards it
        if (attackMode) {
            Direction dir = pathfinder.getMove(enemy);
            // if ur next to tower, dont move (using distancesquared)
            if (dir != null && rc.canMove(dir) && rc.getLocation().distanceSquaredTo(enemy) > 1) {
                MapLocation nextLoc = rc.getLocation().add(dir);
                // if (rc.canAttack(nextLoc)) {
                //     rc.attack(nextLoc);
                // }
                rc.move(dir);
            }
        }

        // if attack mode is off, just be a chill guy and paint
        if (!attackMode) {
            MapInfo[] nearbyTiles = rc.senseNearbyMapInfos();
            // fill viable tiles
            for (MapInfo tile : nearbyTiles) {
                if (tile.getPaint() == PaintType.EMPTY && rc.canAttack(tile.getMapLocation())){
                    rc.attack(tile.getMapLocation());
                }
            }

            // move towards a tile we can fill
            for (MapInfo Tile : nearbyTiles) {
                if (Tile.getPaint() == PaintType.EMPTY && !rc.canAttack(Tile.getMapLocation())){
                    Direction dir = pathfinder.getMove(Tile.getMapLocation());
                    if (dir != null && rc.canMove(dir)){
                        rc.move(dir);
                    }
                }
            }
        }


    }

        
        

}

