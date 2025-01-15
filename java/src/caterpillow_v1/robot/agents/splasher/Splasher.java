package caterpillow_v1.robot.agents.splasher;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import caterpillow_v1.Game;
import static caterpillow_v1.Game.rc;
import caterpillow_v1.packet.packets.StrategyPacket;
import caterpillow_v1.pathfinding.BugnavPathfinder;
import caterpillow_v1.robot.EmptyStrategy;
import caterpillow_v1.robot.agents.Agent;

public class Splasher extends Agent {

    Splasher bot;
    MapLocation spawnLoc;

    @Override
    public void init() throws GameActionException {
        super.init();

        pathfinder = new BugnavPathfinder(c -> c.getPaint().isEnemy());
        primaryStrategy = new EmptyStrategy();
        bot = (Splasher) Game.bot;
    }

    public boolean isEdge(MapLocation loc) throws GameActionException {
        return !(loc.x != 0 && loc.x != rc.getMapWidth() - 1 && loc.y != 0 && loc.y != rc.getMapHeight() - 1);
    }

    public MapLocation bestAttackLocation() throws GameActionException {
        
        

        MapInfo[] attackable = rc.senseNearbyMapInfos(4);

        //if we can bomb an enemy tower, do it
        for (MapInfo info : attackable) {
            if (info.hasRuin() && rc.senseRobotAtLocation(info.getMapLocation()) != null && rc.senseRobotAtLocation(info.getMapLocation()).getTeam() != rc.getTeam()) {
                return info.getMapLocation();
            }
        }

        // check if u can bomb only enemy/neutral tiles
        int bestEnemyTiles = 0;
        int bestNeutralTiles = 0;
        MapLocation bestLoc = null;
        for (MapInfo info : attackable) {
            int enemyTiles = 0;
            int neutralTiles = 0;
            int allyTiles = 0;
            boolean bad = false;
            
            if (isEdge(info.getMapLocation())) {
                //System.out.println("bad " + info.getMapLocation());
                continue;
            }

            for (MapInfo neigh : rc.senseNearbyMapInfos(info.getMapLocation(), 2)) {
                
                if (neigh.isWall() || neigh.hasRuin()) continue;
                
                if (neigh.getPaint().isEnemy()) {
                    enemyTiles++;
                }
                if (neigh.getPaint() == PaintType.EMPTY) {
                    neutralTiles++;
                }
                if (neigh.getPaint().isAlly()) {
                    allyTiles++;
                }


                // adjustable
                if (allyTiles > 2) {
                    bad = true;
                    break;
                }

                //System.out.println("info " + info.getMapLocation() + " " + enemyTiles + " " + neutralTiles + " " + allyTiles);
            }
            
            if (!bad && enemyTiles > bestEnemyTiles) {
                bestEnemyTiles = enemyTiles;
                bestLoc = info.getMapLocation();
            } else if (!bad && enemyTiles == bestEnemyTiles && neutralTiles > bestNeutralTiles) {
                bestLoc = info.getMapLocation();
                bestNeutralTiles = neutralTiles;  
            }
        }

        if (bestLoc != null) {
            //System.out.println("best " + bestLoc);
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
