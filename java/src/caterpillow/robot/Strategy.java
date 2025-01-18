package caterpillow.robot;

import battlecode.common.GameActionException;

/*

as a general policy, strategies are responsible for checking whether they are runnable
for example, a previous strategy may have made them unable to paint but still able to move
if the current strategy doesnt require painting, it should see that it can move but not paint and act accordingly

*/

public abstract class Strategy {
    // returns true if the strategy is incomplete
    public boolean tryStrategy(Strategy strat) throws GameActionException {
        if(strat == null) return false;
        if(strat.isComplete()) return false;
        strat.runTick();
        return !strat.isComplete();
    }
    public abstract boolean isComplete() throws GameActionException;
    public abstract void runTick() throws GameActionException;
}
