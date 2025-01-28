package caterpillow.robot.towers.money;

import java.util.ArrayList;
import java.util.List;

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
import caterpillow.robot.towers.spawner.WaitUntilSpawner;
import caterpillow.robot.towers.spawner.mopper.OffenceMopperSpawner;
import caterpillow.robot.towers.spawner.soldier.InstantScoutSpawner;
import caterpillow.robot.towers.spawner.soldier.RushSpawner;
import caterpillow.robot.towers.spawner.soldier.SRPSpawner;
import caterpillow.robot.towers.spawner.splasher.SplasherSpawner;
import caterpillow.util.CustomRandom;
import static caterpillow.util.Util.chebyshevDistance;
import static caterpillow.util.Util.guessEnemyLocs;
import static caterpillow.util.Util.indicate;
import caterpillow.world.GameStage;

public class StarterMoneyTowerStrategy extends TowerStrategy {

    // in case we get rushed
    int todo;
    CustomRandom rng;
    Tower bot;
    boolean anyAlly = false;
    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public StarterMoneyTowerStrategy() throws GameActionException {
        todo = 2;
        bot = (Tower) Game.bot;
        rng = new CustomRandom(seed);
        strats = new ArrayList<>();
        strats.add(new RespawnStrategy());
        strats.add(new UnstuckStrategy());
        strats.add(new TowerAttackStrategy());
//        boolean shouldRush = chebyshevDistance(rc.getLocation(), guessEnemyLocs(rc.getLocation(), true).get(0)) <= 10;
        boolean shouldRush = false;
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
                shouldRush ? new RushSpawner(10) : new InstantScoutSpawner(),
                shouldRush ? new RushSpawner(10) : new InstantScoutSpawner(),
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
