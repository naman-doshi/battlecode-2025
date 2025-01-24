package caterpillow.robot.towers.paint;

import battlecode.common.GameActionException;
import static caterpillow.Game.isStarter;
import caterpillow.robot.towers.Tower;

public class PaintTower extends Tower {

    @Override
    public void init() throws GameActionException {
        super.init();
        if (isStarter) {
            primaryStrategy = new StarterPaintTowerStrategy();
        } else {
            primaryStrategy = new NormalPaintTowerStrategy();
        }
        initScoutTargets();
    }
}
