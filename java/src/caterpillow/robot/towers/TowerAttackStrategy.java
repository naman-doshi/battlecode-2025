package caterpillow.robot.towers;

import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.util.Util.getBestRobot;
import static caterpillow.util.Util.isFriendly;

public class TowerAttackStrategy extends TowerStrategy {

    Tower bot;
    public int lastAttack = 0;

    int countEnemies() throws GameActionException {
        int count = 0;
        for (RobotInfo info : rc.senseNearbyRobots(rc.getType().actionRadiusSquared)) {
            if (!isFriendly(info)) {
                count++;
            }
        }
        return count;
    }

    public TowerAttackStrategy() {
        bot = (Tower) Game.bot;
    }

    @Override
    public void runTick() throws GameActionException {
        // towers already perma splash attack
//        if (rc.canAttack(null)) rc.attack(null);
        int enemies = countEnemies();
        if (enemies > 0) {
            // single target
            RobotInfo info = getBestRobot((a, b) -> {
                int a1 = a.getType().ordinal();
                int b1 = b.getType().ordinal();
                int h1 = a.getHealth();
                int h2 = b.getHealth();
                if (a1 == b1) {
                    if (h1 > h2) return b;
                    else return a;
                } else {
                    if (a1 < b1) return a;
                    else return b;
                }
            }, e -> !isFriendly(e) && e.getType().isRobotType() && rc.canAttack(e.getLocation()));
            if (info != null) {
                rc.attack(info.getLocation());
                lastAttack = Game.time;
            }
        }
    }
}
