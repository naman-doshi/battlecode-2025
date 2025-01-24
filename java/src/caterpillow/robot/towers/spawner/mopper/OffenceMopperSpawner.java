package caterpillow.robot.towers.spawner.mopper;

import battlecode.common.*;
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
        if (rc.getChips() >= 1000) ticksShouldSpawn++;
        else ticksShouldSpawn = 0;
        return ticksShouldSpawn > 2 || rc.getChips() >= MOPPER.moneyCost + 1000;
    }

    @Override
    public boolean spawn() throws GameActionException {
        if (shouldSpawn()) {
            MapLocation loc = getSpawnLoc(MOPPER);
            if (loc != null && rc.canBuildRobot(MOPPER, loc)) {
                bot.build(MOPPER, loc, new SeedPacket(trng.nextInt()), new StrategyPacket(Mopper.OFFENCE_STRAT));
                return true;
            }
        }
        return false;
    }
}
