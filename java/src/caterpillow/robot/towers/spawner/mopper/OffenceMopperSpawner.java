package caterpillow.robot.towers.spawner.mopper;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.agents.mopper.Mopper;
import caterpillow.robot.towers.spawner.Spawner;

import static battlecode.common.UnitType.MOPPER;
import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class OffenceMopperSpawner extends Spawner {
    int ticksShouldSpawn = 0;
    protected boolean shouldSpawn() {
        return rc.getChips() >= 1000;
    }

    @Override
    public boolean spawn() throws GameActionException {
        if (shouldSpawn()) ticksShouldSpawn++;
        else ticksShouldSpawn = 0;
        if (ticksShouldSpawn > 2) { // wait 2 ticks
            MapInfo loc = getSpawnLoc(MOPPER);
            if (loc != null && rc.canBuildRobot(MOPPER, loc.getMapLocation())) {
                bot.build(MOPPER, loc.getMapLocation(), new SeedPacket(trng.nextInt()), new StrategyPacket(Mopper.OFFENCE_STRAT));
                return true;
            }
        }
        return false;
    }
}
