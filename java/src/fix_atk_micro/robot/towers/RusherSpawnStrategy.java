package fix_atk_micro.robot.towers;

import battlecode.common.*;
import fix_atk_micro.Game;
import fix_atk_micro.packet.packets.SeedPacket;
import fix_atk_micro.packet.packets.StrategyPacket;

import java.util.Random;

import static fix_atk_micro.util.Util.*;
import static fix_atk_micro.Game.*;

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
