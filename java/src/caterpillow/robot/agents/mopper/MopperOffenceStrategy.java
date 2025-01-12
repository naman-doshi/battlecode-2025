package caterpillow.robot.agents.mopper;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;

public class MopperOffenceStrategy extends Strategy {

    public Mopper bot;

    public MopperOffenceStrategy() {
        bot = (Mopper) Game.bot;


    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        RobotController rcc = rc;
    }
}
