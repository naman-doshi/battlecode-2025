package caterpillow.robot.towers.spawner.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.agents.soldier.Soldier;
import caterpillow.robot.towers.spawner.Spawner;
import caterpillow.util.Profiler;

import static battlecode.common.UnitType.SOLDIER;
import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class ScoutSpawner extends Spawner {
    int ticksShouldSpawn = 0;
    protected boolean shouldSpawn() {
        return rc.getChips() >= 1000;
    }

    @Override
    public boolean spawn() throws GameActionException {
        if (shouldSpawn()) ticksShouldSpawn++;
        else ticksShouldSpawn = 0;
        if (ticksShouldSpawn > 2) { // wait 2 ticks
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
