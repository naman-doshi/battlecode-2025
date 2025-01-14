package caterpillow_v1.robot.towers.spawner;

import battlecode.common.GameActionException;
import caterpillow_v1.Game;
import caterpillow_v1.util.TowerTracker;
import caterpillow_v1.world.GameStage;

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
