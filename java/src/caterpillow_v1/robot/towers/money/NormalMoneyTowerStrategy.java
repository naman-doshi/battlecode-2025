package caterpillow_v1.robot.towers.money;

import battlecode.common.*;
import caterpillow_v1.Game;
import caterpillow_v1.packet.packets.SeedPacket;
import caterpillow_v1.packet.packets.StrategyPacket;
import caterpillow_v1.robot.towers.*;
import caterpillow_v1.robot.towers.spawner.*;
import caterpillow_v1.util.CyclicQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static caterpillow_v1.Game.seed;
import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;

public class NormalMoneyTowerStrategy extends TowerStrategy {

    // in case we get rushed
    int seed;
    Random rng;
    Tower bot;
    int nxt;

    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public NormalMoneyTowerStrategy() {
        bot = (Tower) Game.bot;
        seed = new Random(rc.getID()).nextInt();
        rng = new Random(seed);

        strats = new ArrayList<>();
        strats.add(new RespawnStrategy());
        strats.add(new TowerAttackStrategy());
        strats.add(new SpawnerStrategy(
                new DelaySpawner(3),
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
        for (TowerStrategy strat : strats) {
            strat.runTick();
        }

        if (ticksExisted <= 3) {
            return;
        }

        // shit code
        if (nxt <= 2) {
            MapInfo spawn = getSafeSpawnLoc(UnitType.SOLDIER);
            if (spawn != null && rc.getPaint() >= UnitType.SOLDIER.paintCost + UnitType.MOPPER.paintCost && rc.getChips() >= UnitType.SOLDIER.moneyCost + UnitType.MOPPER.moneyCost + TOWER_COST) {
                bot.build(UnitType.SOLDIER, spawn.getMapLocation(), new SeedPacket(rng.nextInt()), new StrategyPacket(3));
                nxt = trng.nextInt(5);
            }
        } else if (nxt <= 3) {
            MapInfo spawn = getSafeSpawnLoc(UnitType.SOLDIER);
            if (spawn != null && rc.getPaint() >= UnitType.SOLDIER.paintCost + UnitType.MOPPER.paintCost && rc.getChips() >= UnitType.SOLDIER.moneyCost + UnitType.MOPPER.moneyCost + TOWER_COST) {
                bot.build(UnitType.SOLDIER, spawn.getMapLocation(), new SeedPacket(rng.nextInt()), new StrategyPacket(1));
                nxt = trng.nextInt(5);
            }
        } else if (nxt <= 4) {
            MapInfo spawn = getSafeSpawnLoc(UnitType.MOPPER);
            if (spawn != null && rc.getPaint() >= UnitType.MOPPER.paintCost + UnitType.MOPPER.paintCost && rc.getChips() >= UnitType.MOPPER.moneyCost + UnitType.MOPPER.moneyCost + TOWER_COST) {
                bot.build(UnitType.MOPPER, spawn.getMapLocation(), new SeedPacket(rng.nextInt()), new StrategyPacket(1));
                nxt = trng.nextInt(5);
            }
        }
    }
}
