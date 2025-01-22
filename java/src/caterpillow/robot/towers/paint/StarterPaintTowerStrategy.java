package caterpillow.robot.towers.paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import caterpillow.Game;
import static caterpillow.Game.seed;
import caterpillow.robot.towers.Tower;
import caterpillow.robot.towers.TowerAttackStrategy;
import caterpillow.robot.towers.TowerStrategy;
import caterpillow.robot.towers.UnstuckStrategy;
import caterpillow.robot.towers.spawner.ConditionalSpawner;
import caterpillow.robot.towers.spawner.LoopedSpawner;
import caterpillow.robot.towers.spawner.SpawnerStrategy;
import caterpillow.robot.towers.spawner.mopper.OffenceMopperSpawner;
import caterpillow.robot.towers.spawner.soldier.InstantScoutSpawner;
import caterpillow.robot.towers.spawner.soldier.SRPSpawner;
import caterpillow.robot.towers.spawner.splasher.SplasherSpawner;
import static caterpillow.util.Util.indicate;
import caterpillow.world.GameStage;

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
                // new ConditionalSpawner(
                //     () -> mapHeight * mapWidth < 1300,
                //     new RushSpawner(),
                //     new InstantScoutSpawner()
                // ),
                new InstantScoutSpawner(),
                new InstantScoutSpawner(),
                new InstantScoutSpawner(),
                // new LoopedSpawner(
                //         () -> new LoopedSpawner(2,
                //                 () -> new ConditionalSpawner(
                //                         () -> Game.gameStage == GameStage.EARLY,
                //                         new SRPSpawner(),
                //                         new SplasherSpawner()
                //                 )
                //         ),
                //         OffenceMopperSpawner::new
                // )
                new LoopedSpawner(
                        SRPSpawner::new,
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
