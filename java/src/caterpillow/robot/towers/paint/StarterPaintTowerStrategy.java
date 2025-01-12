package caterpillow.robot.towers.paint;

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

public class StarterPaintTowerStrategy extends TowerStrategy {

    List<TowerStrategy> strats;
    // in case we get rushed
    int todo, seed;
    Tower bot;

    public StarterPaintTowerStrategy() {
        bot = (Tower) Game.bot;
        seed = new Random(rc.getID()).nextInt();

        strats = new ArrayList<>();
        strats.add(new RespawnStrategy());
        strats.add(new TowerAttackStrategy());
        strats.add(new RefillStrategy());
    }

    private void spawnSoldier(MapLocation loc, int strat) throws GameActionException {
        println("spawning soldier!\n");
        bot.build(UnitType.SOLDIER, loc);
        pm.send(loc, new SeedPacket(seed));
        pm.send(loc, new StrategyPacket(strat));
        todo--;
    }

    @Override
    public void runTick() throws GameActionException {
        for (TowerStrategy strat : strats) {
            strat.runTick();
        }
        switch (gameStage) {
            // TODO: implement strategies for all game times
            // currently just spam soldiers
            case LATE:
            case MID:
            case EARLY:
                if (rc.getPaint() >= 2 * UnitType.SOLDIER.paintCost && rc.getChips() >= 2 * UnitType.SOLDIER.moneyCost) {
                    todo = 2;
                    Random random = new Random(seed);
                    seed = random.nextInt();
                }
                if (todo > 0) {
                    // just spawn adjacent
                    MapInfo spawn = getSafeSpawnLoc(UnitType.SOLDIER);
                    if (spawn != null && rc.canBuildRobot(UnitType.SOLDIER, spawn.getMapLocation())) {
                        spawnSoldier(spawn.getMapLocation(), 0);
                        return;
                    }
                }
                break;
        }
    }
}
