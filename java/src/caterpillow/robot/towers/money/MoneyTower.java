package caterpillow.robot.towers.money;

import static caterpillow.Game.isStarter;
import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.towers.MopperSpawnStrategy;
import caterpillow.robot.towers.Tower;

public class MoneyTower extends Tower {
    @Override
    public void init() {
        super.init();
        if (isStarter) {
            primaryStrategy = new StarterMoneyTowerStrategy();
        } else {
            primaryStrategy = new EmptyStrategy();
        }
    }
}