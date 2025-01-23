package caterpillow;

import static java.lang.Math.max;
import java.util.List;
import java.util.Random;

import battlecode.common.*;
import static battlecode.common.UnitType.MOPPER;
import static caterpillow.Game.*;
import caterpillow.tracking.TowerTracker;
import static caterpillow.util.Util.*;

public class Config {
    // idea : dynamically update this based on coin amt
    // right now, we have too much paint in the endgame (when most towers are maxed)
    // we also need more chips on larger maps
    public static double targetRatio() {
        if(rc.getNumberTowers() < moneyTowerThreshold()) return 1;
        int area = mapHeight * mapWidth;
        double ratio = area < 1500 ? 0.66 : 0.72;
        return ratio;
    }
    public static int moneyTowerThreshold() {
        if(mapHeight * mapWidth <= 900) return 3;
        if(mapHeight * mapWidth <= 1500) return 4;
        return 5;
    }

    public static boolean canUpgrade(int level) {
        if (level == 2) {
            return rc.getChips() >= 3500;
        } else if (level == 3) {
            return rc.getChips() >= 6500;
        }
        return false;
    }

    public static boolean shouldSRPBuildTower() {
        return rc.getChips() >= 1200;
    }

    public static boolean shouldRescue(RobotInfo b) {
        assert rc.getType().equals(MOPPER);
        if (b.getType() == MOPPER) return false;
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
        if (b.getType() == MOPPER) return false;
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

    public static UnitType nextTowerType() {

       boolean enemyVisible = false;

       for (RobotInfo r : rc.senseNearbyRobots()) {
           if (!isFriendly(r)) {
               enemyVisible = true;
               break;
           }
       }

       if (enemyVisible && rc.getChips() >= 1500 && (double) TowerTracker.coinTowers / (double) rc.getNumberTowers() < targetRatio() + 0.05 && rc.getNumberTowers() >= 4) {
           return UnitType.LEVEL_ONE_DEFENSE_TOWER;
       }

       return nextResourceType();
    }

    public static UnitType nextResourceType(boolean deterministic) {
        if (!TowerTracker.broken) {
            System.out.println("i have " + TowerTracker.coinTowers + " coin towers and my ratio is " + (double) TowerTracker.coinTowers / (double) rc.getNumberTowers());
            double currentRatio = (double) TowerTracker.coinTowers / (double) rc.getNumberTowers();
            if ((deterministic ? currentRatio > targetRatio() : logisticSample(currentRatio - targetRatio(), 10)) || rc.getChips() >= 3000) {
                System.out.println("paint tower");
                return UnitType.LEVEL_ONE_PAINT_TOWER;
            } else {
                System.out.println("money tower");
                return UnitType.LEVEL_ONE_MONEY_TOWER;
            }
        } else {
            System.out.println("BROKEN");
            if (trng.nextDouble() > targetRatio() || rc.getChips() >= 3000) {
                return UnitType.LEVEL_ONE_PAINT_TOWER;
            } else {
                return UnitType.LEVEL_ONE_MONEY_TOWER;
            }
        }
    }
    public static UnitType nextResourceType() {
        return nextResourceType(false);
    }

    public static MapLocation genExplorationTarget(Random rng) {
        while (true) {
            int x = rng.nextInt(0, mapWidth - 1);
            int y = rng.nextInt(0, mapHeight - 1);
            MapLocation pivot = new MapLocation((rc.getLocation().x + origin.x) / 2, (rc.getLocation().y + origin.y) / 2);
            if (new MapLocation(x, y).distanceSquaredTo(rc.getLocation()) < 9 || new MapLocation(x, y).distanceSquaredTo(pivot) < 9) {
                continue;
            }
            MapLocation moveDir = subtract(new MapLocation(x, y), pivot);
            return project(pivot, moveDir, 0.7 * max(mapWidth, mapHeight));
        }
    }

    public static MapLocation genAggroTarget(Random rng) {
        while (true) {
            int x = rng.nextInt(0, mapWidth - 1);
            int y = rng.nextInt(0, mapHeight - 1);
            MapLocation loc = new MapLocation(x, y);
            MapLocation pivot = origin;
            if (loc.distanceSquaredTo(pivot) < 9 || pivot.distanceSquaredTo(loc) < pivot.distanceSquaredTo(centre)) {
                continue;
            }
            MapLocation moveDir = subtract(loc, pivot);
            return project(pivot, moveDir, 0.8 * max(mapWidth, mapHeight));
        }
    }

    public static MapLocation genPassiveTarget(Random rng) {
        while (true) {
            int x = rng.nextInt(0, mapWidth - 1);
            int y = rng.nextInt(0, mapHeight - 1);
            MapLocation pivot = rc.getLocation();
            if (new MapLocation(x, y).distanceSquaredTo(pivot) < 9) {
                continue;
            }
            MapLocation moveDir = subtract(new MapLocation(x, y), pivot);
            return project(pivot, moveDir, 0.5 * max(mapWidth, mapHeight));
        }
    }

    public static List<MapLocation> getEnemySpawnList(Random rng) throws GameActionException {
        List<MapLocation> locs = guessEnemyLocs(origin);
        return locs;
    }

    public static boolean shouldConvertMoneyToPaint() {
        return rc.getChips() >= 15000;
    }
}
