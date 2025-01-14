package caterpillow.robot.towers.paint;

import battlecode.common.*;
import caterpillow.Game;
import caterpillow.robot.towers.*;
import caterpillow.robot.towers.spawner.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class StarterPaintTowerStrategy extends TowerStrategy {

    List<TowerStrategy> strats;
    // in case we get rushed
    int todo;
    Tower bot;
    Random rng;

    public StarterPaintTowerStrategy() {
        todo = 1;
        bot = (Tower) Game.bot;
        rng = new Random(seed);
        strats = new ArrayList<>();
        strats.add(new RespawnStrategy());
        strats.add(new TowerAttackStrategy());
        strats.add(new SpawnerStrategy(
                    new ScoutSpawner(),
                    new ScoutSpawner(),
                    new LoopedSpawner(
                            new SRPSpawner(),
                            new OffenceMopperSpawner()
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
