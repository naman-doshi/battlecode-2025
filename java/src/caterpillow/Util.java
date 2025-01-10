package caterpillow;

import battlecode.common.Direction;
import battlecode.common.RobotInfo;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

import static caterpillow.Game.*;

public class Util {
    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };
    public static final Random rng = new Random();

    public static RobotInfo getNearest(Predicate<RobotInfo> pred) {
        RobotInfo best = null;
        for (RobotInfo bot : rc.senseNearbyRobots()) {
            if (pred.test(bot)) {
                if (best == null || best.getLocation().distanceSquaredTo(rc.getLocation()) < bot.location.distanceSquaredTo(rc.getLocation())) {
                    best = bot;
                }
            }
        }
        return best;
    }
}
