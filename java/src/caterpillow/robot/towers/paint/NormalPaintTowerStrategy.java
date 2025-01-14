package caterpillow.robot.towers.paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.Game.seed;
import static caterpillow.Game.trng;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.towers.RespawnStrategy;
import caterpillow.robot.towers.Tower;
import caterpillow.robot.towers.TowerAttackStrategy;
import caterpillow.robot.towers.TowerStrategy;
import static caterpillow.util.Util.TOWER_COST;
import static caterpillow.util.Util.getSafeSpawnLoc;
import static caterpillow.util.Util.indicate;

public class NormalPaintTowerStrategy extends TowerStrategy {

    // in case we get rushed
    int nxt;
    Random rng;
    Tower bot;

    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public NormalPaintTowerStrategy() {
        bot = (Tower) Game.bot;
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

        int numTowers = rc.getNumberTowers();

        if (numTowers <= 2) {
            // shit code
            if (nxt <= 3) {
                MapInfo spawn = getSafeSpawnLoc(UnitType.SOLDIER);
                if (spawn != null && rc.getPaint() >= UnitType.SOLDIER.paintCost + UnitType.MOPPER.paintCost && rc.getChips() >= UnitType.SOLDIER.moneyCost + UnitType.MOPPER.moneyCost + TOWER_COST) {
                    bot.build(UnitType.SOLDIER, spawn.getMapLocation(), new SeedPacket(rng.nextInt()), new StrategyPacket(2));
                    nxt = trng.nextInt(10);
                }
            } else {
                MapInfo spawn = getSafeSpawnLoc(UnitType.SOLDIER);
                if (spawn != null && rc.getPaint() >= UnitType.SOLDIER.paintCost + UnitType.MOPPER.paintCost && rc.getChips() >= UnitType.SOLDIER.moneyCost + UnitType.MOPPER.moneyCost + TOWER_COST) {
                    bot.build(UnitType.SOLDIER, spawn.getMapLocation(), new SeedPacket(rng.nextInt()), new StrategyPacket(1));
                    nxt = trng.nextInt(10);
                }
            }
        } else if (numTowers < 20) {
            if (nxt <= 2) {
                MapInfo spawn = getSafeSpawnLoc(UnitType.SOLDIER);
                if (spawn != null && rc.getPaint() >= UnitType.SOLDIER.paintCost + UnitType.MOPPER.paintCost && rc.getChips() >= UnitType.SOLDIER.moneyCost + UnitType.MOPPER.moneyCost + TOWER_COST) {
                    bot.build(UnitType.SOLDIER, spawn.getMapLocation(), new SeedPacket(rng.nextInt()), new StrategyPacket(2));
                    nxt = trng.nextInt(10);
                }
            } else if (nxt <= 5) {
                MapInfo spawn = getSafeSpawnLoc(UnitType.SOLDIER);
                if (spawn != null && rc.getPaint() >= UnitType.SOLDIER.paintCost + UnitType.MOPPER.paintCost && rc.getChips() >= UnitType.SOLDIER.moneyCost + UnitType.MOPPER.moneyCost + TOWER_COST) {
                    bot.build(UnitType.SOLDIER, spawn.getMapLocation(), new SeedPacket(rng.nextInt()), new StrategyPacket(1));
                    nxt = trng.nextInt(10);
                }
            } else if (nxt <= 6) {
                MapInfo spawn = getSafeSpawnLoc(UnitType.MOPPER);
                if (spawn != null && rc.getPaint() >= UnitType.MOPPER.paintCost + UnitType.MOPPER.paintCost && rc.getChips() >= UnitType.MOPPER.moneyCost + UnitType.MOPPER.moneyCost + TOWER_COST) {
                    bot.build(UnitType.MOPPER, spawn.getMapLocation(), new SeedPacket(rng.nextInt()), new StrategyPacket(1));
                    nxt = trng.nextInt(10);
                }
            } else {
                MapInfo spawn = getSafeSpawnLoc(UnitType.SPLASHER);
                if (spawn != null && rc.getPaint() >= UnitType.SPLASHER.paintCost + UnitType.MOPPER.paintCost && rc.getChips() >= UnitType.SPLASHER.moneyCost + UnitType.MOPPER.moneyCost + TOWER_COST) {
                    bot.build(UnitType.SPLASHER, spawn.getMapLocation(), new SeedPacket(rng.nextInt()), new StrategyPacket(0));
                    nxt = trng.nextInt(10);
                }
            }
        } else {
            if (nxt <= 1) {
                MapInfo spawn = getSafeSpawnLoc(UnitType.SOLDIER);
                if (spawn != null && rc.getPaint() >= UnitType.SOLDIER.paintCost + UnitType.MOPPER.paintCost && rc.getChips() >= UnitType.SOLDIER.moneyCost + UnitType.MOPPER.moneyCost + TOWER_COST) {
                    bot.build(UnitType.SOLDIER, spawn.getMapLocation(), new SeedPacket(rng.nextInt()), new StrategyPacket(2));
                    nxt = trng.nextInt(10);
                }
            } else if (nxt <= 5) {
                MapInfo spawn = getSafeSpawnLoc(UnitType.SOLDIER);
                if (spawn != null && rc.getPaint() >= UnitType.SOLDIER.paintCost + UnitType.MOPPER.paintCost && rc.getChips() >= UnitType.SOLDIER.moneyCost + UnitType.MOPPER.moneyCost + TOWER_COST) {
                    bot.build(UnitType.SOLDIER, spawn.getMapLocation(), new SeedPacket(rng.nextInt()), new StrategyPacket(1));
                    nxt = trng.nextInt(10);
                }
            } else if (nxt <= 6) {
                MapInfo spawn = getSafeSpawnLoc(UnitType.MOPPER);
                if (spawn != null && rc.getPaint() >= UnitType.MOPPER.paintCost + UnitType.MOPPER.paintCost && rc.getChips() >= UnitType.MOPPER.moneyCost + UnitType.MOPPER.moneyCost + TOWER_COST) {
                    bot.build(UnitType.MOPPER, spawn.getMapLocation(), new SeedPacket(rng.nextInt()), new StrategyPacket(1));
                    nxt = trng.nextInt(10);
                }
            } else {
                MapInfo spawn = getSafeSpawnLoc(UnitType.SPLASHER);
                if (spawn != null && rc.getPaint() >= UnitType.SPLASHER.paintCost + UnitType.MOPPER.paintCost && rc.getChips() >= UnitType.SPLASHER.moneyCost + UnitType.MOPPER.moneyCost + TOWER_COST) {
                    bot.build(UnitType.SPLASHER, spawn.getMapLocation(), new SeedPacket(rng.nextInt()), new StrategyPacket(0));
                    nxt = trng.nextInt(10);
                }
            }
        }
    }
}
