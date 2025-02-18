package caterpillow.robot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import caterpillow.Game;
import caterpillow.packet.packets.AdoptionPacket;
import caterpillow.packet.packets.OriginPacket;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;

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