package caterpillow.robot.towers;

import battlecode.common.*;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.EmptyStrategy;
import caterpillow.robot.towers.strategies.*;

import static caterpillow.util.Util.*;
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
