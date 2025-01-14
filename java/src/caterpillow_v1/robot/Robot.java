package caterpillow_v1.robot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.UnitType;
import caterpillow_v1.Game;
import caterpillow_v1.packet.packets.AdoptionPacket;
import caterpillow_v1.packet.packets.OriginPacket;
import caterpillow_v1.packet.packets.SeedPacket;
import caterpillow_v1.packet.packets.StrategyPacket;

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

    public abstract void init() throws GameActionException;

    public abstract void runTick() throws GameActionException;

    public void handleAdoptionPacket(AdoptionPacket packet, int senderID) {}
    public void handleOriginPacket(OriginPacket packet, int senderID) {
        Game.origin = packet.loc;
    }
    public void handleSeedPacket(SeedPacket packet, int senderID) {
        Game.seed = packet.seed;
    }
    public void handleStrategyPacket(StrategyPacket packet, int senderID) throws GameActionException {}
}