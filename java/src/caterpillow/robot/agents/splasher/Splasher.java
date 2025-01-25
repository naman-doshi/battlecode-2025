package caterpillow.robot.agents.splasher;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.mapHeight;
import static caterpillow.Game.mapWidth;
import static caterpillow.Game.rc;
import static caterpillow.Game.team;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.agents.Agent;
import caterpillow.tracking.CellTracker;
import caterpillow.util.Pair;
import static caterpillow.util.Util.isInRobotAttackDanger;

public class Splasher extends Agent {

    Splasher bot;
    MapLocation spawnLoc;

    @Override
    public void init() throws GameActionException {
        super.init();

        pathfinder = new BugnavPathfinder(c -> c.getPaint().isEnemy() || isInRobotAttackDanger(c.getMapLocation()));
        primaryStrategy = new EmptyStrategy();
        bot = (Splasher) Game.bot;
    }

    public boolean isEdge(MapLocation loc) throws GameActionException {
        return !(loc.x != 0 && loc.x != mapWidth - 1 && loc.y != 0 && loc.y != mapHeight - 1);
    }

    public Pair<MapLocation, Boolean> bestAttackLocation() throws GameActionException {
        MapLocation[] attackable = rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), 4);

        int bestScore = 11; // minimum score (exclusive)
        MapLocation bestLoc = null;
        boolean bestPaint = false; // is secondary?
        for (MapLocation loc : attackable) {
            int score = 0;
            int primaryTiles = 0;
            int allyTiles = 0;

            if (isEdge(loc)) continue;

            int x = loc.x;
            int y = loc.y;
            MapInfo neigh;
            do {
                neigh = CellTracker.mapInfos[x][y];
                if(neigh.isWall()) continue;
                if(neigh.hasRuin()) {
                    RobotInfo robot = rc.senseRobotAtLocation(neigh.getMapLocation());
                    if(robot != null && robot.team != team) score += 1000;
                    continue;
                }
                switch(neigh.getPaint().ordinal()) {
                    case 0:
                        score++;
                        break;
                    case 1:
                        primaryTiles++;
                    case 2:
                        allyTiles++;
                        break;
                    case 3:
                    case 4:
                        score += 3;
                        break;
                }
                if(allyTiles == 3) break;
                x--;
                neigh = CellTracker.mapInfos[x][y];
                //if(neigh.isWall()) continue;
                if(neigh.hasRuin()) {
                    RobotInfo robot = rc.senseRobotAtLocation(neigh.getMapLocation());
                    if(robot != null && robot.team != team) score += 1000;
                    continue;
                }
                switch(neigh.getPaint().ordinal()) {
                    case 0:
                        score++;
                        break;
                    case 1:
                        primaryTiles++;
                    case 2:
                        allyTiles++;
                        break;
                    case 3:
                    case 4:
                        score += 3;
                        break;
                }
                if(allyTiles == 3) break;
                y--;
                neigh = CellTracker.mapInfos[x][y];
                //if(neigh.isWall()) continue;
                if(neigh.hasRuin()) {
                    RobotInfo robot = rc.senseRobotAtLocation(neigh.getMapLocation());
                    if(robot != null && robot.team != team) score += 1000;
                    continue;
                }
                switch(neigh.getPaint().ordinal()) {
                    case 0:
                        score++;
                        break;
                    case 1:
                        primaryTiles++;
                    case 2:
                        allyTiles++;
                        break;
                    case 3:
                    case 4:
                        score += 3;
                        break;
                }
                if(allyTiles == 3) break;
                x++;
                neigh = CellTracker.mapInfos[x][y];
                //if(neigh.isWall()) continue;
                if(neigh.hasRuin()) {
                    RobotInfo robot = rc.senseRobotAtLocation(neigh.getMapLocation());
                    if(robot != null && robot.team != team) score += 1000;
                    continue;
                }
                switch(neigh.getPaint().ordinal()) {
                    case 0:
                        score++;
                        break;
                    case 1:
                        primaryTiles++;
                    case 2:
                        allyTiles++;
                        break;
                    case 3:
                    case 4:
                        score += 3;
                        break;
                }
                if(allyTiles == 3) break;
                x++;
                neigh = CellTracker.mapInfos[x][y];
                //if(neigh.isWall()) continue;
                if(neigh.hasRuin()) {
                    RobotInfo robot = rc.senseRobotAtLocation(neigh.getMapLocation());
                    if(robot != null && robot.team != team) score += 1000;
                    continue;
                }
                switch(neigh.getPaint().ordinal()) {
                    case 0:
                        score++;
                        break;
                    case 1:
                        primaryTiles++;
                    case 2:
                        allyTiles++;
                        break;
                    case 3:
                    case 4:
                        score += 3;
                        break;
                }
                if(allyTiles == 3) break;
                y++;
                neigh = CellTracker.mapInfos[x][y];
                //if(neigh.isWall()) continue;
                if(neigh.hasRuin()) {
                    RobotInfo robot = rc.senseRobotAtLocation(neigh.getMapLocation());
                    if(robot != null && robot.team != team) score += 1000;
                    continue;
                }
                switch(neigh.getPaint().ordinal()) {
                    case 0:
                        score++;
                        break;
                    case 1:
                        primaryTiles++;
                    case 2:
                        allyTiles++;
                        break;
                    case 3:
                    case 4:
                        score += 3;
                        break;
                }
                if(allyTiles == 3) break;
                y++;
                neigh = CellTracker.mapInfos[x][y];
                //if(neigh.isWall()) continue;
                if(neigh.hasRuin()) {
                    RobotInfo robot = rc.senseRobotAtLocation(neigh.getMapLocation());
                    if(robot != null && robot.team != team) score += 1000;
                    continue;
                }
                switch(neigh.getPaint().ordinal()) {
                    case 0:
                        score++;
                        break;
                    case 1:
                        primaryTiles++;
                    case 2:
                        allyTiles++;
                        break;
                    case 3:
                    case 4:
                        score += 3;
                        break;
                }
                if(allyTiles == 3) break;
                x--;
                neigh = CellTracker.mapInfos[x][y];
                //if(neigh.isWall()) continue;
                if(neigh.hasRuin()) {
                    RobotInfo robot = rc.senseRobotAtLocation(neigh.getMapLocation());
                    if(robot != null && robot.team != team) score += 1000;
                    continue;
                }
                switch(neigh.getPaint().ordinal()) {
                    case 0:
                        score++;
                        break;
                    case 1:
                        primaryTiles++;
                    case 2:
                        allyTiles++;
                        break;
                    case 3:
                    case 4:
                        score += 3;
                        break;
                }
                if(allyTiles == 3) break;
                x--;
                neigh = CellTracker.mapInfos[x][y];
                //if(neigh.isWall()) continue;
                if(neigh.hasRuin()) {
                    RobotInfo robot = rc.senseRobotAtLocation(neigh.getMapLocation());
                    if(robot != null && robot.team != team) score += 1000;
                    continue;
                }
                switch(neigh.getPaint().ordinal()) {
                    case 0:
                        score++;
                        break;
                    case 1:
                        primaryTiles++;
                    case 2:
                        allyTiles++;
                        break;
                    case 3:
                    case 4:
                        score += 3;
                        break;
                }
                if(allyTiles == 3) break;
            } while(false);
            x = loc.x;
            y = loc.y;

            if(x > 1) {
                MapInfo info = CellTracker.mapInfos[x - 2][y];
                if(info.hasRuin()) {
                    RobotInfo robot = rc.senseRobotAtLocation(new MapLocation(x - 2, y));
                    if(robot != null && robot.team != team) score += 1000;
                } else if(info.getPaint() == PaintType.EMPTY) score++;
            }
            if(y > 1) {
                MapInfo info = CellTracker.mapInfos[x][y - 2];
                if(info.hasRuin()) {
                    RobotInfo robot = rc.senseRobotAtLocation(new MapLocation(x, y - 2));
                    if(robot != null && robot.team != team) score += 1000;
                } else if(info.getPaint() == PaintType.EMPTY) score++;
            }
            if(x < mapWidth - 2) {
                MapInfo info = CellTracker.mapInfos[x + 2][y];
                if(info.hasRuin()) {
                    RobotInfo robot = rc.senseRobotAtLocation(new MapLocation(x + 2, y));
                    if(robot != null && robot.team != team) score += 1000;
                } else if(info.getPaint() == PaintType.EMPTY) score++;
            }
            if(y < mapHeight - 2) {
                MapInfo info = CellTracker.mapInfos[x][y + 2];
                if(info.hasRuin()) {
                    RobotInfo robot = rc.senseRobotAtLocation(new MapLocation(x, y + 2));
                    if(robot != null && robot.team != team) score += 1000;
                } else if(info.getPaint() == PaintType.EMPTY) score++;
            }

            if(score > bestScore) {
                bestScore = score;
                bestLoc = loc;
                bestPaint = primaryTiles * 2 <= allyTiles;
            }
        }

        return new Pair<>(bestLoc, bestPaint);
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
