package caterpillow.robot.towers.spawner.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
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
        MapInfo loc = getNeighbourSpawnLoc(SOLDIER);
        if(time < 4 && loc == null) loc = getNearestCell(c -> rc.canBuildRobot(SOLDIER, c.getMapLocation()));
        if (loc != null && rc.canBuildRobot(SOLDIER, loc.getMapLocation())) {
            bot.build(SOLDIER, loc.getMapLocation(), new SeedPacket(trng.nextInt()), new StrategyPacket(Soldier.RUSH_STRAT, distanceThreshold));
            return true;
        }
        return false;
    }
}
