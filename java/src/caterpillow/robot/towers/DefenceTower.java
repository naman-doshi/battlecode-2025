package caterpillow.robot.towers;

import battlecode.common.*;
import caterpillow.robot.Strategy;
import caterpillow.robot.towers.strategies.DefenceStrategy;
import caterpillow.robot.towers.strategies.RusherSpawnStrategy;
import caterpillow.robot.towers.strategies.ShitRushSpawnerStrategy;
import caterpillow.robot.towers.strategies.SpawnStrategy;

import static caterpillow.Game.*;

public class DefenceTower extends Tower {

    @Override
    public void init() {
        super.init();
        primaryStrategy = new RusherSpawnStrategy();
        secondaryStrategy = new DefenceStrategy();
    }
}