package caterpillow.robot.agents.splasher;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import static caterpillow.Game.*;
import caterpillow.robot.Strategy;
import static caterpillow.tracking.CellTracker.*;
import static caterpillow.util.Util.*;
import caterpillow.pathfinding.*;
import caterpillow.util.Pair;
import caterpillow.util.Profiler;
import static java.lang.Math.*;

public class SplasherAttackTowerStrategy extends Strategy {

    Splasher bot;
    MapLocation target;
    MapLocation safeSquare;
    Direction lastMove;
    BugnavPathfinder pathfinder;

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

    boolean canHitTower(MapLocation loc) {
        return abs(loc.x - target.x) + abs(loc.y - target.y) <= 4;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("ATTACKING TOWER AT " + target);
        if(isInDanger(rc.getLocation())) {
            tryAttack();
            if(safeSquare == null || !rc.getLocation().isAdjacentTo(safeSquare) || isInDanger(safeSquare)) {
                safeSquare = getNearestLocation(loc -> !isInDanger(loc));
            }
            pathfinder.makeMove(safeSquare);
        } else if(rc.isMovementReady()) {
            if(downgrade(rc.senseRobotAtLocation(target).type) == UnitType.LEVEL_ONE_DEFENSE_TOWER) {
                safeSquare = null;
            } else {
                if(safeSquare == null || isInDanger(safeSquare) || !canHitTower(safeSquare)) {
                    safeSquare = getNearestLocation(loc -> canHitTower(loc) && !isInDanger(loc));
                }
            }
            if(safeSquare != null) indicate("SAFE SQUARE: " + safeSquare);
            else indicate("NO SAFE SQUARE");
            Direction move = safeSquare != null ? pathfinder.getMove(safeSquare) : pathfinder.getMove(target);
            if(move != null && rc.canMove(move) && (!isInDanger(rc.getLocation().add(move)) || rc.isActionReady())) {
                pathfinder.makeMove(move);
            }
            tryAttack();
        }
    }
}
