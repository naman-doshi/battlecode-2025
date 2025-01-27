package fix_atk_micro.robot.towers.spawner;

import battlecode.common.GameActionException;
import fix_atk_micro.util.GameSupplier;

public class ConditionalSpawner extends Spawner {

    GameSupplier<Boolean> condition;
    Spawner spawner1, spawner2;

    public ConditionalSpawner(GameSupplier<Boolean> condition, Spawner spawner1, Spawner spawner2) {
        this.condition = condition;
        this.spawner1 = spawner1;
        this.spawner2 = spawner2;
    }

    @Override
    public boolean spawn() throws GameActionException {
        if (condition.get()) {
            return spawner1.spawn();
        } else {
            return spawner2.spawn();
        }
    }
}
