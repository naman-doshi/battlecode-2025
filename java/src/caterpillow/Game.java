package caterpillow;

import static java.lang.Math.min;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import caterpillow.packet.PacketManager;
import caterpillow.robot.Robot;
import caterpillow.util.TowerTracker;
import static caterpillow.util.Util.getNearestRobot;
import static caterpillow.util.Util.isFriendly;
import caterpillow.world.GameStage;

public class Game {
    public static int time;
    public static int ticksExisted; // first runTick = 0
    public static RobotController rc;
    public static PacketManager pm;
    public static Robot bot;
    public static MapLocation origin, centre;
    public static boolean isStarter;
    public static GameStage gameStage;
    public static int seed;
    public static int spawnCoinPenalty;
    // this is for when we actually actually want random
    public static Random trng;
    public static int MAX_MAP_SIZE = 60;

    public static int symmetry; // -1 = unknown, 0 = rotational, 1 = hor, 2 = ver
    public static Map<Integer, Integer> lastPainted;

    // this is called *before* the robot object is instantiated
    public static void preInit() throws GameActionException {
        Game.pm = new PacketManager();
        trng = new Random(rc.getID() ^ time);
        symmetry = -1;
        isStarter = (time <= 10);
        if (isStarter) {
            if (rc.getType().isTowerType()) {
                Game.origin = rc.getLocation();
                TowerTracker.coinTowers = 1;
                TowerTracker.hasReceivedInitPacket = true;
            } else {
                // we shouldnt need this
//                RobotInfo nearest = getNearestRobot(r -> isFriendly(r) && r.getType().isTowerType());
//                assert nearest != null;
//                Game.origin = nearest.getLocation();
            }
        }
        centre = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
        gameStage = GameStage.EARLY;
        seed = 0;
        lastPainted = new HashMap<>();
    }

    public static void postInit() throws GameActionException {
    }

    // can change
    public static void upd() {

        // update our spawn "penalty" too
        int coinTowers = TowerTracker.probablyMinCoinTowers();
        // if early/early mid: all level 1, if mid all l2, if late all l3
        if (time - TowerTracker.lastTowerChange > 50 && rc.getNumberTowers() > 2) {
            gameStage = GameStage.MID;
            spawnCoinPenalty = min(0, 1000 - coinTowers * 30 * 3);
        }

    }
}
