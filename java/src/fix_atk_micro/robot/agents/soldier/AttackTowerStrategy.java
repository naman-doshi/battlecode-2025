package fix_atk_micro.robot.agents.soldier;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import fix_atk_micro.Game;
import static fix_atk_micro.Game.rc;
import static fix_atk_micro.Game.time;
import fix_atk_micro.robot.Strategy;
import static fix_atk_micro.tracking.CellTracker.getNearestLocation;
import static fix_atk_micro.util.Util.indicate;
import static fix_atk_micro.util.Util.isFriendly;
import static fix_atk_micro.util.Util.isInDanger;

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
        if(rc.getHealth() < 30) return true;
        if (rc.canSenseLocation(target)) {
            RobotInfo bot = rc.senseRobotAtLocation(target);
            return bot == null || isFriendly(bot);
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
        if(isInDanger(rc.getLocation())) {
            tryAttack();
            if(safeSquare == null || !rc.getLocation().isAdjacentTo(safeSquare) || isInDanger(safeSquare)) {
                indicate("RESET SAFE SQUARE");
                if(safeSquare == null) indicate("NULL");
                else indicate(safeSquare.toString());
                safeSquare = getNearestLocation(loc -> !isInDanger(loc) && !rc.senseMapInfo(loc).isWall());
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
