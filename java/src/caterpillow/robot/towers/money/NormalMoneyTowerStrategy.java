package caterpillow.robot.towers.money;

import battlecode.common.*;
import caterpillow.Game;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.towers.*;
import caterpillow.util.CyclicQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static caterpillow.Game.seed;
import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

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
