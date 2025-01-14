package caterpillow.robot.agents.soldier;

import battlecode.common.GameActionException;
import battlecode.common.UnitType;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.LinkStrategy;
import caterpillow.robot.troll.QueueStrategy;

/*

this is the strategy for starter soldiers
    1. they need to comm with the tower
    2. they need to reinforce their starting tower
        however, the second strat *should* complete the first task as a side effect so its ok
    3. they need to then go scouting

this class looks stupid since its basically has the same structure as the soldier's default pair of primary and secondary
but its like against the philosophy of what those two are meant to represent,
and reinforcing the starting tower is more of a part of the starting strategy than a sidequest ykwim

*/

public class StarterStrategy extends QueueStrategy {

    Agent bot;
    Strategy mainStrat;
    Strategy homework;

    public StarterStrategy(UnitType type) {
        bot = (Agent) Game.bot;
        mainStrat = new ScoutStrategy();
        homework = new LinkStrategy(bot.home);
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (homework != null) {
            if (homework.isComplete()) {
                homework = null;
                return isComplete();
            } else {
                return false;
            }
        }
        return mainStrat.isComplete();
    }

    @Override
    public void runTick() throws GameActionException {
        if (homework != null) {
            homework.runTick();
        } else {
            mainStrat.runTick();
        }
    }
}
