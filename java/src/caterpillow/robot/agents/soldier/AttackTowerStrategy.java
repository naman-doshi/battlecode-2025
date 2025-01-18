package caterpillow.robot.agents.soldier;

import battlecode.common.*;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.util.Util.*;

import caterpillow.pathfinding.AbstractPathfinder;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;

// pathfinding testing
public class AttackTowerStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    Direction lastMove;

    AbstractPathfinder reversePathfinder;

    public AttackTowerStrategy(MapLocation target) {
        bot = (Agent) Game.bot;
        this.target = target;
        reversePathfinder = new BugnavPathfinder(c -> c.getMapLocation().distanceSquaredTo(target) <= UnitType.SOLDIER.actionRadiusSquared);
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (rc.canSenseLocation(target)) {
            RobotInfo bot = rc.senseRobotAtLocation(target);
            return bot == null || isFriendly(bot);
        }
        return true;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("ATTACKING TOWER AT " + target);
        if (rc.isActionReady() && rc.isMovementReady() && rc.getPaint() >= 8) {
            if (isInAttackRange(target)) {
                if (rc.canAttack(target)) {
                    rc.attack(target);
                    reversePathfinder.makeMove(target);
                }
            } else {
                bot.pathfinder.makeMove(target);
                if (rc.canAttack(target)) {
                    rc.attack(target);
                }
            }
        }
    }
}
