package caterpillow.robot.towers.spawner.soldier;

import static caterpillow.Game.*;

public class EarlyScoutSpawner extends ScoutSpawner {
    @Override
    public boolean shouldSpawn() {
        return super.shouldSpawn() || rc.getPaint() == rc.getType().paintCapacity;
    };
}
