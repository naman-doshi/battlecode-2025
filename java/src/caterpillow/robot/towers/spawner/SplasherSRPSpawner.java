package caterpillow.robot.towers.spawner;

import battlecode.common.GameActionException;
import caterpillow.Game;
import caterpillow.world.GameStage;

public class SplasherSRPSpawner extends Spawner {
    @Override
    public boolean spawn() throws GameActionException {
        if (Game.gameStage.equals(GameStage.MID)) {
            return new SplasherSpawner().spawn();
        } else {
            return new SRPSpawner().spawn();
        }
    }
}
