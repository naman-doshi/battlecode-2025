package caterpillow.robot.troll;

import battlecode.common.GameActionException;
import caterpillow.robot.Strategy;

import java.util.Stack;

public abstract class StackableStrategy extends Strategy {

    public Strategy secondaryStrategy;

    public StackableStrategy() {
        secondaryStrategy = null;
    }

    protected boolean tryStrategy(Strategy strat) throws GameActionException {
        if (strat.isComplete()) {
            return false;
        }
        strat.runTick();
        if (strat.isComplete()) {
            return false;
        } else {
            secondaryStrategy = strat;
            return true;
        }
    }

    @Override
    public final boolean isComplete() throws GameActionException {
        if (secondaryStrategy != null) {
            if (!secondaryStrategy.isComplete()) {
                return false;
            }
            secondaryStrategy = null;
        }
        return isBaseComplete();
    }

    public abstract boolean isBaseComplete();

    @Override
    public final void runTick() throws GameActionException {
        if (secondaryStrategy != null) {
            secondaryStrategy.runTick();
            if (!secondaryStrategy.isComplete()) {
                return;
            }
            secondaryStrategy = null;
        }
        runBaseTick();
    }

    public abstract void runBaseTick() throws GameActionException;
}
