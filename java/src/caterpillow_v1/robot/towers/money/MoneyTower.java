package caterpillow_v1.robot.towers.money;

import battlecode.common.GameActionException;
import static caterpillow_v1.Game.isStarter;
import caterpillow_v1.robot.towers.Tower;

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