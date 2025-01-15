package caterpillow.util;

import static java.lang.Math.max;

import static caterpillow.Game.rc;
import static caterpillow.Game.time;

public class TowerTracker {
    public static final int MAX_TOWER_BITS = 4;

    // if this is true, pretend its values are garbage
    public static boolean broken = false;
    public static int prevTowers, curTowers;
    public static int lastTowerChange = 0;
    public static int px = 0, x = 0; // last known coins
    public static int coinTowers = 0;
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
        prevTowers = curTowers;
        curTowers = rc.getNumberTowers();
        if (prevTowers != curTowers) {
            lastTowerChange = time;
        }

//        rc.setIndicatorString("paint: " + (totTowers - coinTowers) + " coin: " + coinTowers + " broken: " + broken);
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
                for (int pot = probablyMinCoinTowers(); pot <= rc.getNumberTowers(); pot++) {
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
        }
    }
}
