package caterpillow.robot.agents;

import battlecode.common.GameActionException;
import caterpillow.robot.Strategy;

import java.util.function.Supplier;

public class RecursiveStrategy extends Strategy {
    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    public Strategy cur;
    Supplier<Strategy> factory;

    public RecursiveStrategy(Supplier<Strategy> factory) {
        this.factory = factory;
        cur = factory.get();
    }

    @Override
    public void runTick() throws GameActionException {
        if (cur.isComplete()) {
            cur = factory.get();
        }
        cur.runTick();
    }
}
