package caterpillow.robot.towers.defence;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.towers.RespawnStrategy;
import caterpillow.robot.towers.Tower;
import caterpillow.robot.towers.TowerAttackStrategy;
import caterpillow.robot.towers.TowerStrategy;
import caterpillow.robot.towers.spawner.LoopedSpawner;
import caterpillow.robot.towers.spawner.OffenceMopperSpawner;
import caterpillow.robot.towers.spawner.PassiveMopperSpawner;
import caterpillow.robot.towers.spawner.SRPSpawner;
import caterpillow.robot.towers.spawner.ScoutSpawner;
import caterpillow.robot.towers.spawner.SpawnerStrategy;
import caterpillow.robot.towers.spawner.SplasherSRPSpawner;
import static caterpillow.util.Util.indicate;

public class NormalDefenceTowerStrategy extends TowerStrategy {

    // in case we get rushed
    int seed;
    Random rng;
    Tower bot;
    int nxt;

    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public NormalDefenceTowerStrategy() {
        bot = (Tower) Game.bot;
        seed = new Random(rc.getID()).nextInt();
        rng = new Random(seed);

        strats = new ArrayList<>();
        strats.add(new RespawnStrategy());
        strats.add(new TowerAttackStrategy());
        strats.add(new SpawnerStrategy(
                new ScoutSpawner(),
                new LoopedSpawner(
                        new SplasherSRPSpawner(),
                        new OffenceMopperSpawner(),
                        new SRPSpawner(),
                        new PassiveMopperSpawner(),
                        new SplasherSRPSpawner()
                )
        ));
        nxt = 0;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("NORMAL");

        // check if i need to kms
        boolean enemySurround = false;
        for (MapInfo cell : rc.senseNearbyMapInfos()) {
            if (cell.getPaint().isEnemy()) {
                enemySurround = true;
                break;
            }
        }

        if (!enemySurround) rc.disintegrate();


        for (TowerStrategy strat : strats) {
            strat.runTick();
        }
    }
}
