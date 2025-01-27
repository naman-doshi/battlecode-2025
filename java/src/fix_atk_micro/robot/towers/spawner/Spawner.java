package fix_atk_micro.robot.towers.spawner;

import battlecode.common.GameActionException;
import fix_atk_micro.Game;
import fix_atk_micro.robot.towers.Tower;

public abstract class Spawner {
    public Tower bot;
    public Spawner() {
        bot = (Tower) Game.bot;
    }
    // returns whether complete
    public abstract boolean spawn() throws GameActionException;
}
