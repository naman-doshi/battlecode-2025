package caterpillow.robot.towers.spawner.soldier;

import battlecode.common.*;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.agents.soldier.Soldier;
import caterpillow.robot.towers.spawner.Spawner;

import static battlecode.common.UnitType.SOLDIER;
import static caterpillow.Game.rc;
import static caterpillow.Game.trng;
import static caterpillow.util.Util.getNeighbourSpawnLoc;

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
            MapLocation loc = getNeighbourSpawnLoc(SOLDIER);
            if (loc != null && rc.canBuildRobot(SOLDIER, loc)) {
                bot.build(SOLDIER, loc, new SeedPacket(seed), new StrategyPacket(Soldier.SCOUT_STRAT));
                todo--;
            }
        }
        return todo == 0;
    }
}
