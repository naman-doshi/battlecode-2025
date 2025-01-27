package fix_atk_micro.tracking;

import java.util.ArrayList;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import static fix_atk_micro.Game.origin;
import static fix_atk_micro.Game.pm;
import static fix_atk_micro.Game.rc;
import static fix_atk_micro.util.Util.println;
import static java.lang.Math.*;

import fix_atk_micro.packet.packets.InitPacket;
import fix_atk_micro.util.GameBinaryOperator;
import fix_atk_micro.util.GamePredicate;
import fix_atk_micro.util.Tuple;

public class TowerTracker {
    public static final int MAX_TOWER_BITS = 5;
    public static final int MAX_SRP_BITS = 8;

    // if this is true, pretend its values are garbage
    public static boolean broken = false;
    public static int curTowers;
    public static int peakTowers = 0;
    public static int lastTowerChange = 0;
    public static int px = 0, x = 0; // last known coins
    public static int coinTowers = 0;
    public static boolean hasStarterCoinDied;
    public static int processedTicks = 0;
    public static int blindTicks = 0;
    public static boolean hasReceivedInitPacket = false;
    public static int srps = 0;

    public static ArrayList<MapLocation> paintLocs, nonPaintLocs;
    public static ArrayList<MapLocation> enemyLocs;

    public static void init() {
        paintLocs = new ArrayList<>();
        nonPaintLocs = new ArrayList<>();
        enemyLocs = new ArrayList<>();
    }

    public static MapLocation getNearestFriendlyPaintTowerGlobal(GamePredicate<MapLocation> pred) throws GameActionException {
        if (paintLocs.isEmpty()) {
            return null;
        }
        MapLocation best = null;
        MapLocation cur = rc.getLocation();
        for (int i = paintLocs.size() - 1; i >= 0; i--) {
            MapLocation cand = paintLocs.get(i);
            if (pred.test(cand)) {
                if (best == null || best.distanceSquaredTo(cur) > cand.distanceSquaredTo(cur)) {
                    best = cand;
                }
            }
        }
        return best;
    }

    public static MapLocation getNearestFriendlyNonPaintTowerGlobal(GamePredicate<MapLocation> pred) throws GameActionException {
        if (nonPaintLocs.isEmpty()) {
            return null;
        }
        MapLocation best = null;
        MapLocation cur = rc.getLocation();
        for (int i = nonPaintLocs.size() - 1; i >= 0; i--) {
            MapLocation cand = nonPaintLocs.get(i);
            if (pred.test(cand)) {
                if (best == null || best.distanceSquaredTo(cur) > cand.distanceSquaredTo(cur)) {
                    best = cand;
                }
            }
        }
        return best;
    }

    public static MapLocation getNearestFriendlyTowerGlobal(GamePredicate<MapLocation> pred) throws GameActionException {
        MapLocation best = null;
        MapLocation cur = rc.getLocation();
        for (int i = paintLocs.size() - 1; i >= 0; i--) {
            MapLocation cand = paintLocs.get(i);
            if (pred.test(cand)) {
                if (best == null || best.distanceSquaredTo(cur) > cand.distanceSquaredTo(cur)) {
                    best = cand;
                }
            }
        }
        for (int i = nonPaintLocs.size() - 1; i >= 0; i--) {
            MapLocation cand = nonPaintLocs.get(i);
            if (pred.test(cand)) {
                if (best == null || best.distanceSquaredTo(cur) > cand.distanceSquaredTo(cur)) {
                    best = cand;
                }
            }
        }
        return best;
    }

    public static MapLocation getNearestEnemyTowerGlobal(GamePredicate<RobotInfo> pred) throws GameActionException {
        RobotInfo best = null;
        MapLocation cur = rc.getLocation();
        for (int i = enemyLocs.size() - 1; i >= 0; i--) {
            MapLocation loc = enemyLocs.get(i);
            RobotInfo info = rc.senseRobotAtLocation(loc);
            if (info != null && pred.test(info)) {
                if (best == null || best.getLocation().distanceSquaredTo(cur) > info.getLocation().distanceSquaredTo(cur)) {
                    best = info;
                }
            }
        }
        return best == null ? null : best.getLocation();
    }

    public static RobotInfo getNearestVisibleTower(GamePredicate<RobotInfo> pred) throws GameActionException {
        RobotInfo best = null;
        for (int i = CellTracker.nearbyRuins.length - 1; i >= 0; i--) {
            MapLocation loc = CellTracker.nearbyRuins[i];
            RobotInfo info = rc.senseRobotAtLocation(loc);
            if (info != null && pred.test(info)) {
                if (best == null || best.getLocation().distanceSquaredTo(rc.getLocation()) > info.getLocation().distanceSquaredTo(rc.getLocation())) {
                    best = info;
                }
            }
        }
        return best;
    }

    public static void sendInitPacket(MapLocation loc) throws GameActionException {
        if (broken) {
            pm.send(loc, new InitPacket(origin, 0, 0, 0));
        } else {
            pm.send(loc, new InitPacket(origin, srps, coinTowers, hasStarterCoinDied ? 1 : 0));
        }
    }

    public static void sendInitPacket(RobotInfo info) throws GameActionException {
        sendInitPacket(info.getLocation());
    }

    public static int minGain() {
        return coinTowers * 20 + 10;
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

    public static void runTick() throws GameActionException {
        curTowers = rc.getNumberTowers();
        coinTowers = min(coinTowers, curTowers);

        if (curTowers > peakTowers) {
            lastTowerChange = rc.getRoundNum();
            peakTowers = curTowers;
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
            Tuple<Integer, Integer, Integer> bestSol = null;

            GameBinaryOperator<Tuple<Integer, Integer, Integer>> comp = (a, b) -> {
                if (a == null) return b;
                int score1 = abs(a.first - coinTowers) + abs(a.second - srps) + 2 * abs(a.third - (hasStarterCoinDied ? 1 : 0));
                int score2 = abs(b.first - coinTowers) + abs(b.second - srps) + 2 * abs(b.third - (hasStarterCoinDied ? 1 : 0));
                if (score1 < score2) return a;
                else return b;
            };

            if (!hasStarterCoinDied) {
                for (int potSRP = probablyMinSRP(); potSRP <= probablyMaxSRP(); potSRP++) {
                    int salary = 20 + 3 * potSRP;
                    for (int pot = probablyMinCoinTowers(); pot <= probablyMaxCoinTowers(); pot++) {
                        if (pot * salary + 10 == x - px) {
                            bestSol = comp.apply(bestSol, new Tuple<Integer, Integer, Integer>(pot, potSRP, 0));
                        }
                    }
                }
            }
            for (int potSRP = probablyMinSRP(); potSRP <= probablyMaxSRP(); potSRP++) {
                int salary = 20 + 3 * potSRP;
                for (int pot = probablyMinCoinTowers(); pot <= probablyMaxCoinTowers(); pot++) {
                    if (pot * salary == x - px) {
                        bestSol = comp.apply(bestSol, new Tuple<Integer, Integer, Integer>(pot, potSRP, 1));
                    }
                }
            }
            if (bestSol != null) {
                coinTowers = bestSol.first;
                srps = bestSol.second;
                hasStarterCoinDied = (bestSol.third == 1);
                blindTicks = 0;
            }
        }
    }
}
