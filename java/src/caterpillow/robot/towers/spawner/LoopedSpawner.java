package caterpillow.robot.towers.spawner;

import battlecode.common.GameActionException;
import caterpillow.util.GameSupplier;

import java.util.Arrays;
import java.util.LinkedList;

public class LoopedSpawner extends Spawner {

    LinkedList<GameSupplier<Spawner>> todo;
    Spawner cur;

    public LoopedSpawner(GameSupplier<Spawner>... stuff) throws GameActionException {
        super();
        todo = new LinkedList<>();
        todo.addAll(Arrays.asList(stuff));
        cur = todo.peek().get();
    }

    @Override
    public boolean spawn() throws GameActionException {
        while (cur.spawn()) {
            todo.add(todo.peek());
            todo.remove();
            cur = todo.peek().get();
        }
        return false;
    }
}
