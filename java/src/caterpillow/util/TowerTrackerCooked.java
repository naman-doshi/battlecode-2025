//package caterpillow.util;
//
//import battlecode.common.UnitType;
//
//import static caterpillow.util.Util.*;
//import static caterpillow.Game.*;
//import static java.lang.Math.max;
//
//public class TowerTracker {
//    public static int px = 0, x = 0; // last known coins
//    public static int expectedSalary;
//    public static int totTowers = 0, coinTowers = 0;
//    public static int processedTicks = 0;
//
//    public static boolean shouldIEvenBotherTryingToTrackThis() {
//        return totTowers < 16;
//    }
//
//    public static double targetRatio = 1; // for now, build equal amounts
//    public static UnitType getNextType() {
//        if (shouldIEvenBotherTryingToTrackThis()) {
//            if ((double) (totTowers - coinTowers) / (double) coinTowers < targetRatio) {
//                return UnitType.LEVEL_ONE_PAINT_TOWER;
//            } else {
//                return UnitType.LEVEL_ONE_MONEY_TOWER;
//            }
//        } else {
//            if (trng.nextBoolean()) {
//                return UnitType.LEVEL_ONE_PAINT_TOWER;
//            } else {
//                return UnitType.LEVEL_ONE_MONEY_TOWER;
//            }
//        }
//    }
//
//    public static int expectedCoin() {
//        return px + expectedSalary;
//    }
//
//    public static int minGain() {
//        return coinTowers * 20;
//    }
//
//    public static int probablyMinGain() {
//        return max(0, coinTowers - 1) * 20;
//    }
//
//    public static Pair<Integer, Integer> guessSpendsBlind() {
//        if (x - px > probablyMinGain()) {
//            return new Pair<>(0, 0);
//        }
//        // assume only soldiers and towers
//        int newTowers = 0, newSoldiers = 0;
//
//        while (px + probablyMinGain() - x - 1000 * newTowers > 600) {
//            // assume built tower
//            newTowers++;
//        }
//
//        while (x + 1000 * newTowers + 250 * newSoldiers - px < probablyMinGain()) {
//            newSoldiers++;
//        }
//        return new Pair<>(newTowers, newSoldiers);
//    }
//
//    /*
//
//    - srps add +3 for every coin tower
//    - building agents uses a minimum of 250 money, and is always a multiple or 50
//    - building towers uses 1000 money
//
//    - try not to build too many agents at the same time
//
//    */
//
//    public void runTick() {
//        if (!shouldIEvenBotherTryingToTrackThis()) {
//            return;
//        }
//        processedTicks++;
//
//        if (processedTicks == 1) {
//            x = rc.getChips();
//            // pray nothing weird happened
//            return;
//        } else if (processedTicks == 2) {
//            px = x;
//            x = rc.getChips();
//
//            int dx = x - px;
//            dx += guessTroopSpend();
//            return;
//        }
//
//        px = x;
//        x = rc.getChips();
//
//        // account for chip usage
//    }
//}
