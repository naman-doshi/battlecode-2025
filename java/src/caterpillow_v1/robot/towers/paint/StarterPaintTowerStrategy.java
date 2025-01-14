package caterpillow_v1.robot.towers.paint;

import battlecode.common.*;
import caterpillow_v1.Game;
import caterpillow_v1.packet.packets.SeedPacket;
import caterpillow_v1.packet.packets.StrategyPacket;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.robot.towers.*;
import caterpillow_v1.robot.towers.spawner.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;

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
