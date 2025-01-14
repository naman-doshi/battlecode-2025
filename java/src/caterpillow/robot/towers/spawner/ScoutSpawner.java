package caterpillow.robot.towers.spawner;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.agents.soldier.Soldier;

import static battlecode.common.UnitType.SOLDIER;
import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class ScoutSpawner extends Spawner {
    @Override
    public boolean spawn() throws GameActionException {
        if (rc.getChips() >= SOLDIER.moneyCost + 1000) {
            MapInfo loc = getNeighbourSpawnLoc(SOLDIER);
            if (loc != null && rc.canBuildRobot(SOLDIER, loc.getMapLocation())) {
                bot.build(SOLDIER, loc.getMapLocation(), new SeedPacket(trng.nextInt()), new StrategyPacket(Soldier.SCOUT_STRAT));
                return true;
            }
        }
        return false;
    }
}
