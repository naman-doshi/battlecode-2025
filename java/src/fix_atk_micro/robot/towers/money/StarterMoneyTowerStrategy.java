package fix_atk_micro.robot.towers.money;

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
import fix_atk_micro.robot.towers.spawner.SpawnerStrategy;
import fix_atk_micro.robot.towers.spawner.WaitUntilSpawner;
import fix_atk_micro.robot.towers.spawner.mopper.OffenceMopperSpawner;
import fix_atk_micro.robot.towers.spawner.soldier.RushSpawner;
import fix_atk_micro.robot.towers.spawner.soldier.SRPSpawner;
import fix_atk_micro.robot.towers.spawner.splasher.SplasherSpawner;
import static fix_atk_micro.util.Util.chebyshevDistance;
import static fix_atk_micro.util.Util.guessEnemyLocs;
import static fix_atk_micro.util.Util.indicate;
import fix_atk_micro.world.GameStage;

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
        strats.add(new RespawnStrategy());
        strats.add(new UnstuckStrategy());
        strats.add(new TowerAttackStrategy());
        boolean shouldRush = chebyshevDistance(rc.getLocation(), guessEnemyLocs(rc.getLocation()).get(0)) <= 10;
        strats.add(new SpawnerStrategy(
                // new ConditionalSpawner(
                //     () -> expectedRushDistance(rc.getLocation()) < 15,
                //     new RushSpawner(),
                //     new InstantScoutSpawner()
                // ),
                // new ConditionalSpawner(
                //     () -> expectedRushDistance(rc.getLocation()) < 15,
                //     new RushSpawner(),
                //     new InstantScoutSpawner()
                // ),
                new RushSpawner(10),
                new RushSpawner(10),
                // new InstantScoutSpawner(),
                // new InstantScoutSpawner(),
                new WaitUntilSpawner(() -> rc.getNumberTowers() >= 3),
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
