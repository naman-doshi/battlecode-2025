package fix_atk_micro.robot.towers.defence;

import battlecode.common.GameActionException;
import fix_atk_micro.robot.towers.Tower;

public class DefenceTower extends Tower {

    @Override
    public void init() throws GameActionException {
        super.init();
        primaryStrategy = new NormalDefenceTowerStrategy();
    }
}