package caterpillow.robot.agents.soldier;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import static caterpillow.tracking.CellTracker.*;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.isInDanger;

// pathfinding testing
public class AttackTowerStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    MapLocation safeSquare;
    Direction lastMove;

    public AttackTowerStrategy(MapLocation target) {
        bot = (Agent) Game.bot;
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
        if(isInDanger(rc.getLocation())) {
            tryAttack();
            if(safeSquare == null || !rc.getLocation().isAdjacentTo(safeSquare) || isInDanger(safeSquare)) {
                safeSquare = getNearestLocation(loc -> !isInDanger(loc));
            }
            if(safeSquare != null) bot.pathfinder.makeMove(safeSquare);
        } else if(rc.isMovementReady() && rc.isActionReady()) {
            bot.pathfinder.makeMove(target);
            tryAttack();
        }
    }
}
