package caterpillow.robot.towers;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import static caterpillow.Game.pm;
import static caterpillow.Game.rc;
import static caterpillow.Game.time;
import caterpillow.packet.packets.StrategyPacket;
import static caterpillow.util.Util.add;
import static caterpillow.tracking.RobotTracker.getNearestRobot;
import static caterpillow.util.Util.getSafeSpawnLoc;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.isPatternComplete;
import static caterpillow.util.Util.println;

/*

currently, its kinda beatable since itll only spawn a mopper
todo: spawn moppers that clean bad cells (if applicable), and spawn soldiers that can refill cells

*/

// this is more of an offence strategy than respawn, it chases the enemy

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
        if (rc.getChips() < 1300) {
            return;
        }
        println("spawning defensive mopper");
        lastSpawnTime = time;
        bot.build(UnitType.MOPPER, loc);
        pm.send(loc, new StrategyPacket(1));
    }

    @Override
    public void runTick() throws GameActionException {
        // if people are rushing with both mopper and soldier then its actually wraps
        if (isInDanger()) {
            indicate("DANGER");
            if (shouldSpawnNewMopper()) {
                MapInfo spawnLoc = getSafeSpawnLoc(UnitType.MOPPER);
                if (spawnLoc != null) {
                    spawnMopper(spawnLoc.getMapLocation());
                }
            }
        }
    }
}
