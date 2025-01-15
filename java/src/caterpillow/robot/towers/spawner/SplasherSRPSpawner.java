package caterpillow.robot.towers.spawner;

import battlecode.common.GameActionException;
import caterpillow.Game;
import caterpillow.world.GameStage;

public class SplasherSRPSpawner extends Spawner {
    @Override
    public boolean spawn() throws GameActionException {
        double splasherProbability;
        if (Game.gameStage.equals(GameStage.MID)) {
            splasherProbability = 0.8;
        } else {
            splasherProbability = 0.2;
        }
        double rand = Game.trng.nextDouble();
        if (rand < splasherProbability) {
            return new SplasherSpawner().spawn();
        } else {
            return new SRPSpawner().spawn();
        }
    }
}
