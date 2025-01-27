package fix_atk_micro.robot.towers.money;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import fix_atk_micro.Game;
import static fix_atk_micro.Game.rc;
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
import fix_atk_micro.robot.towers.spawner.soldier.SRPSpawner;
import fix_atk_micro.robot.towers.spawner.soldier.ScoutSpawner;
import fix_atk_micro.robot.towers.spawner.splasher.SplasherSpawner;
import fix_atk_micro.tracking.TowerTracker;
import static fix_atk_micro.util.Util.indicate;
import fix_atk_micro.world.GameStage;

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
        strats.add(new RespawnStrategy());
        strats.add(new UnstuckStrategy());
        strats.add(new TowerAttackStrategy());
        strats.add(new SpawnerStrategy(
                //new ScoutSpawner(),
                TowerTracker.coinTowers % 3 == 0 ? new InstantScoutSpawner() : new NullSpawner(),
                // trng.nextInt(4) == 0
                //     ? Game.gameStage == GameStage.EARLY ? new InstantScoutSpawner() : new InstantSRPSpawner()
                //     : trng.nextInt(3) != 0
                //     ? new RushSpawner()
                //     : new NullSpawner(),
                // new InstantScoutSpawner(),
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
