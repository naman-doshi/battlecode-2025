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
import static caterpillow.tracking.CellTracker.*;

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
            MapLocation loc = getNeighbourSpawnLoc(SOLDIER);
            if(time < 4 && loc == null) loc = getNearestLocation(loc2 -> rc.canBuildRobot(SOLDIER, loc2));
            if (loc != null && rc.canBuildRobot(SOLDIER, loc)) {
                MapLocation target = bot.scoutTarget();
                bot.build(SOLDIER, loc, new SeedPacket(trng.nextInt()), new StrategyPacket(Soldier.SCOUT_STRAT, encodeLoc(target)));
                return true;
            }
        }
        return false;
    }
}
