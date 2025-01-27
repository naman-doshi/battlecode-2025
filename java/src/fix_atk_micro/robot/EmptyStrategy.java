package fix_atk_micro.robot;

import battlecode.common.GameActionException;
import static fix_atk_micro.util.Util.*;

public class EmptyStrategy extends Strategy {
    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("EMPTYSTRAT");
    }
}
