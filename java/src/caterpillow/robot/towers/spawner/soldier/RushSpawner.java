package caterpillow.robot.towers.spawner.soldier;

import battlecode.common.*;
import static battlecode.common.UnitType.SOLDIER;
import static caterpillow.Game.*;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.agents.soldier.Soldier;
import caterpillow.robot.towers.spawner.Spawner;

import static caterpillow.util.Util.*;
import static caterpillow.tracking.CellTracker.*;

public class RushSpawner extends Spawner {
    int distanceThreshold;
    public RushSpawner() {
        distanceThreshold = 30;
    }
    public RushSpawner(int distanceThreshold) {
        this.distanceThreshold = distanceThreshold;
    }

    @Override
    public boolean spawn() throws GameActionException {
        MapLocation loc = getNeighbourSpawnLoc(SOLDIER);
        if(time < 4 && loc == null) loc = getNearestLocation(loc2 -> rc.canBuildRobot(SOLDIER, loc2));
        if (loc != null && rc.canBuildRobot(SOLDIER, loc)) {
            bot.build(SOLDIER, loc, new SeedPacket(trng.nextInt()), new StrategyPacket(Soldier.RUSH_STRAT, distanceThreshold));
            return true;
        }
        return false;
    }
}
