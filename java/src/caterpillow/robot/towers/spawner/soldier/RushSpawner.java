package caterpillow.robot.towers.spawner.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import static battlecode.common.UnitType.SOLDIER;
import static caterpillow.Game.rc;
import static caterpillow.Game.trng;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.agents.soldier.Soldier;
import caterpillow.robot.towers.spawner.Spawner;

import static caterpillow.util.Util.getNeighbourSpawnLoc;

public class RushSpawner extends Spawner {
    @Override
    public boolean spawn() throws GameActionException {
        if (rc.getChips() >= SOLDIER.moneyCost + 1000) {
            MapInfo loc = getNeighbourSpawnLoc(SOLDIER);
            if (loc != null && rc.canBuildRobot(SOLDIER, loc.getMapLocation())) {
                bot.build(SOLDIER, loc.getMapLocation(), new SeedPacket(trng.nextInt()), new StrategyPacket(Soldier.RUSH_STRAT));
                return true;
            }
        }
        return false;
    }
}
