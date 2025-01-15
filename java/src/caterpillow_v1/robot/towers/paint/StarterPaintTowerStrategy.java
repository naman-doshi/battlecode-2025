package caterpillow_v1.robot.towers.paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import caterpillow_v1.Game;
import static caterpillow_v1.Game.seed;
import caterpillow_v1.robot.towers.RespawnStrategy;
import caterpillow_v1.robot.towers.Tower;
import caterpillow_v1.robot.towers.TowerAttackStrategy;
import caterpillow_v1.robot.towers.TowerStrategy;
import caterpillow_v1.robot.towers.spawner.LoopedSpawner;
import caterpillow_v1.robot.towers.spawner.OffenceMopperSpawner;
import caterpillow_v1.robot.towers.spawner.SRPSpawner;
import caterpillow_v1.robot.towers.spawner.ScoutSpawner;
import caterpillow_v1.robot.towers.spawner.SpawnerStrategy;
import caterpillow_v1.robot.towers.spawner.SplasherSRPSpawner;
import static caterpillow_v1.util.Util.indicate;

public class StarterPaintTowerStrategy extends TowerStrategy {

    List<TowerStrategy> strats;
    // in case we get rushed
    int todo;
    Tower bot;
    Random rng;

    public StarterPaintTowerStrategy() {
        todo = 1;
        bot = (Tower) Game.bot;
        rng = new Random(seed);
        strats = new ArrayList<>();
        strats.add(new TowerAttackStrategy());
        strats.add(new SpawnerStrategy(
                    new ScoutSpawner(),
                    new ScoutSpawner(),
                    new LoopedSpawner(
                            new SRPSpawner(),
                            new SplasherSRPSpawner(),
                            new OffenceMopperSpawner()
                    )
        ));
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("STARTER");
        for (TowerStrategy strat : strats) {
            strat.runTick();
        }
    }
}
