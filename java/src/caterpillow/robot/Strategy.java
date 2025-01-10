package caterpillow.robot;

import battlecode.common.GameActionException;

public abstract class Strategy {
    public abstract boolean isComplete();
    public abstract void runTick() throws GameActionException;
}
