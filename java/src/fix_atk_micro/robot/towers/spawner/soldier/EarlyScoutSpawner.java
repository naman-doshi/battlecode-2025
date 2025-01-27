package fix_atk_micro.robot.towers.spawner.soldier;

import static fix_atk_micro.Game.*;

public class EarlyScoutSpawner extends ScoutSpawner {
    @Override
    public boolean shouldSpawn() {
        return super.shouldSpawn() || rc.getPaint() == rc.getType().paintCapacity;
    };
}
