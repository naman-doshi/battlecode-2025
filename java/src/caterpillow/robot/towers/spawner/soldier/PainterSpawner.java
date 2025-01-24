package caterpillow.robot.towers.spawner.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import static battlecode.common.UnitType.SOLDIER;
import static caterpillow.Game.rc;
import static caterpillow.Game.trng;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.agents.soldier.Soldier;
import caterpillow.robot.towers.spawner.Spawner;
import static caterpillow.util.Util.encodeLoc;
import static caterpillow.util.Util.getNeighbourSpawnLoc;

public class PainterSpawner extends Spawner {
    int ticksShouldSpawn = 0;
    protected boolean shouldSpawn() {
        return rc.getChips() >= SOLDIER.moneyCost + 1000;
    }

    @Override
    public boolean spawn() throws GameActionException {
        if (shouldSpawn()) {
            MapLocation loc = getNeighbourSpawnLoc(SOLDIER);
            if (loc != null && rc.canBuildRobot(SOLDIER, loc)) {
                MapLocation target = bot.scoutTarget();
                bot.build(SOLDIER, loc, new SeedPacket(trng.nextInt()), new StrategyPacket(Soldier.PAINT_EVERYWHERE_STRAT, encodeLoc(target)));
                System.out.println("Spawning soldier at " + loc);
                return true;
            }
        }
        return false;
    }
}
