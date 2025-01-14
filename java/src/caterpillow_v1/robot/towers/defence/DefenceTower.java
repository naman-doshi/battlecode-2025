package caterpillow_v1.robot.towers.defence;

import battlecode.common.GameActionException;
import caterpillow_v1.robot.EmptyStrategy;
import caterpillow_v1.robot.towers.Tower;

public class DefenceTower extends Tower {

    @Override
    public void init() throws GameActionException {
        super.init();
        primaryStrategy = new EmptyStrategy();
    }
}