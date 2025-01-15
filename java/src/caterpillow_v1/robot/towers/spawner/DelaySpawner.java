package caterpillow_v1.robot.towers.spawner;

import battlecode.common.GameActionException;

import static caterpillow_v1.Game.ticksExisted;

// HIGHLY ILLEGAL STATEFUL SPAWNER
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
