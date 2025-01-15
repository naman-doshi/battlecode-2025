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
import caterpillow.robot.towers.spawner.PassiveMopperSpawner;
import caterpillow.robot.towers.spawner.SRPSpawner;
import caterpillow.robot.towers.spawner.ScoutSpawner;
import caterpillow.robot.towers.spawner.SpawnerStrategy;
import caterpillow.robot.towers.spawner.SplasherSRPSpawner;
import static caterpillow.util.Util.indicate;

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
//                new ScoutSpawner(),
                new SRPSpawner(),
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
