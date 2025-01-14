package caterpillow_v1.robot.towers;

import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.UnitType;
import caterpillow_v1.Game;
import static caterpillow_v1.Game.gameStage;
import static caterpillow_v1.Game.pm;
import static caterpillow_v1.Game.rc;
import caterpillow_v1.packet.packets.SeedPacket;
import caterpillow_v1.packet.packets.StrategyPacket;
import caterpillow_v1.robot.Strategy;
import static caterpillow_v1.util.Util.getClosestNeighbourTo;
import static caterpillow_v1.util.Util.println;

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
