package caterpillow_v1.robot.towers.spawner;

import battlecode.common.GameActionException;

import java.util.Arrays;
import java.util.LinkedList;

public class LoopedSpawner extends Spawner {

    LinkedList<Spawner> todo;
    public LoopedSpawner(Spawner... stuff) {
        super();
        todo = new LinkedList<>();
        todo.addAll(Arrays.asList(stuff));
    }

    @Override
    public boolean spawn() throws GameActionException {
        while (todo.peek().spawn()) {
            todo.add(todo.peek());
            todo.remove();
        }
        return false;
    }
}
