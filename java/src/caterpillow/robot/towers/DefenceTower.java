package caterpillow.robot.towers;

import battlecode.common.*;
import caterpillow.robot.Strategy;
import caterpillow.robot.towers.strategies.DefenceStrategy;
import caterpillow.robot.towers.strategies.RusherSpawnStrategy;
import caterpillow.robot.towers.strategies.ShitRushSpawnerStrategy;
import caterpillow.robot.towers.strategies.SpawnStrategy;

import static caterpillow.Game.*;

public class DefenceTower extends Tower {
    Strategy primaryStrategy;
    Strategy secondaryStrategy;

    @Override
    public void init() {
        super.init();
        primaryStrategy = new RusherSpawnStrategy();
        secondaryStrategy = new DefenceStrategy();
    }

    @Override
    public void runTick() throws GameActionException {
        if (!secondaryStrategy.isComplete()) {
            secondaryStrategy.runTick();
        } else {
            primaryStrategy.runTick();
        }
    }
}