package caterpillow.robot.towers.money;

import java.util.ArrayList;
import java.util.List;

import battlecode.common.GameActionException;
import static caterpillow.Config.shouldHaveSuicidalMoneyTowers;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.towers.RespawnStrategy;
import caterpillow.robot.towers.Tower;
import caterpillow.robot.towers.TowerAttackStrategy;
import caterpillow.robot.towers.TowerStrategy;
import caterpillow.robot.towers.UnstuckStrategy;
import caterpillow.robot.towers.spawner.ConditionalSpawner;
import caterpillow.robot.towers.spawner.LoopedSpawner;
import caterpillow.robot.towers.spawner.NullSpawner;
import caterpillow.robot.towers.spawner.SpawnerStrategy;
import caterpillow.robot.towers.spawner.mopper.OffenceMopperSpawner;
import caterpillow.robot.towers.spawner.soldier.InstantScoutSpawner;
import caterpillow.robot.towers.spawner.soldier.PainterSpawner;
import caterpillow.robot.towers.spawner.soldier.SRPSpawner;
import caterpillow.robot.towers.spawner.soldier.ScoutSpawner;
import caterpillow.robot.towers.spawner.splasher.SplasherSpawner;
import caterpillow.tracking.TowerTracker;
import caterpillow.util.CustomRandom;
import static caterpillow.util.Util.indicate;
import caterpillow.world.GameStage;

public class NormalMoneyTowerStrategy extends TowerStrategy {

    // in case we get rushed
    int seed;
    CustomRandom rng;
    Tower bot;
    int nxt;
    boolean anyAlly = false;

    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public NormalMoneyTowerStrategy() throws GameActionException {
        bot = (Tower) Game.bot;
        seed = new CustomRandom(rc.getID()).nextInt();
        rng = new CustomRandom(seed);

        strats = new ArrayList<>();
        strats.add(new RespawnStrategy());
        strats.add(new UnstuckStrategy());
        strats.add(new TowerAttackStrategy());
        strats.add(new SpawnerStrategy(
                //new ScoutSpawner(),
                rc.getNumberTowers() % 2 == 1 || shouldHaveSuicidalMoneyTowers() ? new InstantScoutSpawner() : new NullSpawner(),
                // trng.nextInt(4) == 0
                //     ? Game.gameStage == GameStage.EARLY ? new InstantScoutSpawner() : new InstantSRPSpawner()
                //     : trng.nextInt(3) != 0
                //     ? new RushSpawner()
                //     : new NullSpawner(),
                // new InstantScoutSpawner(),

                new ConditionalSpawner(() -> shouldHaveSuicidalMoneyTowers(), 
                        new LoopedSpawner(
                            () -> new ConditionalSpawner(() -> rng.nextDouble() < 0.5, 
                                new LoopedSpawner(
                                    OffenceMopperSpawner::new,
                                    OffenceMopperSpawner::new
                                ),
                                new SplasherSpawner()
                            )
                        ),
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
                                        new ScoutSpawner(),
                                        new SplasherSpawner()
                                ),
                                OffenceMopperSpawner::new,
                                () -> new ConditionalSpawner(
                                        () -> Game.gameStage == GameStage.MID && rng.nextDouble() < 0.3,
                                        new PainterSpawner(),
                                        new NullSpawner()
                                )
                        )
                )


                
        ));
        strats.add(new ConvertToPaintTowerStrategy());
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
