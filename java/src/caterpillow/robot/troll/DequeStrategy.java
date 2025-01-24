package caterpillow.robot.troll;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;

import battlecode.common.GameActionException;
import caterpillow.robot.Strategy;

public class DequeStrategy extends Strategy {

    public Deque<Strategy> todo;

    public void push_front(Strategy strat) {
        todo.addFirst(strat);
    }

    public void push_back(Strategy strat) {
        todo.addLast(strat);
    }

    public DequeStrategy() {
        todo = new ArrayDeque<>();
    }

    @Override
    public boolean isComplete() throws GameActionException {
        while (!todo.isEmpty()) {
            Iterator<Strategy> it = todo.iterator();
            Strategy first = it.next();
            if (!first.isComplete()) break;
            else it.remove();
        }
        return todo.isEmpty();
    }

    @Override
    public void runTick() throws GameActionException {
        assert !todo.isEmpty();
        // allow fallthrough
        todo.peek().runTick();
        while (!todo.isEmpty()) {
            Iterator<Strategy> it = todo.iterator();
            Strategy first = it.next();
            first.runTick();
            if (todo.size() == 1 || !first.isComplete()) break;
            else it.remove();
        }
    }
}
