package caterpillow_v1.robot.towers;

import battlecode.common.GameActionException;
import caterpillow_v1.robot.Strategy;

public abstract class TowerStrategy extends Strategy {
    @Override
    public final boolean isComplete() throws GameActionException {
        assert false : "tower strategies should ignore isComplete()";
        return false;
    }
}
