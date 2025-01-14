package caterpillow_v1.robot.towers;

import battlecode.common.*;
import caterpillow_v1.Game;
import caterpillow_v1.packet.packets.StrategyPacket;

import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;

/*

currently, its kinda beatable since itll only spawn a mopper
todo: spawn moppers that clean bad cells (if applicable), and spawn soldiers that can refill cells

*/

public class RespawnStrategy extends TowerStrategy {

    int lastSpawnTime;
    Tower bot;

    public boolean isEnemyPaintBlocking() throws GameActionException {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                MapLocation loc = add(rc.getLocation(), new MapLocation(dx, dy));
                if (rc.senseMapInfo(loc).getPaint().isEnemy()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isTowerRespawnReady() throws GameActionException {
        return isPatternComplete(rc.getLocation(), rc.getType());
    }

    public boolean isInDanger() throws GameActionException {
        RobotInfo inc = getNearestRobot(b -> b.getType().isRobotType() && !isFriendly(b));
        return inc != null;
    }

    public boolean shouldSpawnNewMopper() throws GameActionException {
        return time - lastSpawnTime > 30 || (getNearestRobot(b -> bot.kids.contains(b.getID()) && b.getType().equals(UnitType.MOPPER)) == null);
    }

    public RespawnStrategy() {
        bot = (Tower) Game.bot;
        lastSpawnTime = -69696969;
    }

    public void spawnMopper(MapLocation loc) throws GameActionException {
        println("spawning defensive mopper");
        lastSpawnTime = time;
        bot.build(UnitType.MOPPER, loc);
        pm.send(loc, new StrategyPacket(2));
    }

    @Override
    public void runTick() throws GameActionException {
        // if people are rushing with both mopper and soldier then its actually wraps
        if (isInDanger()) {
            if (shouldSpawnNewMopper() && isTowerRespawnReady()) {
                MapInfo spawnLoc = getSafeSpawnLoc(UnitType.MOPPER);
                if (spawnLoc != null) {
                    spawnMopper(spawnLoc.getMapLocation());
                }
            }
        }
    }
}
