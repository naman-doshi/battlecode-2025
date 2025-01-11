package lmkaepillow.robot.agents;

import java.util.Arrays;
import java.util.List;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import lmkaepillow.pathfinding.BugnavPathfinder;


public class Splasher extends Agent {

    int enemyX = 0;
    int enemyY = 0;
    boolean attackMode = true;

    @Override
    public void init(RobotController rc) {
        pathfinder = new BugnavPathfinder();
        
        // is the map mirrored hor or vert?
        int dist_hormiddle = Math.abs(rc.getLocation().x - rc.getMapWidth() / 2);
        int dist_vertmiddle = Math.abs(rc.getLocation().y - rc.getMapHeight() / 2);
        if (dist_hormiddle >= dist_vertmiddle) {
            enemyX = rc.getMapWidth() - rc.getLocation().x;
            enemyY = rc.getLocation().y;
        } else {
            enemyX = rc.getLocation().x;
            enemyY = rc.getMapHeight() - rc.getLocation().y;
        }
        System.out.println("Enemy is at " + enemyX + ", " + enemyY);
    }

    @Override
    public void runTick(RobotController rc) throws GameActionException {
        if (!rc.isActionReady()) return;
        MapLocation enemy = new MapLocation(enemyX, enemyY);
        // list of towers as unit types
        UnitType[] towers = new UnitType[] {UnitType.LEVEL_ONE_PAINT_TOWER, UnitType.LEVEL_TWO_PAINT_TOWER, UnitType.LEVEL_THREE_PAINT_TOWER, 
            UnitType.LEVEL_ONE_DEFENSE_TOWER, UnitType.LEVEL_TWO_DEFENSE_TOWER, UnitType.LEVEL_THREE_DEFENSE_TOWER,
            UnitType.LEVEL_ONE_MONEY_TOWER, UnitType.LEVEL_TWO_MONEY_TOWER, UnitType.LEVEL_THREE_MONEY_TOWER};
        
        List<UnitType> towerList = Arrays.asList(towers);

        MapInfo[] nearbyTiles = rc.senseNearbyMapInfos();
        RobotInfo[] nearbyRobots = rc.senseNearbyRobots();


        // check nearby tiles
        for (RobotInfo robot : nearbyRobots) {
            if (robot.getTeam() != rc.getTeam() && towerList.contains(robot.getType())) {
                enemyX = robot.getLocation().x;
                enemyY = robot.getLocation().y;
                attackMode = false;  
            }
        }

        for (MapInfo tile : nearbyTiles) {
            if (tile.hasRuin()) {
                attackMode = false;
            }
        }

        if (attackMode == true) {
            Direction dir = null;
            if (enemyX > rc.getLocation().x && enemyY > rc.getLocation().y) {
                if (rc.canMove(Direction.NORTHEAST)) {
                    dir = Direction.NORTHEAST;
                } else if (rc.canMove(Direction.EAST)) {
                    dir = Direction.EAST;
                } else if (rc.canMove(Direction.NORTH)) {
                    dir = Direction.NORTH;
                }
            } else if (enemyX > rc.getLocation().x && enemyY < rc.getLocation().y) {
                if (rc.canMove(Direction.SOUTHEAST)) {
                    dir = Direction.SOUTHEAST;
                } else if (rc.canMove(Direction.EAST)) {
                    dir = Direction.EAST;
                } else if (rc.canMove(Direction.SOUTH)) {
                    dir = Direction.SOUTH;
                }
            } else if (enemyX < rc.getLocation().x && enemyY > rc.getLocation().y) {
                if (rc.canMove(Direction.NORTHWEST)) {
                    dir = Direction.NORTHWEST;
                } else if (rc.canMove(Direction.WEST)) {
                    dir = Direction.WEST;
                } else if (rc.canMove(Direction.NORTH)) {
                    dir = Direction.NORTH;
                }
            } else if (enemyX < rc.getLocation().x && enemyY < rc.getLocation().y) {
                if (rc.canMove(Direction.SOUTHWEST)) {
                    dir = Direction.SOUTHWEST;
                } else if (rc.canMove(Direction.WEST)) {
                    dir = Direction.WEST;
                } else if (rc.canMove(Direction.SOUTH)) {
                    dir = Direction.SOUTH;
                }
            } else if (enemyX == rc.getLocation().x) {
                if (enemyY > rc.getLocation().y) {
                    if (rc.canMove(Direction.NORTH)) {
                        dir = Direction.NORTH;
                    } else if (rc.canMove(Direction.NORTHEAST)) {
                        dir = Direction.NORTHEAST;
                    } else if (rc.canMove(Direction.NORTHWEST)) {
                        dir = Direction.NORTHWEST;
                    }
                } else {
                    if (rc.canMove(Direction.SOUTH)) {
                        dir = Direction.SOUTH;
                    } else if (rc.canMove(Direction.SOUTHEAST)) {
                        dir = Direction.SOUTHEAST;
                    } else if (rc.canMove(Direction.SOUTHWEST)) {
                        dir = Direction.SOUTHWEST;
                    }
                }
            } else if (enemyY == rc.getLocation().y) {
                if (enemyX > rc.getLocation().x) {
                    if (rc.canMove(Direction.EAST)) {
                        dir = Direction.EAST;
                    } else if (rc.canMove(Direction.NORTHEAST)) {
                        dir = Direction.NORTHEAST;
                    } else if (rc.canMove(Direction.SOUTHEAST)) {
                        dir = Direction.SOUTHEAST;
                    }
                } else {
                    if (rc.canMove(Direction.WEST)) {
                        dir = Direction.WEST;
                    } else if (rc.canMove(Direction.NORTHWEST)) {
                        dir = Direction.NORTHWEST;
                    } else if (rc.canMove(Direction.SOUTHWEST)) {
                        dir = Direction.SOUTHWEST;
                    }
                }
            } 

            if (dir == null) {
                for (MapInfo tile : nearbyTiles) {
                    if (!tile.getPaint().isAlly() && rc.canAttack(tile.getMapLocation())){
                        rc.attack(tile.getMapLocation());
                        return;
                    }
                }
            }

            if (dir != null) {
                rc.move(dir);
            }
        } else {
            
            // // build a tower for fun
            // MapInfo curRuin = null;
            // for (MapInfo tile : nearbyTiles){
            //     if (tile.hasRuin()){
            //         curRuin = tile;
            //     }
            // }

            // if (curRuin != null){
            //     MapLocation targetLoc = curRuin.getMapLocation();
                
            //     // mark pattern if needed
            //     MapLocation shouldBeMarked = curRuin.getMapLocation().add(Direction.EAST);
            //     if (rc.canSenseLocation(shouldBeMarked) && rc.senseMapInfo(shouldBeMarked).getMark() == PaintType.EMPTY && rc.canMarkTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc)){
            //         rc.markTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc);
            //         System.out.println("Trying to build a tower at " + targetLoc);
            //     }

            //     // clear everything that's in reach
            //     boolean filled = false;
            //     for (MapInfo patternTile : rc.senseNearbyMapInfos()){
            //         if (patternTile.getMark() != patternTile.getPaint() && patternTile.getMark() != PaintType.EMPTY && (patternTile.getPaint() == PaintType.ENEMY_PRIMARY || patternTile.getPaint() == PaintType.ENEMY_SECONDARY)){
            //             boolean useSecondaryColor = patternTile.getMark() == PaintType.ALLY_SECONDARY;
            //             if (rc.canAttack(patternTile.getMapLocation())) {
            //                 System.out.println("mop colour there is " + patternTile.getPaint());
            //                 rc.attack(patternTile.getMapLocation(), useSecondaryColor);
            //                 System.out.println("Filled a tile at " + patternTile.getMapLocation());
            //                 filled = true;
            //             }  
            //         }
            //     }

            //     // if we couldn't reach anything, move towards something we can fill
            //     if (!filled) {
            //         System.out.println("mop Couldn't fill anything, moving towards a tile we can fill");
            //         for (MapInfo patternTile : nearbyTiles) {
            //             if (patternTile.getMark() != patternTile.getPaint() && patternTile.getMark() != PaintType.EMPTY  && (patternTile.getPaint() == PaintType.ENEMY_PRIMARY || patternTile.getPaint() == PaintType.ENEMY_SECONDARY)){
            //                 Direction dir = pathfinder.getMove(patternTile.getMapLocation(), rc);
            //                 if (dir != null && rc.canMove(dir)){
            //                     rc.move(dir);
            //                     return;
            //                 } 
            //             }
            //         }
            //     }

            //     // ge

            //     // complete the ruin if we can.
            //     if (rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc)){
            //         rc.completeTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc);
            //         rc.setTimelineMarker("Tower built", 0, 255, 0);
            //         System.out.println("Built a tower at " + targetLoc + "!");
            //     }
            // }

            // fill viable tiles
            for (MapInfo tile : nearbyTiles) {
                // check all blocks in a radius of sqrt 2 from this
                int numAlly = 0;
                for (MapInfo tile2 : rc.senseNearbyMapInfos(tile.getMapLocation(), 2)){
                    if (tile2.getPaint().isAlly()){
                        numAlly++;
                    }
                }
                if (numAlly < 2 && rc.canAttack(tile.getMapLocation())){
                    rc.attack(tile.getMapLocation());
                    return;
                }
            }

            // move towards a tile we can fill
            for (MapInfo Tile : nearbyTiles) {
                int numAlly = 0;
                for (MapInfo tile2 : rc.senseNearbyMapInfos(Tile.getMapLocation(), 2)){
                    if (tile2.getPaint().isAlly()){
                        numAlly++;
                    }
                }

                if (numAlly < 2 && !rc.canAttack(Tile.getMapLocation())){
                    Direction dir = pathfinder.getMove(Tile.getMapLocation());
                    if (dir != null && rc.canMove(dir)){
                        rc.move(dir);
                        return;
                    }
                }
            }

        }

        
        

    }
}
