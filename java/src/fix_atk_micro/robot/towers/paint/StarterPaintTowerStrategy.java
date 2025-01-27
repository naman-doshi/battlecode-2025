package fix_atk_micro.robot.towers.paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import fix_atk_micro.Game;
import static fix_atk_micro.Game.rc;
import static fix_atk_micro.Game.seed;
import fix_atk_micro.robot.towers.RespawnStrategy;
import fix_atk_micro.robot.towers.Tower;
import fix_atk_micro.robot.towers.TowerAttackStrategy;
import fix_atk_micro.robot.towers.TowerStrategy;
import fix_atk_micro.robot.towers.UnstuckStrategy;
import fix_atk_micro.robot.towers.spawner.ConditionalSpawner;
import fix_atk_micro.robot.towers.spawner.LoopedSpawner;
import fix_atk_micro.robot.towers.spawner.NullSpawner;
import fix_atk_micro.robot.towers.spawner.SpawnerStrategy;
import fix_atk_micro.robot.towers.spawner.mopper.OffenceMopperSpawner;
import fix_atk_micro.robot.towers.spawner.soldier.InstantScoutSpawner;
import fix_atk_micro.robot.towers.spawner.soldier.PainterSpawner;
import fix_atk_micro.robot.towers.spawner.soldier.RushSpawner;
import fix_atk_micro.robot.towers.spawner.soldier.SRPSpawner;
import fix_atk_micro.robot.towers.spawner.splasher.SplasherSpawner;
import static fix_atk_micro.util.Util.chebyshevDistance;
import static fix_atk_micro.util.Util.guessEnemyLocs;
import static fix_atk_micro.util.Util.indicate;
import fix_atk_micro.world.GameStage;

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
                        OffenceMopperSpawner::new,
                        () -> new ConditionalSpawner(
                                () -> Game.gameStage == GameStage.MID && rng.nextDouble() < 0.25,
                                new PainterSpawner(),
                                new NullSpawner()
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
