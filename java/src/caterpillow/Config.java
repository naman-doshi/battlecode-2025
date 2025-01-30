package caterpillow;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import java.util.List;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import static battlecode.common.UnitType.MOPPER;
import static caterpillow.Game.centre;
import static caterpillow.Game.mapHeight;
import static caterpillow.Game.mapWidth;
import static caterpillow.Game.origin;
import static caterpillow.Game.rc;
import static caterpillow.Game.trng;
import static caterpillow.tracking.CellTracker.getNearestCell;
import caterpillow.tracking.TowerTracker;
import caterpillow.util.CustomRandom;
import static caterpillow.util.Util.getPaintLevel;
import static caterpillow.util.Util.guessEnemyLocs;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.logisticSample;
import static caterpillow.util.Util.project;
import static caterpillow.util.Util.subtract;

public class Config {
    // idea : dynamically update this based on coin amt
    // right now, we have too much paint in the endgame (when most towers are maxed)
    // we also need more chips on larger maps

    public static boolean shouldHaveSuicidalMoneyTowers() {
        return mapHeight * mapWidth <= 1500;
    }

    public static double targetRatio() {
        if(rc.getNumberTowers() < moneyTowerThreshold()) return 1;
        if (shouldHaveSuicidalMoneyTowers()) return 0.9;
        //if (rc.getNumberTowers() == moneyTowerThreshold()) return 0;
        int area = mapHeight * mapWidth;
        double ratio = area < 1500 ? 0.63 : 0.68;
        //double ratio = 0.66;
        return ratio;
    }
    public static int moneyTowerThreshold() {
        // if(mapHeight * mapWidth <= 900) return 3;
        if(mapHeight * mapWidth <= 1500) return 4;
        return 5;
    }

    public static boolean canUpgrade(int level) {
        if (shouldHaveSuicidalMoneyTowers()) return false;
        if (level == 2) {
            return rc.getChips() >= 2500;
        } else if (level == 3) {
            return rc.getChips() >= 5000;
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

    public static boolean isCentral(MapLocation loc) {
        return loc.distanceSquaredTo(centre) <= mapHeight * mapWidth / 25;
    }

    public static UnitType nextTowerType(MapLocation loc) throws GameActionException {

        if (shouldHaveSuicidalMoneyTowers()) return UnitType.LEVEL_ONE_MONEY_TOWER;

        if(isCentral(loc) && rc.getNumberTowers() > 6 && trng.nextInt(2) == 0 && getNearestCell(c -> c.getPaint().isEnemy()) != null) {
            return UnitType.LEVEL_ONE_DEFENSE_TOWER;
        }

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

        if (shouldHaveSuicidalMoneyTowers()) {
            return UnitType.LEVEL_ONE_MONEY_TOWER;
        }
        
        if (!TowerTracker.broken) {
            System.out.println("i have " + TowerTracker.coinTowers + " coin towers and my ratio is " + (double) TowerTracker.coinTowers / (double) rc.getNumberTowers());
            double currentRatio = (double) TowerTracker.coinTowers / (double) rc.getNumberTowers();
            if(abs(currentRatio - targetRatio()) >= 0.05) deterministic = true;
            if ((deterministic ? currentRatio > targetRatio() : logisticSample(currentRatio - targetRatio(), 10)) || (rc.getChips() >= 3000 && !shouldHaveSuicidalMoneyTowers())) {
                System.out.println("paint tower");
                return UnitType.LEVEL_ONE_PAINT_TOWER;
            } else {
                System.out.println("money tower");
                return UnitType.LEVEL_ONE_MONEY_TOWER;
            }
        } else {
            System.out.println("BROKEN");
            if (trng.nextDouble() > targetRatio() || (rc.getChips() >= 3000 && !shouldHaveSuicidalMoneyTowers())) {
                return UnitType.LEVEL_ONE_PAINT_TOWER;
            } else {
                return UnitType.LEVEL_ONE_MONEY_TOWER;
            }
        }
    }
    public static UnitType nextResourceType() {
        return nextResourceType(false);
    }

    public static MapLocation genExplorationTarget(CustomRandom rng) {
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

    public static MapLocation genAggroTarget(CustomRandom rng) {
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

    public static MapLocation genPassiveTarget(CustomRandom rng) {
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

    public static List<MapLocation> getEnemySpawnList(CustomRandom rng) throws GameActionException {
        List<MapLocation> locs = guessEnemyLocs(origin);
        return locs;
    }

    public static boolean shouldConvertMoneyToPaint() {
        return rc.getChips() >= 15000;
    }
}
