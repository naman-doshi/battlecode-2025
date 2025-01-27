package fix_atk_micro.robot.towers.money;

import battlecode.common.GameActionException;
import static fix_atk_micro.Game.isStarter;
import fix_atk_micro.robot.towers.Tower;

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