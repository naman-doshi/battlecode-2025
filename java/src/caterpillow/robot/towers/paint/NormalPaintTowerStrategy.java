package caterpillow.robot.towers.paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import static caterpillow.Game.seed;
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
import caterpillow.robot.towers.spawner.soldier.PainterSpawner;
import caterpillow.robot.towers.spawner.soldier.SRPSpawner;
import caterpillow.robot.towers.spawner.soldier.ScoutSpawner;
import caterpillow.robot.towers.spawner.splasher.SplasherSpawner;
import static caterpillow.tracking.CellTracker.getNearestCell;
import static caterpillow.util.Util.indicate;
import caterpillow.world.GameStage;

public class NormalPaintTowerStrategy extends TowerStrategy {

    // in case we get rushed
    int nxt;
    Random rng;
    Tower bot;
    boolean anyAlly = false;

    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public NormalPaintTowerStrategy() throws GameActionException {
        bot = (Tower) Game.bot;
        rng = new Random(seed);

        strats = new ArrayList<>();
        strats.add(new RespawnStrategy());
        strats.add(new UnstuckStrategy());
        strats.add(new TowerAttackStrategy());
        strats.add(new SpawnerStrategy(
                //new ScoutSpawner(),
                // trng.nextInt(0, 1) == 0 ? new RushSpawner() : new NullSpawner(),
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
                                () -> Game.gameStage == GameStage.MID && rng.nextDouble() < 0.25,
                                new PainterSpawner(),
                                new NullSpawner()
                        )
                )
        ));
        nxt = 0;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("NORMAL");
        for (MapInfo info : Game.rc.senseNearbyMapInfos(4)) {
            if (!info.getPaint().isEnemy() && !info.hasRuin()) {
                //System.out.println("type " + info.getPaint() + " loc " + info.getMapLocation() + " my loc " + Game.rc.getLocation());
                anyAlly = true;
                break;
            }
        }
        //System.out.println("ally " + anyAlly);

        if (!anyAlly) {
            MapInfo info = getNearestCell(c -> Game.rc.canBuildRobot(UnitType.MOPPER, c.getMapLocation()));
            if (info != null) {
                bot.build(UnitType.MOPPER, info.getMapLocation());
                anyAlly = true;
            }
        }
        for (TowerStrategy strat : strats) {
            strat.runTick();
        }
    }
}
