package caterpillow.robot.agents.splasher;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.agents.Agent;

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
        int savedAllyTiles = 0;
        int savedPrimaryTiles = 0;
        MapLocation bestLoc = null;
        for (MapInfo info : attackable) {
            int enemyTiles = 0;
            int neutralTiles = 0;
            int allyTiles = 0;
            int primaryTiles = 0;
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
                    if (neigh.getPaint() == PaintType.ALLY_PRIMARY) primaryTiles++;
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
                bestNeutralTiles = neutralTiles;
                savedAllyTiles = allyTiles;
                savedPrimaryTiles = primaryTiles;
            } else if (!bad && enemyTiles == bestEnemyTiles && neutralTiles > bestNeutralTiles) {
                bestLoc = info.getMapLocation();
                bestNeutralTiles = neutralTiles;
                savedAllyTiles = allyTiles;
                savedPrimaryTiles = primaryTiles;
            }
        }

        if (bestLoc != null) {
            //System.out.println("best " + bestLoc);
            if (rc.canAttack(bestLoc)) {
                rc.attack(bestLoc, savedAllyTiles - savedPrimaryTiles > savedPrimaryTiles);
            }

            bot.pathfinder.makeMove(bestLoc);
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

    @Override
    public void runTick() throws GameActionException {
        super.runTick();

        // i saw an edge case where a tower just spawned moppers around it, but they weren't connected by paint so they just afked w/o a strat
        // LOL
        if (primaryStrategy instanceof EmptyStrategy && Game.time > 4) {
            if (home != null) {
                Game.origin = home;
            } else {
                Game.origin = rc.getLocation();
            }
            primaryStrategy = new SplasherAggroStrategy();
        }

    }
}
