package caterpillow.robot.towers;

import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.packet.packets.AdoptionPacket;
import caterpillow.packet.packets.TestPacket;
import caterpillow.robot.Robot;

import java.util.ArrayList;

import static caterpillow.Util.*;
import static caterpillow.Game.*;

public abstract class Tower extends Robot {
    // im indexing levels from 0
    public int level;
    protected UnitType[] types;

    public ArrayList<Integer> kids;

    public void registerKid(int id) {
        kids.add(id);
    }

    @Override
    public void handleAdoptionPacket(AdoptionPacket packet, int senderID) {
        super.handleAdoptionPacket(packet, senderID);
        registerKid(packet.child_id);
        System.out.println("added " + packet.child_id + " to kids");
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

    public void upgrade() {
        level += 1;
    }
}
