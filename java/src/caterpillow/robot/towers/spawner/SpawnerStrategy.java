package caterpillow.robot.towers.spawner;

import battlecode.common.GameActionException;
import caterpillow.robot.towers.TowerStrategy;

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
        while (!todo.isEmpty() && todo.peek().spawn()) {
            todo.remove();
        }
        assert !todo.isEmpty() : "ran out of things to spawn";
    }
}
