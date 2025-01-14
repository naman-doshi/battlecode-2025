package caterpillow_v1;

import battlecode.common.MapLocation;
import battlecode.common.UnitType;
import caterpillow_v1.util.TowerTracker;

import java.util.Random;

import static caterpillow_v1.Game.*;
import static caterpillow_v1.util.Util.*;
import static java.lang.Math.max;

public class Config {

    public static double targetRatio = 0.75; // fraction of towers that should be coin

    public static boolean canUpgrade(int level) {
        if (level == 2) {
            if (maxedTowers()) {
                return rc.getChips() >= 4000;
            } else {
                return rc.getChips() >= 5000;
            }
        } else if (level == 3) {
            if (maxedTowers()) {
                return rc.getChips() >= 8000;
            } else {
                return rc.getChips() >= 12000;
            }
        }
        assert false : "wtf";
        return false;
    }

    public static UnitType getNextType() {
        if (!TowerTracker.broken) {
            if ((double) TowerTracker.coinTowers / (double) rc.getNumberTowers() > targetRatio) {
                return UnitType.LEVEL_ONE_PAINT_TOWER;
            } else {
                return UnitType.LEVEL_ONE_MONEY_TOWER;
            }
        } else {
            if (trng.nextDouble() > targetRatio) {
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
            MapLocation pivot = origin;
            if (new MapLocation(x, y).distanceSquaredTo(pivot) < 9) {
                continue;
            }
            MapLocation moveDir = subtract(new MapLocation(x, y), pivot);
            return project(pivot, moveDir, 0.8 * max(rc.getMapWidth(), rc.getMapHeight()));
        }
    }
}
