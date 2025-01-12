package caterpillow.robot.towers.money;

import battlecode.common.*;
import caterpillow.Game;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.Strategy;
import caterpillow.robot.towers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class StarterMoneyTowerStrategy extends TowerStrategy {

    // in case we get rushed
    int todo;
    Random rng;
    Tower bot;

    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public StarterMoneyTowerStrategy() {
        todo = 2;
        bot = (Tower) Game.bot;
        rng = new Random(seed);
        strats = new ArrayList<>();
        strats.add(new RespawnStrategy());
        strats.add(new TowerAttackStrategy());
    }

    @Override
    public void runTick() throws GameActionException {
        rc.setIndicatorString("STARTER");
        for (TowerStrategy strat : strats) {
            strat.runTick();
        }
        switch (gameStage) {
            // TODO: implement strategies for all game times
            // currently just spam soldiers
            case LATE:
            case MID:
            case EARLY:
                if (rc.getPaint() >= UnitType.SOLDIER.paintCost + UnitType.MOPPER.paintCost && rc.getChips() >= UnitType.SOLDIER.moneyCost + UnitType.MOPPER.moneyCost + TOWER_COST && time > 30) {
                    todo += 1;
                }
                if (todo > 0) {
                    // just spawn adjacent
                    MapInfo spawn = getSafeSpawnLoc(UnitType.SOLDIER);
                    if (spawn != null && rc.canBuildRobot(UnitType.SOLDIER, spawn.getMapLocation())) {
                        bot.build(UnitType.SOLDIER, spawn.getMapLocation(), new SeedPacket(rng.nextInt()), new StrategyPacket(0));
                        todo--;
                        return;
                    }
                }
                break;
        }
    }
}
