package fix_atk_micro.robot.towers.spawner.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import fix_atk_micro.packet.packets.SeedPacket;
import fix_atk_micro.packet.packets.StrategyPacket;
import fix_atk_micro.robot.agents.soldier.Soldier;
import fix_atk_micro.robot.towers.spawner.Spawner;
import fix_atk_micro.util.Profiler;

import static battlecode.common.UnitType.SOLDIER;
import static fix_atk_micro.util.Util.*;
import static fix_atk_micro.Game.*;

public class ScoutSpawner extends Spawner {
    int ticksShouldSpawn = 0;
    protected boolean shouldSpawn() {
        if (rc.getChips() >= 1000) ticksShouldSpawn++;
        else ticksShouldSpawn = 0;
        return ticksShouldSpawn > 2 || rc.getChips() >= SOLDIER.moneyCost + 1000;
    }

    @Override
    public boolean spawn() throws GameActionException {
        if (shouldSpawn()) {
            MapInfo loc = getNeighbourSpawnLoc(SOLDIER);
            if (loc != null && rc.canBuildRobot(SOLDIER, loc.getMapLocation())) {
                MapLocation target = bot.scoutTarget();
                bot.build(SOLDIER, loc.getMapLocation(), new SeedPacket(trng.nextInt()), new StrategyPacket(Soldier.SCOUT_STRAT, encodeLoc(target)));
                return true;
            }
        }
        return false;
    }
}
