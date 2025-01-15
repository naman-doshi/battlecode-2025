package caterpillow_v1.robot.towers;

import battlecode.common.*;
import caterpillow_v1.Game;
import caterpillow_v1.packet.packets.SeedPacket;
import caterpillow_v1.packet.packets.StrategyPacket;

import java.util.Random;

import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;

public class RusherSpawnStrategy extends TowerStrategy {

    // in case we get rushed
    int todo, seed;
    Tower bot;

    public RusherSpawnStrategy() {
        bot = (Tower) Game.bot;
        seed = new Random(rc.getID()).nextInt();
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
                    // this part uses too much compute
//                    MapInfo spawn = getClosestCellTo(centre, cell -> connectedByPaint(cell.getMapLocation(), rc.getLocation(), true) && rc.canBuildRobot(UnitType.SOLDIER, cell.getMapLocation()));
//                    if (spawn != null) {
//                        spawnSoldier(spawn.getMapLocation(), 1);
//                        return;
//                    }
                    // just spawn adjacent
                    MapInfo spawn = getClosestNeighbourTo(rc.getLocation(), cell -> cell.getMapLocation().distanceSquaredTo(rc.getLocation()) == 1 && !cell.getPaint().isEnemy() && rc.canBuildRobot(UnitType.SOLDIER, cell.getMapLocation()));
                    if (spawn != null) {
                        spawnSoldier(spawn.getMapLocation(), 1);
                        return;
                    }
                }
                break;
        }
    }
}
