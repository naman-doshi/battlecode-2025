package caterpillow.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.util.TowerTracker;

import static caterpillow.util.Util.isFriendly;


import static caterpillow.Game.*;
import static caterpillow.Config.*;

public class UpgradeTowerStrategy extends Strategy {

    Agent bot;
    int targetLevel;
    MapLocation target;

    public UpgradeTowerStrategy(MapLocation target, int targetLevel) {
        this.targetLevel = targetLevel;
        this.target = target;
        bot = (Agent) Game.bot;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (!canUpgrade(targetLevel)) {
            return true;
        }

        if (!rc.canSenseLocation(target)) {
            return false;
        }
        RobotInfo info = rc.senseRobotAtLocation(target);
        if (info == null || !isFriendly(info)) {
            return true;
        }
        return info.getType().level == targetLevel;
    }

    @Override
    public void runTick() throws GameActionException {
        bot.pathfinder.makeMove(target);
        if (!rc.canSenseLocation(target)) {
            return;
        }
        RobotInfo info = rc.senseRobotAtLocation(target);
        if (rc.canUpgradeTower(info.getLocation())) {
            rc.upgradeTower(info.getLocation());
        }
    }
}
