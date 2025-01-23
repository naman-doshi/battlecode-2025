package caterpillow.robot.agents.soldier;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.*;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.soldier.Soldier;
import static caterpillow.tracking.CellTracker.*;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.isInDanger;

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
                safeSquare = getNearestLocation(loc -> !isInDanger(loc));
            }
            if(safeSquare != null) bot.pathfinder.makeMove(safeSquare);
        } else {
            safeSquare = rc.getLocation();
            if(rc.isMovementReady() && rc.isActionReady() && (!bot.syncAttacks || time % 2 == 0)) {
                bot.pathfinder.noPreference = true;
                bot.pathfinder.makeMove(target);
                bot.pathfinder.noPreference = false;
                tryAttack();
            }
        }
    }
}
