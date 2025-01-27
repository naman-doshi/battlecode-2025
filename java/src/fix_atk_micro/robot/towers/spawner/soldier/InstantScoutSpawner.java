package fix_atk_micro.robot.towers.spawner.soldier;

public class InstantScoutSpawner extends ScoutSpawner {
    public InstantScoutSpawner() {
        super();
    }
    @Override
    public boolean shouldSpawn() {
        return true;
    }
}
