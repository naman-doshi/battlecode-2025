package caterpillow.robot.towers.spawner.soldier;

public class InstantSRPSpawner extends SRPSpawner {
    public InstantSRPSpawner() {
        super();
    }
    @Override
    public boolean shouldSpawn() {
        return true;
    }
}
