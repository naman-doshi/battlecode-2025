package fix_atk_micro.robot.towers.paint;

import battlecode.common.GameActionException;
import static fix_atk_micro.Game.isStarter;
import fix_atk_micro.robot.towers.Tower;

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
