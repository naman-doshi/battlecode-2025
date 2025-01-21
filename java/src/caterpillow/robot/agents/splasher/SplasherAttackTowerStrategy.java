package caterpillow.robot.agents.splasher;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import static caterpillow.tracking.CellTracker.getNearestCell;
import static caterpillow.util.Util.*;
import caterpillow.pathfinding.*;
import caterpillow.util.Pair;

public class SplasherAttackTowerStrategy extends Strategy {

    Splasher bot;
    MapLocation target;
    MapLocation safeSquare;
    Direction lastMove;
    AbstractPathfinder pathfinder;

    public SplasherAttackTowerStrategy(MapLocation target) {
        bot = (Splasher) Game.bot;
        this.target = target;
        pathfinder = new BugnavPathfinder();
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
            Pair<MapLocation, Boolean> res = bot.bestAttackLocation();
            MapLocation target = res.first;
            boolean paintType = res.second;
            if(target != null && rc.canAttack(target)) {
                rc.attack(target, paintType);
            }
        }
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("ATTACKING TOWER AT " + target);
        if(isInDanger(rc.getLocation())) {
            tryAttack();
            if(safeSquare == null || !rc.getLocation().isAdjacentTo(safeSquare) || isInDanger(safeSquare)) {
                safeSquare = getNearestCell(c -> !isInDanger(c.getMapLocation())).getMapLocation();
            }
            pathfinder.makeMove(safeSquare);
        } else if(rc.isMovementReady() && rc.isActionReady()) {
            pathfinder.makeMove(target);
            tryAttack();
        }
    }
}
