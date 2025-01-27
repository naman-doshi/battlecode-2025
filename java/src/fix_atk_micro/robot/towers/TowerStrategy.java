package fix_atk_micro.robot.towers;

import battlecode.common.GameActionException;
import fix_atk_micro.robot.Strategy;

public abstract class TowerStrategy extends Strategy {
    @Override
    public final boolean isComplete() throws GameActionException {
        assert false : "tower strategies should ignore isComplete()";
        return false;
    }
}
