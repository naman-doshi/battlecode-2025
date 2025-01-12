package caterpillow.robot.troll;

import battlecode.common.GameActionException;
import caterpillow.robot.Strategy;

import java.util.Stack;

public abstract class StackableStrategy extends Strategy {

    public Stack<Strategy> stack;

    void stackStrat(Strategy strat) {
        stack.push(strat);
    }

    public StackableStrategy() {
        stack = new Stack<>();
    }

    @Override
    public boolean isComplete() throws GameActionException {
        while (!stack.isEmpty() && stack.peek().isComplete()) {
            stack.pop();
        }
        if (!stack.isEmpty()) {
            return false;
        }
        return isBaseComplete();
    }

    public abstract boolean isBaseComplete();

    @Override
    public void runTick() throws GameActionException {
        if (!stack.isEmpty()) {
            stack.peek().runTick();
        } else {
            runBaseTick();
        }
    }

    public abstract void runBaseTick() throws GameActionException;
}
