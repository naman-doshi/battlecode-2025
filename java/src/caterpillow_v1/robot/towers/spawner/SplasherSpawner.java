package caterpillow_v1.robot.towers.spawner;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import caterpillow_v1.packet.packets.SeedPacket;
import caterpillow_v1.packet.packets.StrategyPacket;
import caterpillow_v1.robot.agents.splasher.Splasher;

import static battlecode.common.UnitType.SPLASHER;
import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;

public class SplasherSpawner extends Spawner {
    @Override
    public boolean spawn() throws GameActionException {
        if (rc.getChips() >= SPLASHER.moneyCost + 1000) {
            MapInfo loc = getSpawnLoc(SPLASHER);
            if (loc != null && rc.canBuildRobot(SPLASHER, loc.getMapLocation())) {
                bot.build(SPLASHER, loc.getMapLocation(), new SeedPacket(trng.nextInt()), new StrategyPacket(Splasher.AGGRO_STRAT));
                return true;
            }
        }
        return false;
    }
}
