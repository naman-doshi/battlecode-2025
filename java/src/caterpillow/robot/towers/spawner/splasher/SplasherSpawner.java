package caterpillow.robot.towers.spawner.splasher;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.agents.splasher.Splasher;
import caterpillow.robot.towers.spawner.Spawner;

import static battlecode.common.UnitType.SPLASHER;
import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

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
