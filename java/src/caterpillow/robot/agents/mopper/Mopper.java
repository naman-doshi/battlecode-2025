package caterpillow.robot.agents.mopper;

import java.util.List;

import battlecode.common.GameActionException;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.agents.Agent;
import battlecode.common.MapLocation;
import caterpillow.Game;
import caterpillow.Game.*;

public class Mopper extends Agent {

    List<MapLocation> enemyLocs = new java.util.LinkedList<MapLocation>();
    MapLocation spawnLoc = null;

    public MapLocation reflectHor(MapLocation loc) {
        return new MapLocation(Game.rc.getMapWidth() - 1 - loc.x, loc.y);
    }

    public MapLocation reflectVert(MapLocation loc) {
        return new MapLocation(loc.x, Game.rc.getMapHeight() - 1 - loc.y);
    }

    public MapLocation reflectRot(MapLocation loc) {
        return new MapLocation(Game.rc.getMapWidth() - 1 - loc.x, Game.rc.getMapHeight() - 1 - loc.y);
    }

    public void populateEnemyLocs() throws GameActionException {
        int dist_hormiddle = Math.abs(spawnLoc.x - Game.rc.getMapWidth() / 2);
        int dist_vertmiddle = Math.abs(spawnLoc.y - Game.rc.getMapHeight() / 2);
        if (dist_hormiddle > dist_vertmiddle) {
            enemyLocs.addLast(reflectHor(spawnLoc));
            // second is the rotation one.
            enemyLocs.addLast(reflectRot(spawnLoc));
            // third is the vert ref one
            enemyLocs.addLast(reflectVert(spawnLoc));
        } else if (dist_hormiddle < dist_vertmiddle) {
            // first is vert ref
            enemyLocs.addLast(reflectVert(spawnLoc));
            // second is the rotation one.
            enemyLocs.addLast(reflectRot(spawnLoc));
            // third is the hor ref one
            enemyLocs.addLast(reflectHor(spawnLoc));
        } else {
            // first is hor ref
            enemyLocs.addLast(reflectHor(spawnLoc));
            // second is vert ref
            enemyLocs.addLast(reflectVert(spawnLoc));
            // third is the rotation one
            enemyLocs.addLast(reflectRot(spawnLoc));
        }
    }
    
    @Override
    public void init() throws GameActionException {
        super.init();

        pathfinder = new BugnavPathfinder();
        primaryStrategy = new EmptyStrategy();
        Mopper mop = (Mopper) Game.bot;
        spawnLoc = mop.home;

    }

    @Override
    public void handleStrategyPacket(StrategyPacket packet, int senderID) {
        super.handleStrategyPacket(packet, senderID);
        switch (packet.strategyID) {
            case 0:
                primaryStrategy = new MopperDefenceStrategy();
                break;
            case 1:
                primaryStrategy = new MopperOffenceStrategy();
                System.out.println("set strategy to offence");
                break;
        }
    }
}
