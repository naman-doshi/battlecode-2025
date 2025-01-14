package caterpillow_v1.robot.towers.spawner;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow_v1.Game;
import caterpillow_v1.robot.towers.Tower;

public abstract class Spawner {
    Tower bot;
    public Spawner() {
        bot = (Tower) Game.bot;
    }
    // returns whether complete
    public abstract boolean spawn() throws GameActionException;
}
