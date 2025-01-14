package caterpillow.robot.towers.paint;

import battlecode.common.GameActionException;
import caterpillow.robot.towers.Tower;

public class PaintTower extends Tower {

    @Override
    public void init() throws GameActionException {
        super.init();
        primaryStrategy = new NormalPaintTowerStrategy();
    }
}
