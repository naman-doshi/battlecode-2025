package fix_atk_micro.robot.towers.spawner.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import static battlecode.common.UnitType.SOLDIER;
import static fix_atk_micro.Game.*;
import fix_atk_micro.packet.packets.SeedPacket;
import fix_atk_micro.packet.packets.StrategyPacket;
import fix_atk_micro.robot.agents.soldier.Soldier;
import fix_atk_micro.robot.towers.spawner.Spawner;

import static fix_atk_micro.util.Util.*;
import static fix_atk_micro.tracking.CellTracker.*;

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
