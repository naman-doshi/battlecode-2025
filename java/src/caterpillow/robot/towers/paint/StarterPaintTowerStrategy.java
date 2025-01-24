package caterpillow.robot.towers.paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.Game.seed;
import caterpillow.robot.towers.RespawnStrategy;
import caterpillow.robot.towers.Tower;
import caterpillow.robot.towers.TowerAttackStrategy;
import caterpillow.robot.towers.TowerStrategy;
import caterpillow.robot.towers.UnstuckStrategy;
import caterpillow.robot.towers.spawner.ConditionalSpawner;
import caterpillow.robot.towers.spawner.LoopedSpawner;
import caterpillow.robot.towers.spawner.SpawnerStrategy;
import caterpillow.robot.towers.spawner.mopper.OffenceMopperSpawner;
import caterpillow.robot.towers.spawner.soldier.InstantScoutSpawner;
import caterpillow.robot.towers.spawner.soldier.RushSpawner;
import caterpillow.robot.towers.spawner.soldier.SRPSpawner;
import caterpillow.robot.towers.spawner.splasher.SplasherSpawner;
import static caterpillow.util.Util.chebyshevDistance;
import static caterpillow.util.Util.guessEnemyLocs;
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
        strats.add(new RespawnStrategy());
        strats.add(new UnstuckStrategy());
        strats.add(new TowerAttackStrategy());
        boolean shouldRush = chebyshevDistance(rc.getLocation(), guessEnemyLocs(rc.getLocation()).get(0)) <= 10;
        strats.add(new SpawnerStrategy(
                // new ConditionalSpawner(
                //     () -> expectedRushDistance(Game.rc.getLocation()) < 20,
                //     new RushSpawner(),
                //     new InstantScoutSpawner()
                // ),
                // new ConditionalSpawner(
                //     () -> expectedRushDistance(Game.rc.getLocation()) < 20,
                //     new RushSpawner(),
                //     new InstantScoutSpawner()
                // ),

                new RushSpawner(10),
                new RushSpawner(10),

                //new InstantScoutSpawner(),
                //new InstantScoutSpawner(),
                new InstantScoutSpawner(),
                // new InstantOffenceMopperSpawner(),
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
                        // () -> new ConditionalSpawner(
                        //         () -> Game.gameStage == GameStage.MID && rng.nextDouble() < 0.25,
                        //         new PainterSpawner(),
                        //         new NullSpawner()
                        // )
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
