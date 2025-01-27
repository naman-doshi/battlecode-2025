package fix_atk_micro.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import fix_atk_micro.Game;
import fix_atk_micro.robot.Strategy;

import static fix_atk_micro.util.Util.isFriendly;


import static fix_atk_micro.Game.*;
import static fix_atk_micro.Config.*;

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
            return true;
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
