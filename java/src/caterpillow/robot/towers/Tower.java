package caterpillow.robot.towers;

import battlecode.common.*;
import caterpillow.packet.Packet;
import caterpillow.packet.packets.*;
import caterpillow.robot.Robot;
import caterpillow.robot.Strategy;
import caterpillow.util.Profiler;
import caterpillow.util.TowerTracker;
import static caterpillow.util.Util.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static caterpillow.Game.*;

public abstract class Tower extends Robot {
    // im indexing levels from 0
    public int level;
    public Strategy primaryStrategy;

    public ArrayList<Integer> kids;

    boolean[] edgeUsed = new boolean[(rc.getMapWidth() + rc.getMapHeight() - 2) * 2];
    MapLocation[] edges = new MapLocation[edgeUsed.length];

    public void registerKid(int id) {
        kids.add(id);
    }

    // USE THIS INSTEAD OF THE DEFAULT BUILD
    public int build(UnitType type, MapLocation loc, Packet... packets) throws GameActionException {
        rc.buildRobot(type, loc);
        RobotInfo newBot = rc.senseRobotAtLocation(loc);
        kids.add(newBot.getID());

        TowerTracker.sendInitPacket(newBot);

        for (Packet packet : packets) {
            pm.send(newBot.getID(), packet);
        }
        return newBot.getID();
    }

    public int build(UnitType type, MapLocation loc, List<Packet> packets) throws GameActionException {
        rc.buildRobot(type, loc);
        RobotInfo newBot = rc.senseRobotAtLocation(loc);
        kids.add(newBot.getID());

        TowerTracker.sendInitPacket(newBot);

        for (Packet packet : packets) {
            pm.send(newBot.getID(), packet);
        }
        return newBot.getID();
    }

    public void handleAdoptionPacket(AdoptionPacket packet, int senderID) {
        super.handleAdoptionPacket(packet, senderID);
        registerKid(senderID);
        System.out.println("Tower adopted " + senderID);
    }

    public boolean goForEnemy = false;
    public MapLocation scoutTarget() throws GameActionException {
        int usedCount = 0;
        for(int i = edgeUsed.length - 1; i >= 0; i--) {
            if(edgeUsed[i]) {
                usedCount++;
            }
        }
        if(usedCount == edgeUsed.length) {
            for(int i = edgeUsed.length - 1; i >= 0; i--) {
                edgeUsed[i] = false;
            }
            usedCount = 0;
        }
        MapLocation res;
        if(usedCount == 0) {
            int shift = -1;
            if(goForEnemy) {
                List<MapLocation> enemyLocs = guessSpawnLocs();
                int x = 0;
                int y = 0;
                for(MapLocation loc : enemyLocs) {
                    x += loc.x;
                    y += loc.y;
                }
                MapLocation average = new MapLocation(x / enemyLocs.size(), y / enemyLocs.size());
                System.out.println("average: " + average.toString());
                MapLocation vec = subtract(average, origin);
                MapLocation target = project(average, vec);
                System.out.println("origin: " + origin.toString() + " projection: "+ target.toString());
                for(int i = 0; i < edges.length; i++) {
                    if(edges[i].equals(target)) {
                        shift = i;
                        break;
                    }
                }
            } else {
                shift = trng.nextInt(edgeUsed.length);
            }
            MapLocation[] shifted = new MapLocation[edges.length];
            int k = 0;
            for(int j = shift + 1; j < edges.length; j++) {
                shifted[k++] = edges[j];
            }
            for(int j = 0; j <= shift; j++) {
                shifted[k++] = edges[j];
            }
            edges = shifted;
            edgeUsed[edgeUsed.length - 1] = true;
            res = edges[edges.length - 1];
        } else {
            int longestRun = 0;
            int best = 0;
            int lastUsed = -1;
            for(int i = 0; i < edgeUsed.length; i++) {
                if(edgeUsed[i]) {
                    if(i - lastUsed > longestRun) {
                        longestRun = i - lastUsed;
                        best = (lastUsed + i) / 2;
                    }
                    System.out.println(i + " " + lastUsed);
                    lastUsed = i;
                }
            }
            assert(!edgeUsed[best]);
            edgeUsed[best] = true;
            res = edges[best];
        }
        if(rc.getLocation().distanceSquaredTo(res) <= 25) return scoutTarget();
        rc.setIndicatorLine(rc.getLocation(), res, 255, 0, 255);
        return res;
    }

    @Override
    public void init() throws GameActionException {
        level = 0;
        kids = new ArrayList<>();
        int j = 0;
        for(int i = 0; i < rc.getMapWidth(); i++) {
            edges[j++] = new MapLocation(i, 0);
        }
        for(int i = 1; i < rc.getMapHeight(); i++) {
            edges[j++] = new MapLocation(rc.getMapWidth() - 1, i);
        }
        for(int i = rc.getMapWidth() - 2; i >= 0; i--) {
            edges[j++] = new MapLocation(i, rc.getMapHeight() - 1);
        }
        for(int i = rc.getMapHeight() - 2; i >= 1; i--) {
            edges[j++] = new MapLocation(0, i);
        }
    }

    @Override
    public void runTick() throws GameActionException {
        rc.attack(null);
        primaryStrategy.runTick();
    }

    public void upgrade() {
        level += 1;
    }
}
