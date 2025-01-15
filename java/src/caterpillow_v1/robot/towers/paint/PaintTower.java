package caterpillow_v1.robot.towers.paint;

import battlecode.common.GameActionException;
import static caterpillow_v1.Game.isStarter;
import caterpillow_v1.robot.towers.Tower;

public class PaintTower extends Tower {

    @Override
    public void init() throws GameActionException {
        super.init();
        if (isStarter) {
            primaryStrategy = new StarterPaintTowerStrategy();
        } else {
            primaryStrategy = new NormalPaintTowerStrategy();
        }
    }
}
