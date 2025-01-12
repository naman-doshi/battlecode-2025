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

public class NormalPaintTowerStrategy extends TowerStrategy {

    // in case we get rushed
    int todo, seed;
    Tower bot;

    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public NormalPaintTowerStrategy() {
        bot = (Tower) Game.bot;
        seed = new Random(rc.getID()).nextInt();

        strats = new ArrayList<>();
        strats.add(new RespawnStrategy());
        strats.add(new TowerAttackStrategy());
    }

    private void spawnSoldier(MapLocation loc, int strat) throws GameActionException {
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
        MapInfo spawn = getSafeSpawnLoc(UnitType.SOLDIER);
        if (spawn != null && rc.canBuildRobot(UnitType.SOLDIER, spawn.getMapLocation())) {
            spawnSoldier(spawn.getMapLocation(), rng.nextInt(0, 2));
        }
    }
}
