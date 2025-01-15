package caterpillow.robot.agents.soldier;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import static caterpillow.util.Util.indicate;

// pathfinding testing
public class AttackTowerStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    int numAttacks;

    public AttackTowerStrategy(MapLocation target) {
        bot = (Agent) Game.bot;
        this.target = target;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (rc.canSenseLocation(target)) {
            RobotInfo bot = rc.senseRobotAtLocation(target);
            return (bot == null || !bot.getType().isTowerType());
        }
        return false;
    }

    // TODO: combat micro
    @Override
    public void runTick() throws GameActionException {
        indicate("ATTACKING TOWER");
        // TODO: running from enemy (hitting those circle strafes)
        // bot.pathfinder.makeMove(target);
        // if (rc.isActionReady()) {
        //     if (rc.canAttack(target)) {
        //         rc.attack(target);
        //     }
        // }

        // attempt to kite??
        // if numattacks even, move towards, then attack
        // if numattacks odd, attack, then move away

        // obv move until in range
        int distanceSquared = rc.getLocation().distanceSquaredTo(target);
        if (distanceSquared > 16 && numAttacks == 0) {
            bot.pathfinder.makeMove(target);
            return;
        }

        if (rc.isActionReady() && rc.isMovementReady() && rc.getPaint() >= 8) {
            Direction plannedMove = bot.pathfinder.getMove(target);
            if (!rc.canAttack(target) && rc.getLocation().add(plannedMove).distanceSquaredTo(target) <= 9) {
                bot.pathfinder.makeMove(target);
                rc.attack(target);
            } else if (rc.canAttack(target)) {
                rc.attack(target);
                bot.pathfinder.makeMove(rc.getLocation().add(rc.getLocation().directionTo(target).opposite()));
            }
        }

        numAttacks++;
    }
}
