package caterpillow.robot.troll;

import battlecode.common.GameActionException;
import caterpillow.robot.Strategy;

import java.util.ArrayDeque;
import java.util.Queue;

public class QueueStrategy extends Strategy {

    public Queue<Strategy> todo;

    public void push(Strategy strat) {
        todo.add(strat);
    }

    public QueueStrategy() {
        todo = new ArrayDeque<>();
    }

    @Override
    public boolean isComplete() throws GameActionException {
        while (!todo.isEmpty() && todo.peek().isComplete()) {
            todo.poll();
        }
        return todo.isEmpty();
    }

    @Override
    public void runTick() throws GameActionException {
        assert !todo.isEmpty();
        todo.peek().runTick();
    }
}
