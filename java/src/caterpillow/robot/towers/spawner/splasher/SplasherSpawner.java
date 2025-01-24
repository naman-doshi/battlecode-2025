package caterpillow.robot.towers.spawner.splasher;

import battlecode.common.*;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.agents.splasher.Splasher;
import caterpillow.robot.towers.spawner.Spawner;

import static battlecode.common.UnitType.SPLASHER;
import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class SplasherSpawner extends Spawner {
    int ticksShouldSpawn = 0;
    protected boolean shouldSpawn() {
        return rc.getChips() >= 1000;
    }

    @Override
    public boolean spawn() throws GameActionException {
        if (shouldSpawn()) ticksShouldSpawn++;
        else ticksShouldSpawn = 0;
        if (ticksShouldSpawn > 2) { // wait 2 ticks
            MapLocation loc = getSpawnLoc(SPLASHER);
            if (loc != null && rc.canBuildRobot(SPLASHER, loc)) {
                bot.build(SPLASHER, loc, new SeedPacket(trng.nextInt()), new StrategyPacket(Splasher.AGGRO_STRAT));
                return true;
            }
        }
        return false;
    }
}
