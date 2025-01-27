package caterpillow.robot.towers;

import battlecode.common.*;
import caterpillow.Game;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;

import caterpillow.util.CustomRandom;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class RusherSpawnStrategy extends TowerStrategy {

    // in case we get rushed
    int todo, seed;
    Tower bot;

    public RusherSpawnStrategy() {
        bot = (Tower) Game.bot;
        seed = new CustomRandom(rc.getID()).nextInt();
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
            case LATE:
            case MID:
            case EARLY:
                if (rc.getPaint() >= 2 * UnitType.SOLDIER.paintCost && rc.getChips() >= 2 * UnitType.SOLDIER.moneyCost) {
                    todo = 2;
                    CustomRandom random = new CustomRandom(seed);
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
