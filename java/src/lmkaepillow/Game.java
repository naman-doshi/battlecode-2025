package lmkaepillow;

import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import lmkaepillow.packet.PacketManager;
import lmkaepillow.robot.Robot;
import static lmkaepillow.util.Util.getNearestCell;
import lmkaepillow.world.GameStage;

public class Game {
    public static int time = 0;
    public static RobotController rc;
    public static PacketManager pm;
    public static Robot bot;
    public static MapLocation origin, centre;
    public static boolean isStarter;
    public static GameStage gameStage = GameStage.EARLY;
    public static int seed = 0;
    public static Random rng;

    private static int midTime, lateTime;

    // this is called *before* the robot object is instantiated
    public static void preInit() throws GameActionException {
        rng = new Random(rc.getID());
        if (rc.getType().isTowerType()) {
            MapInfo cell = getNearestCell(c -> c.getPaint().isAlly());
            isStarter = (cell == null);
            origin = rc.getLocation();
        } else {
            isStarter = !rc.senseMapInfo(rc.getLocation()).getPaint().isAlly();
        }
        midTime = 200 + (rc.getMapWidth() * rc.getMapHeight()) / 6; // random ass formula
        lateTime = midTime + (rc.getMapWidth() * rc.getMapHeight()) / 6;
        centre = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
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
