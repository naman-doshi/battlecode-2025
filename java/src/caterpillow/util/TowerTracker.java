package caterpillow.util;

import battlecode.common.UnitType;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;
import static java.lang.Math.max;

public class TowerTracker {
    public static final int MAX_TOWER_BITS = 4;

    // if this is true, pretend its values are garbage
    public static boolean broken = false;

    public static double targetRatio = 0.5; // for now, build equal amounts
    public static UnitType getNextType() {
        if (!broken) {
            if ((double) (totTowers - coinTowers) / (double) coinTowers < targetRatio) {
                return UnitType.LEVEL_ONE_PAINT_TOWER;
            } else {
                return UnitType.LEVEL_ONE_MONEY_TOWER;
            }
        } else {
            if (trng.nextBoolean()) {
                return UnitType.LEVEL_ONE_PAINT_TOWER;
            } else {
                return UnitType.LEVEL_ONE_MONEY_TOWER;
            }
        }
    }

    public static int px = 0, x = 0; // last known coins
    public static int totTowers = 0, coinTowers = 0;
    public static int processedTicks = 0;
    public static int blindTicks = 0;
    public static boolean hasReceivedInitPacket = false;

    public static int minGain() {
        return coinTowers * 20;
    }

    public static int probablyMinCoinTowers() {
        return max(0, coinTowers - (blindTicks + 2) / 3);
    }

    public static int probablyMinGain() {
        return probablyMinCoinTowers() * 20;
    }

    public static void runTick() {
        if (time % 20 == 0) {
            println("paint: " + (totTowers - coinTowers) + ", coin: " + coinTowers + ", broken: " + broken);
        }
//        rc.setIndicatorString("paint: " + (totTowers - coinTowers) + " coin: " + coinTowers + " broken: " + broken);
        if (totTowers >= 16) {
            broken = true;
        }
        if (broken) {
            return;
        }

        processedTicks++;
        px = x;
        x = rc.getChips();

        if (!hasReceivedInitPacket) {
            return;
        }
        blindTicks++;

        if (processedTicks == 1) {
            return;
        }

        if (x - px >= probablyMinGain()) {
            boolean foundSolution = false;
            for (int salary = 20; salary < 50; salary += 3) {
                if (foundSolution) {
                    break;
                }
                for (int pot = probablyMinCoinTowers(); pot <= totTowers; pot++) {
                    if (pot * salary == x - px) {
                        foundSolution = true;
                        coinTowers = pot;
                        break;
                    }
                }
            }
            if (!foundSolution) {
                broken = true;
            }
            blindTicks = 0;
        } else {
            totTowers += max(0, ((px - x - 600) + 999) / 1000);
        }
    }
}
