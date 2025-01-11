package caterpillow.robot.towers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.packet.packets.AdoptionPacket;
import caterpillow.packet.packets.OriginPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.Robot;
import caterpillow.robot.Strategy;

import java.util.ArrayList;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public abstract class Tower extends Robot {
    // im indexing levels from 0
    public int level;
    protected UnitType[] types;
    Strategy primaryStrategy;
    Strategy secondaryStrategy;

    public ArrayList<Integer> kids;

    public void registerKid(int id) {
        kids.add(id);
    }

    // USE THIS INSTEAD OF THE DEFAULT BUILD
    public void build(UnitType type, MapLocation loc) throws GameActionException {
        rc.buildRobot(type, loc);
        RobotInfo newBot = rc.senseRobotAtLocation(loc);
        kids.add(newBot.getID());
        pm.send(newBot.getID(), new OriginPacket(origin));
    }

    public void handleAdoptionPacket(AdoptionPacket packet, int senderID) {
        super.handleAdoptionPacket(packet, senderID);
        registerKid(senderID);
        System.out.println("Tower adopted " + senderID);
    }

    @Override
    public UnitType getType() {
        return types[level];
    }

    @Override
    public void init() {
        level = 0;
        kids = new ArrayList<>();
    }

    @Override
    public void runTick() throws GameActionException {
        if (secondaryStrategy != null) {
            if (secondaryStrategy.isComplete()) {
                secondaryStrategy = null;
            }
        }
        if (secondaryStrategy != null) {
            secondaryStrategy.runTick();
        } else {
            if (primaryStrategy.isComplete()) {
                dead("primary strategy completed");
                return;
            }
            primaryStrategy.runTick();
        }
    }

    public void upgrade() {
        level += 1;
    }
}
