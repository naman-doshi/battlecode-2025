package caterpillow.robot.agents.soldier;

import battlecode.common.*;
import static caterpillow.Game.*;
import caterpillow.robot.agents.AbstractAttackTowerStrategy;

public class SoldierAttackTowerStrategy extends AbstractAttackTowerStrategy {
    public SoldierAttackTowerStrategy(MapLocation target) {
        super(target);
    }
    @Override
    public boolean canHitTower(MapLocation loc) throws GameActionException {
        return loc.distanceSquaredTo(target) <= 9;
    }
    @Override
    public void tryAttack() throws GameActionException {
        if (rc.isActionReady()) {
            if(rc.canAttack(target)) {
                rc.attack(target);
            }
        }
    }
}
