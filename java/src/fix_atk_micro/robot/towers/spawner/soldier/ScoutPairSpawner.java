package fix_atk_micro.robot.towers.spawner.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import fix_atk_micro.packet.packets.SeedPacket;
import fix_atk_micro.packet.packets.StrategyPacket;
import fix_atk_micro.robot.agents.soldier.Soldier;
import fix_atk_micro.robot.towers.spawner.Spawner;

import static battlecode.common.UnitType.SOLDIER;
import static fix_atk_micro.Game.rc;
import static fix_atk_micro.Game.trng;
import static fix_atk_micro.util.Util.getNeighbourSpawnLoc;

public class ScoutPairSpawner extends Spawner {

    int seed;
    int todo;

    public ScoutPairSpawner() {
        seed = trng.nextInt();
        todo = 2;
    }

    @Override
    public boolean spawn() throws GameActionException {
        if (rc.getChips() >= SOLDIER.moneyCost + 1000) {
            MapInfo loc = getNeighbourSpawnLoc(SOLDIER);
            if (loc != null && rc.canBuildRobot(SOLDIER, loc.getMapLocation())) {
                bot.build(SOLDIER, loc.getMapLocation(), new SeedPacket(seed), new StrategyPacket(Soldier.SCOUT_STRAT));
                todo--;
            }
        }
        return todo == 0;
    }
}
