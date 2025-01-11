package caterpillow.robot.towers.strategies;

import battlecode.common.*;
import caterpillow.robot.Strategy;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class TowerAttackStrategy extends Strategy {

    private boolean isInDanger() throws GameActionException {
        return getNearestRobot(bot -> !isFriendly(bot)) != null;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return !isInDanger();
    }

    @Override
    public void runTick() throws GameActionException {
        RobotInfo info = getBestRobot((a, b) -> {
            int a1 = a.getType().ordinal();
            int b1 = b.getType().ordinal();
            int h1 = a.getHealth();
            int h2 = b.getHealth();
            if (a1 == b1) {
                if (h1 > h2) return b;
                else return a;
            } else {
                if (a1 < b1) return a;
                else return b;
            }
        }, e -> !isFriendly(e) && e.getType().isRobotType() && rc.canAttack(e.getLocation()));
        if (info != null) {
            rc.attack(info.getLocation());
        }
    }
}
