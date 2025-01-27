package fix_atk_micro.robot.towers.spawner.splasher;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import fix_atk_micro.packet.packets.SeedPacket;
import fix_atk_micro.packet.packets.StrategyPacket;
import fix_atk_micro.robot.agents.splasher.Splasher;
import fix_atk_micro.robot.towers.spawner.Spawner;

import static battlecode.common.UnitType.SPLASHER;
import static fix_atk_micro.util.Util.*;
import static fix_atk_micro.Game.*;

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
            MapInfo loc = getSpawnLoc(SPLASHER);
            if (loc != null && rc.canBuildRobot(SPLASHER, loc.getMapLocation())) {
                bot.build(SPLASHER, loc.getMapLocation(), new SeedPacket(trng.nextInt()), new StrategyPacket(Splasher.AGGRO_STRAT));
                return true;
            }
        }
        return false;
    }
}
