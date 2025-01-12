package caterpillow.robot.towers;

import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.Game;

import static caterpillow.Game.*;
import static caterpillow.util.Util.*;
import static java.lang.Math.min;

/*

ceebs making this smart just give to every1 but the mopper

*/

public class RefillStrategy extends TowerStrategy {
    @Override
    public void runTick() throws GameActionException {
        RobotInfo best = getBestRobot((b1, b2) -> {
            if (missingPaint(b1) < missingPaint(b2)) {
                return b1;
            } else {
                return b2;
            }
        }, b -> (b.getType().equals(UnitType.SOLDIER) || b.getType().equals(UnitType.SPLASHER)) && isFriendly(b) && rc.canTransferPaint(b.getLocation(), 1));
        if (best != null) {
            if (missingPaint(best) > 10) {
                int amt = min(missingPaint(best), rc.getPaint());
                rc.transferPaint(best.getLocation(), amt);
            }
        }
    }
}
