package caterpillow_v1.robot.agents.splasher;

import java.util.List;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import caterpillow_v1.Game;
import static caterpillow_v1.Game.rc;
import caterpillow_v1.packet.packets.StrategyPacket;
import caterpillow_v1.pathfinding.BugnavPathfinder;
import caterpillow_v1.robot.agents.Agent;

public class Splasher extends Agent {

    Splasher bot;
    List<MapLocation> enemyLocs;
    MapLocation spawnLoc;

   

    @Override
    public void init() throws GameActionException {
        super.init();

        pathfinder = new BugnavPathfinder();
        primaryStrategy = new SplasherAggroStrategy();
        bot = (Splasher) Game.bot;
    }

    public MapLocation bestAttackLocation() throws GameActionException {
        // if we can bomb an enemy tower, do it
        MapInfo[] attackable = rc.senseNearbyMapInfos(4);
        for (MapInfo info : attackable) {
            if (info.hasRuin() && rc.senseRobotAtLocation(info.getMapLocation()) != null && rc.senseRobotAtLocation(info.getMapLocation()).getTeam() != rc.getTeam()) {
                return info.getMapLocation();
            }
        }

        // check if u can bomb only enemy/neutral tiles
        int bestEnemyTiles = 0;
        MapLocation bestLoc = null;
        for (MapInfo info : attackable) {
            int cur = 0;
            boolean bad = false;
            for (MapInfo neigh : rc.senseNearbyMapInfos(info.getMapLocation(), 2)) {
                if (neigh.getPaint().isEnemy()) {
                    cur++;
                }
                if (neigh.getPaint().isAlly()) {
                    bad = true;
                    break;
                }
            }
            if (!bad && cur >= bestEnemyTiles) {
                bestEnemyTiles = cur;
                bestLoc = info.getMapLocation();
            }
        }
        if (bestLoc != null) {
            return bestLoc;
        }

        return null;
    }

    public static final int AGGRO_STRAT = 0;

    @Override
    public void handleStrategyPacket(StrategyPacket packet, int senderID) throws GameActionException {
        super.handleStrategyPacket(packet, senderID);
        switch (packet.strategyID) {
            case AGGRO_STRAT:
                primaryStrategy = new SplasherAggroStrategy();
                break;
        }
    }
}
