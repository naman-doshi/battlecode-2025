package caterpillow.util;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.packet.packets.InitPacket;

import static caterpillow.Game.*;
import static caterpillow.util.Util.println;
import static java.lang.Math.max;

import static java.lang.Math.min;

public class TowerTracker {
    public static final int MAX_TOWER_BITS = 5;
    public static final int MAX_SRP_BITS = 8;

    // if this is true, pretend its values are garbage
    public static boolean broken = false;
    public static int prevTowers, curTowers;
    public static int lastTowerChange = 0;
    public static int px = 0, x = 0; // last known coins
    public static int coinTowers = 0;
    public static int processedTicks = 0;
    public static int blindTicks = 0;
    public static boolean hasReceivedInitPacket = false;
    public static int srps = 0;

    public static void sendInitPacket(MapLocation loc) throws GameActionException {
        if (broken) {
            pm.send(loc, new InitPacket(origin, 0, 0));
        } else {
            pm.send(loc, new InitPacket(origin, srps, coinTowers));
        }
    }

    public static void sendInitPacket(RobotInfo info) throws GameActionException {
        sendInitPacket(info.getLocation());
    }

    public static int minGain() {
        return coinTowers * 20;
    }

    public static int probablyMinCoinTowers() {
        return max(0, coinTowers - (blindTicks + 2) / 3);
    }
    public static int probablyMaxCoinTowers() {
        return min(rc.getNumberTowers(), coinTowers + 1 + (blindTicks + 2) / 3);
    }
    public static int probablyMinSRP() { return max(0, srps - blindTicks); }
    public static int probablyMaxSRP() {
        return srps + 1 + blindTicks;
    }
    public static int probablyMinGain() {
        return probablyMinCoinTowers() * 20 + probablyMinSRP() * 3;
    }

    public static void runTick() {
        prevTowers = curTowers;
        curTowers = rc.getNumberTowers();
        coinTowers = min(coinTowers, curTowers);
        if (prevTowers != curTowers) {
            lastTowerChange = time;
        }

//        rc.setIndicatorString("paint: " +   (rc.getNumberTowers() - coinTowers) + ", coin: " + coinTowers + ", srps: " + srps + ", broken: " + broken);
        if (blindTicks >= 10) {
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
            for (int potSRP = probablyMinSRP(); potSRP <= probablyMaxSRP(); potSRP++) {
                int salary = 20 + 3 * potSRP;
                for (int pot = probablyMinCoinTowers(); pot <= probablyMaxCoinTowers(); pot++) {
                    if (pot * salary == x - px) {
                        coinTowers = pot;
                        srps = potSRP;
                        blindTicks = 0;
                        return;
                    }
                }
            }
        }
    }
}
