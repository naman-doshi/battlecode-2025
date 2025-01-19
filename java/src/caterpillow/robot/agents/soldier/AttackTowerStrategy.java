package caterpillow.robot.agents.soldier;

import battlecode.common.*;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.util.Util.*;

import caterpillow.pathfinding.AbstractPathfinder;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import static caterpillow.tracking.CellTracker.*;

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
        if(rc.getHealth() < 25) return true;
        if (rc.canSenseLocation(target)) {
            RobotInfo bot = rc.senseRobotAtLocation(target);
            return bot == null || isFriendly(bot);
        }
        return true;
    }

    public void tryAttack() throws GameActionException {
        if (rc.isActionReady() && rc.getPaint() >= 8) {
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
            if(safeSquare == null || isInDanger(safeSquare)) {
                safeSquare = getNearestCell(c -> !isInDanger(c.getMapLocation())).getMapLocation();
            }
            bot.pathfinder.makeMove(safeSquare);
        } else if(rc.isMovementReady()) {
            bot.pathfinder.makeMove(target);
            tryAttack();
        }
    }
}
