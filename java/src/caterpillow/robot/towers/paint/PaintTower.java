package caterpillow.robot.towers.paint;

import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.towers.Tower;

import static caterpillow.Game.*;

public class PaintTower extends Tower {

    @Override
    public void init() {
        super.init();
        if (isStarter) {
//            primaryStrategy = new SniperSpawnStrategy();
        } else {
//            primaryStrategy = new SpawnStrategy();
        }
//        secondaryStrategy = new DefenceStrategy();
        primaryStrategy = new EmptyStrategy();
    }
}
