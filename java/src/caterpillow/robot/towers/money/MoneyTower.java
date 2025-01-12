package caterpillow.robot.towers.money;

import static caterpillow.Game.isStarter;

import battlecode.common.GameActionException;
import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.towers.Tower;

public class MoneyTower extends Tower {
    @Override
    public void init() throws GameActionException {
        super.init();
        if (isStarter) {
            primaryStrategy = new StarterMoneyTowerStrategy();
        } else {
            primaryStrategy = new NormalMoneyTowerStrategy();
        }
    }
}