package fix_atk_micro.robot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import fix_atk_micro.Game;
import fix_atk_micro.packet.packets.AdoptionPacket;
import fix_atk_micro.packet.packets.OriginPacket;
import fix_atk_micro.packet.packets.SeedPacket;
import fix_atk_micro.packet.packets.StrategyPacket;

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