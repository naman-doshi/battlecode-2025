package fix_atk_micro.robot.towers;

import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.UnitType;
import fix_atk_micro.Game;
import static fix_atk_micro.Game.gameStage;
import static fix_atk_micro.Game.pm;
import static fix_atk_micro.Game.rc;
import fix_atk_micro.packet.packets.SeedPacket;
import fix_atk_micro.packet.packets.StrategyPacket;
import fix_atk_micro.robot.Strategy;
import static fix_atk_micro.util.Util.getClosestNeighbourTo;
import static fix_atk_micro.util.Util.println;

public class SplasherSpawnStrategy extends Strategy {

    // in case we get rushed
    int todo, seed;
    Tower bot;

    public SplasherSpawnStrategy() {
        bot = (Tower) Game.bot;
        seed = new Random(rc.getID()).nextInt();
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    private void spawnMopper(MapLocation loc, int strat) throws GameActionException {
        println("spawning splasher!\n");
        bot.build(UnitType.SPLASHER, loc);
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
                // strat 1 = offence
                MapInfo spawn = getClosestNeighbourTo(rc.getLocation(), cell -> cell.getMapLocation().distanceSquaredTo(rc.getLocation()) == 1 && !cell.getPaint().isEnemy() && rc.canBuildRobot(UnitType.SPLASHER, cell.getMapLocation()));
                if (spawn != null) {
                    spawnMopper(spawn.getMapLocation(), 0);
                }
        }
    }
}
