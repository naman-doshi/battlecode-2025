package caterpillow.robot.towers.money;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import caterpillow.Game;

import static caterpillow.Game.rc;
import static caterpillow.Game.seed;

import caterpillow.robot.towers.*;
import caterpillow.robot.towers.spawner.ConditionalSpawner;
import caterpillow.robot.towers.spawner.LoopedSpawner;
import caterpillow.robot.towers.spawner.WaitUntilSpawner;
import caterpillow.robot.towers.spawner.mopper.InstantOffenceMopperSpawner;
import caterpillow.robot.towers.spawner.mopper.OffenceMopperSpawner;
import caterpillow.robot.towers.spawner.mopper.PassiveMopperSpawner;
import caterpillow.robot.towers.spawner.soldier.*;
import caterpillow.robot.towers.spawner.SpawnerStrategy;
import caterpillow.robot.towers.spawner.splasher.SplasherSpawner;
import caterpillow.world.GameStage;

import static caterpillow.util.Util.expectedRushDistance;
import static caterpillow.util.Util.getNearestCell;
import static caterpillow.util.Util.indicate;

public class StarterMoneyTowerStrategy extends TowerStrategy {

    // in case we get rushed
    int todo;
    Random rng;
    Tower bot;
    boolean anyAlly = false;
    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public StarterMoneyTowerStrategy() throws GameActionException {
        todo = 2;
        bot = (Tower) Game.bot;
        rng = new Random(seed);
        strats = new ArrayList<>();
        strats.add(new UnstuckStrategy());
        strats.add(new TowerAttackStrategy());
        strats.add(new SpawnerStrategy(
                new ScoutPairSpawner(),
                new WaitUntilSpawner(() -> rc.getNumberTowers() >= 3),
                new InstantOffenceMopperSpawner(),
                new LoopedSpawner(
                        OffenceMopperSpawner::new,
                        () -> new LoopedSpawner(2,
                                () -> new ConditionalSpawner(
                                        () -> Game.gameStage == GameStage.EARLY,
                                        new SRPSpawner(),
                                        new SplasherSpawner()
                                )
                        )
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
