package caterpillow.robot.agents.soldier;

import java.util.List;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;
import caterpillow.util.GameSupplier;
import static caterpillow.util.Util.checkerboardPaint;
import static caterpillow.util.Util.guessEnemyLocs;

public class SRPStrategy extends Strategy {

    public Soldier bot;

    // enemyLocs is more like "POI locs"
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;
    public List<GameSupplier<MapInfo>> suppliers;

    public SRPStrategy() throws GameActionException {
        bot = (Soldier) Game.bot;
        this.enemyLocs = guessEnemyLocs(bot.home);
        this.enemy = enemyLocs.get(0);
        enemyLocs.addLast(bot.home);
    }


    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {


        // TODO: better scouting system!!!
        if (rc.canSenseLocation(enemy)) {
            // if we can see the enemy, just go to the next enemy loc. it's kinda cyclic for now
            enemyLocs.addLast(enemy);
            enemyLocs.removeFirst();
            enemy = enemyLocs.get(0);

        }

        MapInfo[] cells = rc.senseNearbyMapInfos();
        for (MapInfo cell : cells) {
            if (cell.getPaint() != checkerboardPaint(cell.getMapLocation()) && !cell.getPaint().isEnemy() && rc.canAttack(cell.getMapLocation()) && cell.isPassable()) {
                //System.out.println("attacking cell " + cell.getMapLocation() + " with paint " + cell.getPaint() + " and checkerboard paint " + checkerboardPaint(cell.getMapLocation()));
                bot.checkerboardAttack(cell.getMapLocation());
                return;
            }
        }

        for (MapInfo cell : cells) {
            if (cell.getPaint() != checkerboardPaint(cell.getMapLocation()) && !cell.getPaint().isEnemy() && cell.isPassable()) {
                Direction dir = bot.pathfinder.getMove(cell.getMapLocation());
                if (dir != null && rc.canMove(dir) && !rc.senseMapInfo(rc.getLocation().add(dir)).getPaint().isEnemy()) {
                    rc.move(dir);
                    return;
                }
            }
        }

        Direction dir = bot.pathfinder.getMove(enemy);
        if (dir != null && rc.canMove(dir) && !rc.senseMapInfo(rc.getLocation().add(dir)).getPaint().isEnemy()) {
            rc.move(dir);
        }
        
    }
}
