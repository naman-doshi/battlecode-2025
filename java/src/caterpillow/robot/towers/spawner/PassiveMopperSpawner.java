package caterpillow.robot.towers.spawner;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.agents.mopper.Mopper;

import static battlecode.common.UnitType.MOPPER;
import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class PassiveMopperSpawner extends Spawner {
    @Override
    public boolean spawn() throws GameActionException {
        if (rc.getChips() >= MOPPER.moneyCost + 1000) {
            MapInfo loc = getSpawnLoc(MOPPER);
            if (loc != null && rc.canBuildRobot(MOPPER, loc.getMapLocation())) {
                bot.build(MOPPER, loc.getMapLocation(), new SeedPacket(trng.nextInt()), new StrategyPacket(Mopper.PASSIVE_STRAT));
                return true;
            }
        }
        return false;
    }
}
