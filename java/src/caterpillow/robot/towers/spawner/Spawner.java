package caterpillow.robot.towers.spawner;

import battlecode.common.GameActionException;
import caterpillow.Game;
import caterpillow.robot.towers.Tower;

public abstract class Spawner {
    public Tower bot;
    public Spawner() {
        bot = (Tower) Game.bot;
    }
    // returns whether complete
    public abstract boolean spawn() throws GameActionException;
}
