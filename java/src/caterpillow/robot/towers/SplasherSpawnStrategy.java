package caterpillow.robot.towers;

import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.UnitType;
import caterpillow.Game;
import static caterpillow.Game.gameStage;
import static caterpillow.Game.pm;
import static caterpillow.Game.rc;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.Strategy;
import static caterpillow.util.Util.getClosestNeighbourTo;
import static caterpillow.util.Util.println;

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
