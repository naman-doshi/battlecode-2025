package caterpillow.robot.towers;

import battlecode.common.*;
import caterpillow.robot.Strategy;
import caterpillow.robot.towers.strategies.DefenceStrategy;
import caterpillow.robot.towers.strategies.RusherSpawnStrategy;
import caterpillow.robot.towers.strategies.ShitRushSpawnerStrategy;
import caterpillow.robot.towers.strategies.SpawnStrategy;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class ChipTower extends Tower {
    Strategy primaryStrategy;
    Strategy secondaryStrategy;

    @Override
    public void init() {
        super.init();
        if (isStarter) {
            primaryStrategy = new ShitRushSpawnerStrategy();
        } else {
            primaryStrategy = new SpawnStrategy();
        }
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