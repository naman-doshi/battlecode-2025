package caterpillow;

import static java.lang.Math.max;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import static battlecode.common.UnitType.MOPPER;
import static caterpillow.Game.centre;
import static caterpillow.Game.origin;
import static caterpillow.Game.rc;
import static caterpillow.Game.trng;
import caterpillow.util.TowerTracker;
import static caterpillow.util.Util.getPaintLevel;
import static caterpillow.util.Util.guessEnemyLocs;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.project;
import static caterpillow.util.Util.subtract;

public class Config {


    // idea : dynamically update this based on coin amt
    // right now, we have too much paint in the endgame (when most towers are maxed)
    // we also need more chips on larger maps
    public static double targetRatio() {
        if (rc.getMapHeight() * rc.getMapWidth() < 1500) return 0.6;
        else {
            return 0.7;
        }
    }

    public static boolean canUpgrade(int level) {
        if (level == 2) {
            return rc.getChips() >= 3000;
        } else if (level == 3) {
            return rc.getChips() >= 6000;
        }
        return false;
    }

    public static boolean shouldSRPBuildTower() {
        return rc.getChips() >= 1200;
    }

    public static boolean shouldRescue(RobotInfo b) {
        assert rc.getType().equals(MOPPER);
        if (b.getPaintAmount() > 5) {
            return false;
        }
        if (rc.getPaint() <= 50) {
            return false;
        }
        int available = rc.getPaint() - 50;
        if (available >= 20) {
            return true;
        }
        return false;
    }

    public static boolean shouldRefill(RobotInfo b) {
        assert rc.getType().equals(MOPPER);
        if (getPaintLevel(b) > 0.5) {
            return false;
        }
        if (rc.getPaint() <= 50) {
            return false;
        }
        int available = rc.getPaint() - 50;
        if (available >= 20) {
            return true;
        }
        return false;
    }

    public static UnitType getNextType() {

       boolean enemyVisible = false;

       for (RobotInfo r : rc.senseNearbyRobots()) {
           if (!isFriendly(r)) {
               enemyVisible = true;
               break;
           }
       }

       if (enemyVisible) {
           return UnitType.LEVEL_ONE_DEFENSE_TOWER;
       }

        if (!TowerTracker.broken) {
            if ((double) TowerTracker.coinTowers / (double) rc.getNumberTowers() > targetRatio() || rc.getChips() >= 3000) {
                return UnitType.LEVEL_ONE_PAINT_TOWER;
            } else {
                return UnitType.LEVEL_ONE_MONEY_TOWER;
            }
        } else {
            if (trng.nextDouble() > targetRatio() || rc.getChips() >= 3000) {
                return UnitType.LEVEL_ONE_PAINT_TOWER;
            } else {
                return UnitType.LEVEL_ONE_MONEY_TOWER;
            }
        }
    }

    public static MapLocation genExplorationTarget(Random rng) {
        while (true) {
            int x = rng.nextInt(0, rc.getMapWidth() - 1);
            int y = rng.nextInt(0, rc.getMapHeight() - 1);
            MapLocation pivot = new MapLocation((rc.getLocation().x + origin.x) / 2, (rc.getLocation().y + origin.y) / 2);
            if (new MapLocation(x, y).distanceSquaredTo(rc.getLocation()) < 9 || new MapLocation(x, y).distanceSquaredTo(pivot) < 9) {
                continue;
            }
            MapLocation moveDir = subtract(new MapLocation(x, y), pivot);
            return project(pivot, moveDir, 0.7 * max(rc.getMapWidth(), rc.getMapHeight()));
        }
    }

    public static MapLocation genAggroTarget(Random rng) {
        while (true) {
            int x = rng.nextInt(0, rc.getMapWidth() - 1);
            int y = rng.nextInt(0, rc.getMapHeight() - 1);
            MapLocation loc = new MapLocation(x, y);
            MapLocation pivot = origin;
            if (loc.distanceSquaredTo(pivot) < 9 || pivot.distanceSquaredTo(loc) < pivot.distanceSquaredTo(centre)) {
                continue;
            }
            MapLocation moveDir = subtract(loc, pivot);
            return project(pivot, moveDir, 0.8 * max(rc.getMapWidth(), rc.getMapHeight()));
        }
    }

    public static MapLocation genPassiveTarget(Random rng) {
        while (true) {
            int x = rng.nextInt(0, rc.getMapWidth() - 1);
            int y = rng.nextInt(0, rc.getMapHeight() - 1);
            MapLocation pivot = rc.getLocation();
            if (new MapLocation(x, y).distanceSquaredTo(pivot) < 9) {
                continue;
            }
            MapLocation moveDir = subtract(new MapLocation(x, y), pivot);
            return project(pivot, moveDir, 0.5 * max(rc.getMapWidth(), rc.getMapHeight()));
        }
    }

    public static List<MapLocation> getEnemySpawnList(Random rng) throws GameActionException {
        List<MapLocation> locs = guessEnemyLocs(origin);
        return locs;
    }
}
