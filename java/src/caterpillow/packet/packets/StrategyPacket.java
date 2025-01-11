package caterpillow.packet.packets;

import caterpillow.packet.Packet;

public class StrategyPacket extends Packet {
    public int strategyID;
    public StrategyPacket(int id) {
        strategyID = id;
    }
}
