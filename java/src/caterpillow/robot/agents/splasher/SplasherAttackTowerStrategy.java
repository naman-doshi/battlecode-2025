package caterpillow.robot.agents.splasher;

import battlecode.common.*;
import static caterpillow.Game.*;
import caterpillow.robot.agents.AbstractAttackTowerStrategy;
import caterpillow.util.Pair;
import static java.lang.Math.*;
import static caterpillow.tracking.CellTracker.*;

public class SplasherAttackTowerStrategy extends AbstractAttackTowerStrategy {
    public SplasherAttackTowerStrategy(MapLocation target) {
        super(target);
    }
    @Override
    public boolean canHitTower(MapLocation loc) {
        return abs(loc.x - target.x) + abs(loc.y - target.y) <= 4;
    }
    @Override
    public void tryAttack() throws GameActionException {
        if (rc.isActionReady()) {
            // MapLocation target = getNearestLocation(loc -> loc.distanceSquaredTo(this.target) <= 4);
            // if(target != null && rc.canAttack(target)) {
            //     rc.attack(target, true);
            // }
            Pair<MapLocation, Boolean> res = ((Splasher)bot).bestAttackLocation();
            MapLocation target = res.first;
            boolean paintType = res.second;
            if(target != null && rc.canAttack(target)) {
                rc.attack(target, paintType);
            }
        }
    }
}
