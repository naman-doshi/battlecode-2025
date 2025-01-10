package lmkaepillow.robot.towers;

import battlecode.common.RobotController;
import battlecode.common.UnitType;
import lmkaepillow.robot.Robot;

public abstract class Tower extends Robot {
    // im indexing levels from 0
    public int level;
    protected UnitType[] types;

    @Override
    public UnitType getType() {
        return types[level];
    }

    @Override
    public void init(RobotController rc) {
        level = 0;
    }

    public void upgrade() {
        level += 1;
    }
}
