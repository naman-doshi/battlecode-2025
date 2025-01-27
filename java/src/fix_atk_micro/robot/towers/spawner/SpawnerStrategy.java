package fix_atk_micro.robot.towers.spawner;

import battlecode.common.GameActionException;
import fix_atk_micro.robot.towers.TowerStrategy;
import fix_atk_micro.tracking.TowerTracker;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class SpawnerStrategy extends TowerStrategy {

    Queue<Spawner> todo;

    public SpawnerStrategy(Spawner... spawners) {
        todo = new LinkedList<>();
        todo.addAll(Arrays.asList(spawners));
    }

    @Override
    public void runTick() throws GameActionException {
        // don't spawn stuff if we have no coin towers
        while ((TowerTracker.broken || TowerTracker.coinTowers > 0) && !todo.isEmpty() && todo.peek().spawn()) {
            todo.remove();
        }
        assert !todo.isEmpty() : "ran out of things to spawn";
    }
}
