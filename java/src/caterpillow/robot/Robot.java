package caterpillow.robot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.UnitType;
import caterpillow.packet.packets.AdoptionPacket;
import caterpillow.packet.packets.TestPacket;

public abstract class Robot {
    protected static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    public abstract UnitType getType();

    public abstract void init() throws GameActionException;

    public abstract void runTick() throws GameActionException;

    public void handleTestPacket(TestPacket packet, int senderID) {}
    public void handleAdoptionPacket(AdoptionPacket packet, int senderID) {}

}