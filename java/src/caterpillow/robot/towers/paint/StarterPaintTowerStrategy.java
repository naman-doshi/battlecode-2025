package caterpillow.robot.towers.paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import caterpillow.Game;
import static caterpillow.Game.seed;

import caterpillow.robot.towers.*;
import caterpillow.robot.towers.spawner.*;
import caterpillow.robot.towers.spawner.mopper.OffenceMopperSpawner;
import caterpillow.robot.towers.spawner.soldier.*;
import caterpillow.robot.towers.spawner.splasher.SplasherSpawner;
import caterpillow.world.GameStage;

import static caterpillow.util.Util.getNearestCell;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.expectedRushDistance;

public class StarterPaintTowerStrategy extends TowerStrategy {

    List<TowerStrategy> strats;
    // in case we get rushed
    int todo;
    Tower bot;
    Random rng;
    boolean anyAlly = false;
    public StarterPaintTowerStrategy() throws GameActionException {
        todo = 1;
        bot = (Tower) Game.bot;
        rng = new Random(seed);
        strats = new ArrayList<>();
        strats.add(new UnstuckStrategy());
        strats.add(new TowerAttackStrategy());
        strats.add(new SpawnerStrategy(
                new ScoutPairSpawner(),
                new InstantScoutSpawner(),
//                new EarlyScoutSpawner(),
                new LoopedSpawner(
                        () -> new LoopedSpawner(2,
                                () -> new ConditionalSpawner(
                                        () -> Game.gameStage == GameStage.EARLY,
                                        new SRPSpawner(),
                                        new SplasherSpawner()
                                )
                        ),
                        OffenceMopperSpawner::new
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
