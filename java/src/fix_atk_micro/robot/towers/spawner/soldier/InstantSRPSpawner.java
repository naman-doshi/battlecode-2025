package fix_atk_micro.robot.towers.spawner.soldier;

public class InstantSRPSpawner extends SRPSpawner {
    InstantSRPSpawner() {
        super();
    }
    @Override
    public boolean shouldSpawn() {
        return true;
    }
}
