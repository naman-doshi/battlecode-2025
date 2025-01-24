package caterpillow.robot.agents;

import battlecode.common.*;
import caterpillow.Game;
import static caterpillow.Game.*;
import caterpillow.robot.Strategy;
import static caterpillow.tracking.CellTracker.*;
import static caterpillow.tracking.RobotTracker.*;
import static caterpillow.util.Util.*;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.agents.soldier.Soldier;

public abstract class AbstractAttackTowerStrategy extends Strategy {
    public Agent bot;
    public MapLocation target;
    public MapLocation safeSquare;
    public BugnavPathfinder normalPathfinder = new BugnavPathfinder();
    public BugnavPathfinder safePathfinder = new BugnavPathfinder(c -> isInDanger(c.getMapLocation()));

    public AbstractAttackTowerStrategy(MapLocation target) {
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

    public abstract boolean canHitTower(MapLocation loc) throws GameActionException;
    public abstract void tryAttack() throws GameActionException;

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
                    if (canHitTower(loc)) {
                        canGetInRange = true;
                        goodDir = dir;
                        break;
                    }
                }
            }

            boolean syncWait = bot instanceof Soldier && ((Soldier)bot).syncAttacks && time % 2 == 1;
            if(syncWait) {
                if(getNearestRobot(b -> b.getType() == UnitType.MOPPER && !isFriendly(b)) != null) syncWait = false;
            }
            if(canGetInRange && rc.isMovementReady() && rc.isActionReady() && !syncWait) {
                bot.move(goodDir);
                tryAttack();
            } else if (!canGetInRange) {
                indicate(":(");
                indicate(rc.getLocation().toString());
                indicate(isInDanger(new MapLocation(40, 7)) + "");
                safePathfinder.makeMove(target);
            }
        }
    }
}
