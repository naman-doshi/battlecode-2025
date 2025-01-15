package caterpillow.robot.towers.paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import caterpillow.Game;
import static caterpillow.Game.seed;
import caterpillow.robot.towers.RespawnStrategy;
import caterpillow.robot.towers.Tower;
import caterpillow.robot.towers.TowerAttackStrategy;
import caterpillow.robot.towers.TowerStrategy;
import caterpillow.robot.towers.spawner.LoopedSpawner;
import caterpillow.robot.towers.spawner.OffenceMopperSpawner;
import caterpillow.robot.towers.spawner.SRPSpawner;
import caterpillow.robot.towers.spawner.ScoutSpawner;
import caterpillow.robot.towers.spawner.SpawnerStrategy;
import caterpillow.robot.towers.spawner.SplasherSRPSpawner;
import static caterpillow.util.Util.indicate;

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
        strats.add(new RespawnStrategy());
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
