package caterpillow.robot.towers.defence;

import battlecode.common.GameActionException;
import caterpillow.robot.towers.Tower;

public class DefenceTower extends Tower {

    @Override
    public void init() throws GameActionException {
        super.init();
        primaryStrategy = new NormalDefenceTowerStrategy();
    }
}