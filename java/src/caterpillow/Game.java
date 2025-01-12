package caterpillow;

import battlecode.common.*;
import caterpillow.packet.PacketManager;
import caterpillow.robot.Robot;
import caterpillow.world.GameStage;

import java.util.Random;

import static caterpillow.util.Util.*;

public class Game {
    public static int time;
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

    private static int midTime, lateTime;

    // this is called *before* the robot object is instantiated
    public static void preInit() throws GameActionException {
        Game.pm = new PacketManager();
        trng = new Random(rc.getID() ^ time);
        symmetry = -1;
        isStarter = (time <= 10);
        if (isStarter) {
            if (rc.getType().isTowerType()) {
                origin = rc.getLocation();
            } else {
                RobotInfo nearest = getNearestRobot(r -> isFriendly(r) && r.getType().isTowerType());
                assert nearest != null;
                origin = nearest.getLocation();
            }
        }
        midTime = 200 + (rc.getMapWidth() * rc.getMapHeight()) / 6; // random ass formula
        lateTime = midTime + (rc.getMapWidth() * rc.getMapHeight()) / 6;
        centre = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
        gameStage = GameStage.EARLY;
        seed = 0;
    }

    public static void postInit() throws GameActionException {
    }

    public static void upd() {
        if (time == midTime) {
            gameStage = GameStage.MID;
        }
        if (time == lateTime) {
            gameStage = GameStage.LATE;
        }
    }
}
