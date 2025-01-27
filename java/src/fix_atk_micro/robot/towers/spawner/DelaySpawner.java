package fix_atk_micro.robot.towers.spawner;

import battlecode.common.GameActionException;

import static fix_atk_micro.Game.ticksExisted;

public class DelaySpawner extends Spawner {
    int delay;

    public DelaySpawner(int delay) {
        this.delay = delay;
    }

    @Override
    public boolean spawn() throws GameActionException {
        return ticksExisted > delay;
    }
}
