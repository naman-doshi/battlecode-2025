package caterpillow_v1.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow_v1.Game;
import caterpillow_v1.robot.Strategy;

import static caterpillow_v1.util.Util.getNearestRobot;
import static caterpillow_v1.util.Util.isFriendly;


import static caterpillow_v1.Game.*;
import static caterpillow_v1.Config.*;
import static caterpillow_v1.util.Util.*;

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
