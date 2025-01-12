package caterpillow.robot.towers;

import battlecode.common.*;
import caterpillow.packet.Packet;
import caterpillow.packet.packets.AdoptionPacket;
import caterpillow.packet.packets.OriginPacket;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.Robot;
import caterpillow.robot.Strategy;

import java.util.ArrayList;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

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
        println("added " + newBot.getID() + " to kids");
        pm.send(newBot.getID(), new OriginPacket(origin));
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
