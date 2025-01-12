package caterpillow.robot.agents.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;

import static caterpillow.Game.*;

// pathfinding testing
public class AttackTowerStrategy extends Strategy {

    Agent bot;
    MapLocation target;

    public AttackTowerStrategy(MapLocation target) {
        bot = (Agent) Game.bot;
        this.target = target;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        RobotInfo bot = rc.senseRobotAtLocation(target);
        return (bot == null || !bot.getType().isTowerType());
    }

    // TODO: combat micro
    @Override
    public void runTick() throws GameActionException {
        rc.setIndicatorString("ATTACKING TOWER");
        // TODO: running from enemy (hitting those circle strafes)
        if (rc.isMovementReady()) {
            rc.move(bot.pathfinder.getMove(target));
        }
        if (rc.isActionReady()) {
            if (rc.canAttack(target)) {
                rc.attack(target);
            }
        }
    }
}
