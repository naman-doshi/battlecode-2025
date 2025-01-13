package caterpillow.robot.agents.soldier;

import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;
import static caterpillow.util.Util.cheapGetNearestCell;
import static caterpillow.util.Util.checkerboardPaint;
import static caterpillow.util.Util.decodeLoc;
import static caterpillow.util.Util.getSRPIds;
import static caterpillow.util.Util.guessEnemyLocs;
import static caterpillow.util.Util.isVisiblyWithinRuin;
import static caterpillow.util.Util.project;
import static caterpillow.util.Util.subtract;

import static caterpillow.Game.*;
import static caterpillow.util.Util.*;

public class SRPStrategy extends Strategy {

    public Soldier bot;

    // enemyLocs is more like "POI locs"
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;

    public SRPStrategy() throws GameActionException {
        bot = (Soldier) Game.bot;

        // cursed way to only keep the first elem but idc
        this.enemyLocs = guessEnemyLocs(bot.home);
        this.enemyLocs.removeLast();
        this.enemyLocs.removeLast();
        
        this.enemy = enemyLocs.get(0);
        
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    public void safeMove(MapLocation loc) throws GameActionException {
        if (rc.getLocation().isAdjacentTo(loc) && !rc.senseMapInfo(loc).getPaint().isAlly()) {
            return;
        }
        // wait until andy's buffed pathfinder
        bot.pathfinder.makeMove(loc);
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("srp");
        // TODO: better scouting system!!!

        // get some paint from our towers if we're nearby one
        if (rc.getPaint() < 100) {
            RobotInfo[] bots = rc.senseNearbyRobots(2);
            for (RobotInfo robot : bots) {
                if (robot.getTeam() == rc.getTeam() && rc.senseMapInfo(robot.getLocation()).hasRuin()) {
                    if (rc.canTransferPaint(robot.getLocation(), -100)) {
                        rc.transferPaint(robot.getLocation(), -100);
                        return;
                    }
                }
            }
        }
        
        if (rc.canSenseLocation(enemy)) {
            // if we can see the enemy, just go to the next enemy loc.
            enemyLocs.removeFirst();

            // procedurally gen the next one
            while (enemyLocs.size() < 1) {
                Random rng = new Random();
                int x = rng.nextInt(0, rc.getMapWidth() - 1);
                int y = rng.nextInt(0, rc.getMapHeight() - 1);
                if (new MapLocation(x, y).distanceSquaredTo(rc.getLocation()) >= 9) {
                    MapLocation moveDir = subtract(new MapLocation(x, y), rc.getLocation());
                    enemyLocs.add(project(rc.getLocation(), moveDir, (double) (rc.getMapWidth() + rc.getMapHeight()) / 2));
                    MapLocation xy = new MapLocation(x, y);
                    enemyLocs.add(xy);
                }
            }
            
            enemy = enemyLocs.getFirst();
            //indicate("NEW ENEMY LOC: " + enemy);
        }

        //System.out.println("Left after loc update: " + Clock.getBytecodesLeft());

        // check all visible cells. are any of them ruins?
        // MapInfo[] visibleCells = rc.senseNearbyMapInfos();
        // List<MapInfo> ruins = new ArrayList<>();
        // Map<MapLocation, Boolean> ruinTrolled = new HashMap<>();
        
        // for (MapInfo cell : visibleCells) {
            
        //     if (cell.hasRuin() && rc.senseRobotAtLocation(cell.getMapLocation())!=null && rc.senseRobotAtLocation(cell.getMapLocation()).getTeam() != rc.getTeam()) {
        //         ruins.add(cell);
        //         ruinTrolled.put(cell.getMapLocation(), false);
        //     }
        // }

        // // check if ruin is alr trolled
        // for (MapInfo cell: visibleCells) {
        //     for (MapInfo ruin: ruins) {
        //         // if the cell is in the ruin's square and it's painted, mark it as trolled
        //         if (isWithinRuin(cell.getMapLocation(), ruin.getMapLocation())) {
        //             if (cell.getPaint().isAlly()) ruinTrolled.put(ruin.getMapLocation(), true);
        //         }
        //     }
        // }

        // MapInfo[] attackable = rc.senseNearbyMapInfos(9);

        // // for all attackable squares, try troll first
        // for (MapLocation ruinLoc : ruinTrolled.keySet()) {
        //     if (!ruinTrolled.get(ruinLoc)) {
        //         // if the ruin hasn't been trolled yet, troll it
        //         for (MapInfo cell : attackable) {
        //             if (isWithinRuin(cell.getMapLocation(), ruinLoc) && rc.canAttack(cell.getMapLocation())) {
        //                 bot.checkerboardAttack(cell.getMapLocation());
        //                 System.out.println("TROLLING RUIN" + ruinLoc);
        //                 return;
        //             }
        //         }
        //     }
        // }

        //System.out.println("Left after ruin trolling: " + Clock.getBytecodesLeft());

        

        //System.out.println("Left after paint transfer: " + Clock.getBytecodesLeft());

        // first: it obviously needs to be the wrong colour, non-enemy paint, and passable
        // second: if it's outside a ruin OR a neutral colour, obviously paint it
        // but if it's inside and painted w ally, only paint it if there's more than one SRP on it (since one of them will be the ruin's SRP)
        MapInfo target = cheapGetNearestCell(c -> c.getPaint()!=checkerboardPaint(c.getMapLocation()) && c.isPassable() && !c.getPaint().isEnemy() && (!isVisiblyWithinRuin(c.getMapLocation()) || c.getPaint()==PaintType.EMPTY || getSRPIds(c.getMapLocation()).size() > 1));

        //System.out.println("Left after target selection: " + Clock.getBytecodesLeft());
        
        if (target != null) {
            if (rc.canAttack(target.getMapLocation())) {
                bot.checkerboardAttack(target.getMapLocation());
                // try complete the SRP i just attacked
                List<Integer> srpIDs = getSRPIds(target.getMapLocation());
                for (int id : srpIDs) {
                    MapLocation srpLoc = decodeLoc(id);
                    if (rc.canCompleteResourcePattern(srpLoc)) {
                        rc.completeResourcePattern(srpLoc);
                    }
                }
            } else {
                safeMove(target.getMapLocation());
                //indicate("moving to target " + target.getMapLocation());
            }
        } else {
            safeMove(enemy);
            //indicate("moving to enemy");
        }

        //System.out.println("Left after atk: " + Clock.getBytecodesLeft());

        


    }
}
