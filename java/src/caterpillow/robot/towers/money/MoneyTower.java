package caterpillow.robot.towers.money;

import caterpillow.robot.towers.DefenceStrategy;
import caterpillow.robot.towers.SpawnStrategy;
import caterpillow.robot.towers.Tower;
import caterpillow.robot.towers.braindamage.SniperSpawnStrategy;

import static caterpillow.Game.*;

public class MoneyTower extends Tower {
    @Override
    public void init() {
        super.init();
        if (isStarter) {
            primaryStrategy = new SniperSpawnStrategy();
        } else {
            primaryStrategy = new SpawnStrategy();
        }
        secondaryStrategy = new DefenceStrategy();
    }
}