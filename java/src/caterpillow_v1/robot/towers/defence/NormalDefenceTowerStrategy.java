package caterpillow_v1.robot.towers.defence;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import caterpillow_v1.Game;
import static caterpillow_v1.Game.rc;
import caterpillow_v1.robot.towers.RespawnStrategy;
import caterpillow_v1.robot.towers.Tower;
import caterpillow_v1.robot.towers.TowerAttackStrategy;
import caterpillow_v1.robot.towers.TowerStrategy;
import caterpillow_v1.robot.towers.spawner.LoopedSpawner;
import caterpillow_v1.robot.towers.spawner.OffenceMopperSpawner;
import caterpillow_v1.robot.towers.spawner.SpawnerStrategy;
import caterpillow_v1.robot.towers.spawner.SplasherSpawner;
import static caterpillow_v1.util.Util.indicate;

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
        strats.add(new TowerAttackStrategy());
        strats.add(new SpawnerStrategy(
                new LoopedSpawner(
                    new SplasherSpawner(),
                    new OffenceMopperSpawner()
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

        if (!enemySurround && rc.getChips() > 4000) rc.disintegrate();


        for (TowerStrategy strat : strats) {
            strat.runTick();
        }
    }
}
