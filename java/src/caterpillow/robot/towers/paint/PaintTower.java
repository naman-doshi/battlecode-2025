package caterpillow.robot.towers.paint;

import battlecode.common.GameActionException;
import battlecode.common.UnitType;
import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.towers.SpawnStrategy;
import caterpillow.robot.towers.Tower;

import static caterpillow.Game.*;

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
