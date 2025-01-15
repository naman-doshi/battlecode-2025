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
import caterpillow_v1.robot.towers.spawner.PassiveMopperSpawner;
import caterpillow_v1.robot.towers.spawner.SRPSpawner;
import caterpillow_v1.robot.towers.spawner.ScoutSpawner;
import caterpillow_v1.robot.towers.spawner.SpawnerStrategy;
import caterpillow_v1.robot.towers.spawner.SplasherSRPSpawner;
import static caterpillow_v1.util.Util.indicate;

public class NormalPaintTowerStrategy extends TowerStrategy {

    // in case we get rushed
    int nxt;
    Random rng;
    Tower bot;

    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public NormalPaintTowerStrategy() {
        bot = (Tower) Game.bot;
        rng = new Random(seed);

        strats = new ArrayList<>();
        strats.add(new TowerAttackStrategy());
        strats.add(new SpawnerStrategy(
                new ScoutSpawner(),
//                new SRPSpawner(),
                new LoopedSpawner(
                        new SplasherSRPSpawner(),
                        new OffenceMopperSpawner(),
                        new SRPSpawner(),
                        new PassiveMopperSpawner(),
                        new SplasherSRPSpawner()
                )
        ));
        nxt = 0;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("NORMAL");
        for (TowerStrategy strat : strats) {
            strat.runTick();
        }
    }
}
