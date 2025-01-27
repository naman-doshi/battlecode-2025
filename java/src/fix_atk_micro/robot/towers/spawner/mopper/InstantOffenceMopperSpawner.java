package fix_atk_micro.robot.towers.spawner.mopper;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import fix_atk_micro.packet.packets.SeedPacket;
import fix_atk_micro.packet.packets.StrategyPacket;
import fix_atk_micro.robot.agents.mopper.Mopper;
import fix_atk_micro.robot.towers.spawner.Spawner;

import static battlecode.common.UnitType.MOPPER;
import static fix_atk_micro.util.Util.*;
import static fix_atk_micro.Game.*;

public class InstantOffenceMopperSpawner extends Spawner {
    @Override
    public boolean spawn() throws GameActionException {
        MapInfo loc = getSpawnLoc(MOPPER);
        if (loc != null && rc.canBuildRobot(MOPPER, loc.getMapLocation())) {
            bot.build(MOPPER, loc.getMapLocation(), new SeedPacket(trng.nextInt()), new StrategyPacket(Mopper.OFFENCE_STRAT));
            return true;
        }
        return false;
    }
}
