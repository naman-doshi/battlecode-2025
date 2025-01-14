package caterpillow_v1.robot;

import battlecode.common.GameActionException;

public class EmptyStrategy extends Strategy {
    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
    }
}
