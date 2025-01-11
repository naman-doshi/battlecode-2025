package caterpillow.robot.agents;

import battlecode.common.GameActionException;
import caterpillow.robot.Strategy;

public class EmptyStrategy extends Strategy {
    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {

    }
}
