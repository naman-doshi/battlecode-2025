package fix_atk_micro.robot.towers.spawner.mopper;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import fix_atk_micro.packet.packets.SeedPacket;
import fix_atk_micro.packet.packets.StrategyPacket;
import fix_atk_micro.robot.agents.mopper.Mopper;
import fix_atk_micro.robot.towers.spawner.Spawner;

import static battlecode.common.UnitType.MOPPER;
import static fix_atk_micro.util.Util.*;
import static fix_atk_micro.Game.*;

public class PassiveMopperSpawner extends Spawner {
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
                bot.build(MOPPER, loc.getMapLocation(), new SeedPacket(trng.nextInt()), new StrategyPacket(Mopper.PASSIVE_STRAT));
                return true;
            }
        }
        return false;
    }
}
