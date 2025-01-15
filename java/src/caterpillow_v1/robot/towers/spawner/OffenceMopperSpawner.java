package caterpillow_v1.robot.towers.spawner;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import caterpillow_v1.packet.packets.SeedPacket;
import caterpillow_v1.packet.packets.StrategyPacket;
import caterpillow_v1.robot.agents.mopper.Mopper;

import static battlecode.common.UnitType.MOPPER;
import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;

public class OffenceMopperSpawner extends Spawner {
    @Override
    public boolean spawn() throws GameActionException {
        if (rc.getChips() >= MOPPER.moneyCost + 1000) {
            MapInfo loc = getSpawnLoc(MOPPER);
            if (loc != null && rc.canBuildRobot(MOPPER, loc.getMapLocation())) {
                bot.build(MOPPER, loc.getMapLocation(), new SeedPacket(trng.nextInt()), new StrategyPacket(Mopper.OFFENCE_STRAT));
                return true;
            }
        }
        return false;
    }
}
