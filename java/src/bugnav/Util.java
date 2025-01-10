package bugnav;

import battlecode.common.Direction;
import battlecode.common.RobotController;

import java.util.Random;

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
    public static Random rng;
}
