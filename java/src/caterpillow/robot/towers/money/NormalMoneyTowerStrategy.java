package caterpillow.robot.towers.money;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import caterpillow.Game;
import static caterpillow.Game.rc;

import caterpillow.robot.towers.*;
import caterpillow.robot.towers.spawner.ConditionalSpawner;
import caterpillow.robot.towers.spawner.LoopedSpawner;
import caterpillow.robot.towers.spawner.mopper.OffenceMopperSpawner;
import caterpillow.robot.towers.spawner.mopper.PassiveMopperSpawner;
import caterpillow.robot.towers.spawner.soldier.SRPSpawner;
import caterpillow.robot.towers.spawner.SpawnerStrategy;
import caterpillow.robot.towers.spawner.splasher.SplasherSpawner;
import caterpillow.world.GameStage;

import static caterpillow.util.Util.getNearestCell;
import static caterpillow.util.Util.indicate;

public class NormalMoneyTowerStrategy extends TowerStrategy {

    // in case we get rushed
    int seed;
    Random rng;
    Tower bot;
    int nxt;
    boolean anyAlly = false;

    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public NormalMoneyTowerStrategy() throws GameActionException {
        bot = (Tower) Game.bot;
        seed = new Random(rc.getID()).nextInt();
        rng = new Random(seed);

        strats = new ArrayList<>();
        strats.add(new UnstuckStrategy());
        strats.add(new TowerAttackStrategy());
        strats.add(new SpawnerStrategy(
                //new ScoutSpawner(),
                new SRPSpawner(),
                new LoopedSpawner(
                        () -> new ConditionalSpawner(
                                () -> Game.gameStage == GameStage.EARLY,
                                new SRPSpawner(),
                                new SplasherSpawner()
                        ),
                        OffenceMopperSpawner::new,
                        () -> new ConditionalSpawner(
                                () -> Game.gameStage == GameStage.EARLY,
                                new SRPSpawner(),
                                new SplasherSpawner()
                        ),
                        PassiveMopperSpawner::new
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
