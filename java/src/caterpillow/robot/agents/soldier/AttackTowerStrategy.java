package caterpillow.robot.agents.soldier;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.Game.time;
import caterpillow.robot.Strategy;
import static caterpillow.tracking.CellTracker.getNearestLocation;
import caterpillow.tracking.TowerTracker;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.isFriendly;

// pathfinding testing
public class AttackTowerStrategy extends Strategy {

    Soldier bot;
    MapLocation target;
    MapLocation safeSquare;
    Direction lastMove;

    public AttackTowerStrategy(MapLocation target) {
        bot = (Soldier) Game.bot;
        this.target = target;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        //if(rc.getHealth() < 40) return true;
        //int numEnemyMoppers = RobotTracker.countNearbyFriendly(b -> isEnemyAgent(b) && b.getType() == UnitType.MOPPER);
        //if (numEnemyMoppers >= 4) return true;
        if (rc.canSenseLocation(target)) {
            RobotInfo bot1 = rc.senseRobotAtLocation(target);
            return bot1 == null || isFriendly(bot1) || rc.getHealth() <= bot1.getType().attackStrength + 5;
        }
        return true;
    }

    public void tryAttack() throws GameActionException {
        if (rc.isActionReady()) {
            if(rc.canAttack(target)) {
                rc.attack(target);
            }
        }
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("ATTACKING TOWER AT " + target);
        if(safeSquare != null) indicate("SAFE SQUARE " + safeSquare.toString());
        if(TowerTracker.isCellInDanger(rc.getLocation())) {
            tryAttack();
            if(safeSquare == null || !rc.getLocation().isAdjacentTo(safeSquare) || TowerTracker.isCellInDanger(safeSquare)) {
                indicate("RESET SAFE SQUARE");
                if(safeSquare == null) indicate("NULL");
                else indicate(safeSquare.toString());
                safeSquare = getNearestLocation(loc -> !TowerTracker.isCellInDanger(loc) && !rc.senseMapInfo(loc).isWall());
            }
            if(safeSquare != null) bot.pathfinder.makeMove(safeSquare);
        } else {
            safeSquare = rc.getLocation();

            // try all directions
            boolean canGetInRange = false;
            Direction goodDir = null;
            for (Direction dir : Direction.values()) {
                if (rc.canMove(dir)) {
                    MapLocation loc = rc.getLocation().add(dir);
                    if (loc.distanceSquaredTo(target) <= 9) {
                        canGetInRange = true;
                        goodDir = dir;
                        break;
                    }
                }
            }

            if(canGetInRange && rc.isMovementReady() && rc.isActionReady() && (!bot.syncAttacks || time % 2 == 0)) {
                //bot.pathfinder.noPreference = true;
                bot.move(goodDir);
                //bot.pathfinder.noPreference = false;
                tryAttack();
            } else if (!canGetInRange){
                bot.pathfinder.makeMove(target);
            }
        }
    }
}
