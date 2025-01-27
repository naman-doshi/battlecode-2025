package fix_atk_micro.robot.towers;

import battlecode.common.*;
import fix_atk_micro.packet.Packet;
import fix_atk_micro.packet.packets.*;
import fix_atk_micro.robot.Robot;
import fix_atk_micro.robot.Strategy;
import fix_atk_micro.robot.towers.paint.StarterPaintTowerStrategy;
import fix_atk_micro.robot.towers.money.StarterMoneyTowerStrategy;
import fix_atk_micro.util.Profiler;
import fix_atk_micro.tracking.TowerTracker;
import static fix_atk_micro.util.Util.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static fix_atk_micro.Config.genAggroTarget;
import static fix_atk_micro.Game.*;

public abstract class Tower extends Robot {
    // im indexing levels from 0
    public int level;
    public Strategy primaryStrategy;

    public ArrayList<Integer> kids;

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
        indicate(newBot.getID() + "");
        return newBot.getID();
    }

    public int build(UnitType type, MapLocation loc, List<Packet> packets) throws GameActionException {
        assert rc.senseRobotAtLocation(loc) == null;
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

    ArrayList<MapLocation> initialTargets = new ArrayList<>();
    public MapLocation scoutTarget() throws GameActionException {
        MapLocation res;
        if(initialTargets.size() > 0) {
            res = initialTargets.get(0);
            initialTargets.remove(0);
        } else res = genAggroTarget(trng);
        rc.setIndicatorLine(rc.getLocation(), res, 255, 0, 255);
        return res;
    }

    @Override
    public void init() throws GameActionException {
        level = 0;
        kids = new ArrayList<>();
        MapLocation[] edges = {
            new MapLocation(2, 2),
            new MapLocation(mapWidth / 2, 2),
            new MapLocation(mapWidth - 3, 2),
            new MapLocation(mapWidth - 3, mapHeight / 2),
            new MapLocation(mapWidth - 3, mapHeight - 3),
            new MapLocation(mapWidth / 2, mapHeight - 3),
            new MapLocation(2, mapHeight - 3),
            new MapLocation(2, mapHeight / 2),
        };
        int closest = 0;
        double closestDistSquared = rc.getLocation().distanceSquaredTo(edges[0]) / 1.4;
        for(int i = 7; i > 0; i--) {
            double distSquared = rc.getLocation().distanceSquaredTo(edges[i]);
            if(i % 2 == 0) distSquared /= 1.4;
            if(distSquared < closestDistSquared) {
                closest = i;
                closestDistSquared = rc.getLocation().distanceSquaredTo(edges[i]);
            }
        }
        boolean atCentre = rc.getLocation().distanceSquaredTo(centre) < closestDistSquared;
        initialTargets.add(centre);
        if(closest % 2 == 1) {
            initialTargets.add(edges[(closest + 1) % 8]);
            initialTargets.add(edges[(closest + 7) % 8]);
        } else {
            initialTargets.add(edges[(closest + 2) % 8]);
            initialTargets.add(edges[(closest + 6) % 8]);
        }
        if(rc.getLocation().distanceSquaredTo(initialTargets.get(1)) > rc.getLocation().distanceSquaredTo(initialTargets.get(2))) {
            MapLocation temp = initialTargets.get(1);
            initialTargets.set(1, initialTargets.get(2));
            initialTargets.set(2, temp);
        }
        if(atCentre) initialTargets.add(edges[closest]);
        if(!(primaryStrategy instanceof StarterPaintTowerStrategy)) initialTargets.remove(0);
        rc.setIndicatorLine(rc.getLocation(), edges[closest], 0, 255, 255);
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
