package caterpillow.robot.towers.money;

import battlecode.common.GameActionException;
import caterpillow.robot.towers.Tower;

public class MoneyTower extends Tower {
    @Override
    public void init() throws GameActionException {
        super.init();
        primaryStrategy = new NormalMoneyTowerStrategy();
    }
}