package caterpillow.robot.towers.money;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import static caterpillow.Game.seed;

import caterpillow.robot.towers.*;
import caterpillow.robot.towers.spawner.LoopedSpawner;
import caterpillow.robot.towers.spawner.OffenceMopperSpawner;
import caterpillow.robot.towers.spawner.RushSpawner;
import caterpillow.robot.towers.spawner.SRPSpawner;
import caterpillow.robot.towers.spawner.ScoutSpawner;
import caterpillow.robot.towers.spawner.SpawnerStrategy;
import caterpillow.robot.towers.spawner.SplasherSRPSpawner;
import static caterpillow.util.Util.expectedRushDistance;
import static caterpillow.util.Util.getNearestCell;
import static caterpillow.util.Util.indicate;

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
        strats.add(new UnstuckStrategy());
        strats.add(new TowerAttackStrategy());

        // only rush if map is small (but not too small, as this means that ruins are dense), or short dist to enemy
        // we're rushing from money tower to cripple their finances (as the corresponding enemy tower is a money tower)
        int size = Game.rc.getMapWidth() * Game.rc.getMapHeight();
        int expectedDistance = expectedRushDistance(Game.rc.getLocation());
        if (size < 900 || expectedDistance < 15) {
            strats.add(new SpawnerStrategy(
                new RushSpawner(),
                new RushSpawner(),
                new LoopedSpawner(
                        SRPSpawner::new,
                        SplasherSRPSpawner::new,
                        OffenceMopperSpawner::new
                )
            ));
        } else {
            strats.add(new SpawnerStrategy(
                new ScoutSpawner(),
                new ScoutSpawner(),
                new LoopedSpawner(
                        SRPSpawner::new,
                        SplasherSRPSpawner::new,
                        OffenceMopperSpawner::new
                )
            ));
        }

    }

    @Override
    public void runTick() throws GameActionException {
        indicate("STARTER");
        for (TowerStrategy strat : strats) {
            strat.runTick();
        }
    }
}
