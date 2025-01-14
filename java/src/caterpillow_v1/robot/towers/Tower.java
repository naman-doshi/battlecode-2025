package caterpillow_v1.robot.towers;

import battlecode.common.*;
import caterpillow_v1.packet.Packet;
import caterpillow_v1.packet.packets.*;
import caterpillow_v1.robot.Robot;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.util.TowerTracker;

import java.util.ArrayList;
import java.util.List;

import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;

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

        if (TowerTracker.broken) {
            pm.send(newBot.getID(), new InitPacket(origin, 0));
        } else {
            pm.send(newBot.getID(), new InitPacket(origin, TowerTracker.coinTowers));
        }

        for (Packet packet : packets) {
            pm.send(newBot.getID(), packet);
        }
        return newBot.getID();
    }

    public int build(UnitType type, MapLocation loc, List<Packet> packets) throws GameActionException {
        rc.buildRobot(type, loc);
        RobotInfo newBot = rc.senseRobotAtLocation(loc);
        kids.add(newBot.getID());

        if (TowerTracker.broken) {
            pm.send(newBot.getID(), new InitPacket(origin, 0));
        } else {
            pm.send(newBot.getID(), new InitPacket(origin, TowerTracker.coinTowers));
        }

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

    @Override
    public void init() throws GameActionException {
        level = 0;
        kids = new ArrayList<>();
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
