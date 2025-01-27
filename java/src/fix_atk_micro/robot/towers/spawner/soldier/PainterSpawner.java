package fix_atk_micro.robot.towers.spawner.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import static battlecode.common.UnitType.SOLDIER;
import static fix_atk_micro.Game.rc;
import static fix_atk_micro.Game.trng;
import fix_atk_micro.packet.packets.SeedPacket;
import fix_atk_micro.packet.packets.StrategyPacket;
import fix_atk_micro.robot.agents.soldier.Soldier;
import fix_atk_micro.robot.towers.spawner.Spawner;
import static fix_atk_micro.util.Util.encodeLoc;
import static fix_atk_micro.util.Util.getNeighbourSpawnLoc;

public class PainterSpawner extends Spawner {
    int ticksShouldSpawn = 0;
    protected boolean shouldSpawn() {
        return rc.getChips() >= SOLDIER.moneyCost + 1000;
    }

    @Override
    public boolean spawn() throws GameActionException {
        if (shouldSpawn()) {
            MapInfo loc = getNeighbourSpawnLoc(SOLDIER);
            if (loc != null && rc.canBuildRobot(SOLDIER, loc.getMapLocation())) {
                MapLocation target = bot.scoutTarget();
                bot.build(SOLDIER, loc.getMapLocation(), new SeedPacket(trng.nextInt()), new StrategyPacket(Soldier.PAINT_EVERYWHERE_STRAT, encodeLoc(target)));
                System.out.println("Spawning soldier at " + loc.getMapLocation());
                return true;
            }
        }
        return false;
    }
}
