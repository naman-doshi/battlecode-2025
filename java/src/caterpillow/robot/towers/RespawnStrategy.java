package caterpillow.robot.towers;

import battlecode.common.*;
import caterpillow.Game;
import caterpillow.packet.packets.StrategyPacket;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

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
        if (isEnemyPaintBlocking()) {
            return false;
        }
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                MapLocation loc = add(rc.getLocation(), new MapLocation(dx, dy));
                if (!rc.senseMapInfo(loc).getPaint().isAlly() || rc.senseMapInfo(loc).getPaint().isSecondary() == getCellColour(rc.getLocation(), loc, rc.getType())) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isInDanger() throws GameActionException {
        RobotInfo inc = getNearestRobot(b -> b.getType().isRobotType() && !isFriendly(b));
        return inc != null;
    }

    public boolean shouldSpawnNewMopper() throws GameActionException {
        return time - lastSpawnTime > 20 || (getNearestRobot(b -> bot.kids.contains(b.getID()) && b.getType().equals(UnitType.MOPPER)) == null);
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
        if (isInDanger() && isTowerRespawnReady()) {
            if (shouldSpawnNewMopper()) {
                MapInfo spawnLoc = getSafeSpawnLoc(UnitType.MOPPER);
                if (spawnLoc != null) {
                    spawnMopper(spawnLoc.getMapLocation());
                }
            }
        }
    }
}
