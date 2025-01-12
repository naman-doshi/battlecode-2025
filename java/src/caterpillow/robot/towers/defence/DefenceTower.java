package caterpillow.robot.towers.defence;

import caterpillow.robot.towers.Tower;
import caterpillow.robot.towers.DefenceStrategy;
import caterpillow.robot.towers.RusherSpawnStrategy;

public class DefenceTower extends Tower {

    @Override
    public void init() {
        super.init();
        primaryStrategy = new RusherSpawnStrategy();
    }
}