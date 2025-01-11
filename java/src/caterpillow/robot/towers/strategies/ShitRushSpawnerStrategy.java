package caterpillow.robot.towers.strategies;

import battlecode.common.*;
import caterpillow.Game;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Soldier;
import caterpillow.robot.towers.Tower;
import caterpillow.world.GameStage;

import java.util.Random;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class ShitRushSpawnerStrategy extends Strategy {

    // in case we get rushed
    int todo, seed;
    Tower bot;

    public ShitRushSpawnerStrategy() {
        bot = (Tower) Game.bot;
        seed = new Random(rc.getID()).nextInt();
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    private void spawnSoldier(MapLocation loc, int strat) throws GameActionException {
        bot.build(UnitType.SOLDIER, loc);
        pm.send(loc, new SeedPacket(seed));
        pm.send(loc, new StrategyPacket(strat));
        todo--;
    }

    @Override
    public void runTick() throws GameActionException {
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
                    // build soldiers
                    // this might be computationally expensive
                    // try to spawn as close as possible to the centre
                    MapInfo spawn = getClosestCellTo(centre, cell -> connectedByPaint(cell.getMapLocation(), rc.getLocation(), true) && rc.canBuildRobot(UnitType.SOLDIER, cell.getMapLocation()));
                    if (spawn != null) {
                        spawnSoldier(spawn.getMapLocation(), 2);
                        return;
                    }
                    // just spawn adjacent
                    spawn = getClosestNeighbourTo(rc.getLocation(), cell -> cell.getMapLocation().distanceSquaredTo(rc.getLocation()) == 1 && !cell.getPaint().isEnemy() && rc.canBuildRobot(UnitType.SOLDIER, cell.getMapLocation()));
                    if (spawn != null) {
                        spawnSoldier(spawn.getMapLocation(), 2);
                        return;
                    }
                }
                break;
        }
    }
}
