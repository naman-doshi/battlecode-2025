package caterpillow_v1;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import caterpillow_v1.packet.PacketManager;
import caterpillow_v1.robot.Robot;
import static caterpillow_v1.util.Util.getNearestRobot;
import static caterpillow_v1.util.Util.isFriendly;

import caterpillow_v1.util.TowerTracker;
import caterpillow_v1.world.GameStage;

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
    // this is for when we actually actually want random
    public static Random trng;

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
                origin = rc.getLocation();
                TowerTracker.coinTowers = 1;
                TowerTracker.hasReceivedInitPacket = true;
            } else {
                RobotInfo nearest = getNearestRobot(r -> isFriendly(r) && r.getType().isTowerType());
                assert nearest != null;
                origin = nearest.getLocation();
            }
        }
        centre = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
        gameStage = GameStage.EARLY;
        seed = 0;
        lastPainted = new HashMap<>();
    }

    public static void postInit() throws GameActionException {
    }

    public static void upd() {
        if (time - TowerTracker.lastTowerChange > 50) {
            gameStage = GameStage.MID;
        }
    }
}
