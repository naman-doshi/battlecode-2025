package caterpillow.robot.towers.defence;

import caterpillow.robot.towers.MopperSpawnStrategy;
import caterpillow.robot.towers.Tower;

public class DefenceTower extends Tower {

    @Override
    public void init() {
        super.init();
        primaryStrategy = new MopperSpawnStrategy();
    }
}