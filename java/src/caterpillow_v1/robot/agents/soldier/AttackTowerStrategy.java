package caterpillow_v1.robot.agents.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow_v1.Game;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.robot.agents.Agent;

import static caterpillow_v1.Game.*;
import static caterpillow_v1.util.Util.*;

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
        indicate("ATTACKING TOWER");
        // TODO: running from enemy (hitting those circle strafes)
        bot.pathfinder.makeMove(target);
        if (rc.isActionReady()) {
            if (rc.canAttack(target)) {
                rc.attack(target);
            }
        }
    }
}
