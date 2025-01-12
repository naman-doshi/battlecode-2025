package caterpillow.robot.agents.mopper;

import java.util.List;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;
import static caterpillow.util.Util.guessEnemyLocs;

public class MopperOffenceStrategy extends Strategy {

    public Mopper bot;
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;
    public boolean enemyFound = false;

    public MopperOffenceStrategy() throws GameActionException {
        bot = (Mopper) Game.bot;
        this.enemyLocs = guessEnemyLocs(bot.home);
        this.enemy = enemyLocs.get(0);
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {

        // just checking and updating enemy locs:


        RobotInfo enemyThere = rc.senseRobotAtLocation(enemy);
        
        // if enemy hasn't been found, and we can sense it but it isn't there, update the enemy loc to the next one
        if (!enemyFound && (enemyThere == null || enemyThere != null && enemyThere.getType().isRobotType())) {
            enemyLocs.removeFirst();
            enemy = enemyLocs.getFirst();
        }

        // if it hasn't been found and we can sense it and it is there, set enemyFound to true
        if (!enemyFound && enemyThere != null && enemyThere.getType().isTowerType()) {
            enemyFound = true;
        }

        
        // first: attack any enemy paint in reach

        // if there is an enemy on enemy paint on reach, we do the best attack
        boolean isEnemyPainted = false;
        boolean isEnemyPaintReachable = false;
        boolean isEnemyPaintVisible = false;
        MapInfo[] cells = rc.senseNearbyMapInfos();
        for (MapInfo cell : cells) {
            RobotInfo robotThere = rc.senseRobotAtLocation(cell.getMapLocation());
            if (cell.getPaint().isEnemy() && rc.canAttack(cell.getMapLocation()) && robotThere != null && robotThere.getTeam() != rc.getTeam() && robotThere.getType().isRobotType()) {
                isEnemyPainted = true;
                isEnemyPaintVisible = true;
                isEnemyPaintReachable = true;
            }
            if (cell.getPaint().isEnemy() && rc.canAttack(cell.getMapLocation())) {
                isEnemyPaintReachable = true;
                isEnemyPaintVisible = true;
            }
            if (cell.getPaint().isEnemy()) {
                isEnemyPaintVisible = true;
            }
            if (isEnemyPaintReachable && isEnemyPainted && isEnemyPaintVisible) {
                break;
            }
        }
        
        if (isEnemyPainted) {
            bot.doBestAttack();
        } else if (isEnemyPaintReachable) {
            // if there is enemy paint in reach, attack it
            for (MapInfo cell : cells) {
                if (cell.getPaint().isEnemy() && rc.canAttack(cell.getMapLocation())) {
                    rc.attack(cell.getMapLocation());
                    break;
                }
            }
        } else if (isEnemyPaintVisible) {
            // if there is enemy paint visible, move towards it
            MapLocation enemyPaintLoc = null;
            for (MapInfo cell : cells) {
                if (cell.getPaint().isEnemy()) {
                    enemyPaintLoc = cell.getMapLocation();
                    break;
                }
            }
            if (enemyPaintLoc != null) {
                Direction dir = bot.pathfinder.getMove(enemyPaintLoc);
                if (rc.canMove(dir)) {
                    rc.move(dir);
                }
            }
        } else {

            // walk towards enemy location
            Direction dir = bot.pathfinder.getMove(enemy);

            if (rc.canMove(dir)) {
                rc.move(dir);
            }

        }
          
        
    }
}
