package caterpillow.robot.towers.money;

import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.towers.Tower;
import caterpillow.robot.towers.paint.StarterPaintTowerStrategy;

import static caterpillow.Game.*;

public class MoneyTower extends Tower {
    @Override
    public void init() {
        super.init();
        if (isStarter) {
            primaryStrategy = new StarterPaintTowerStrategy();
        } else {
            primaryStrategy = new EmptyStrategy();
        }
    }
}