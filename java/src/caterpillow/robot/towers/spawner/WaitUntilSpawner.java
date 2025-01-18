package caterpillow.robot.towers.spawner;

import battlecode.common.GameActionException;
import caterpillow.util.GameSupplier;

public class WaitUntilSpawner extends Spawner {

    GameSupplier<Boolean> condition;

    public WaitUntilSpawner(GameSupplier<Boolean> condition) {
        this.condition = condition;
    }

    @Override
    public boolean spawn() throws GameActionException {
        return condition.get();
    }
}
