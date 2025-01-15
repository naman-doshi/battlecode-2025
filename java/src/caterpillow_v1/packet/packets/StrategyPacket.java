package caterpillow_v1.packet.packets;

import caterpillow_v1.packet.Packet;
import caterpillow_v1.packet.PacketManager;

public class StrategyPacket extends Packet {
    public static final int STRATEGY_ID_SIZE = 3;
    public static final int STRATEGY_DATA_SIZE = PacketManager.PAYLOAD_SIZE - STRATEGY_ID_SIZE;
    public int strategyID;
    public int strategyData;
    public StrategyPacket(int id, int data) {
        strategyID = id;
        strategyData = data;
        assert strategyID >= 0 && strategyID < (1 << STRATEGY_ID_SIZE);
        assert strategyData >= 0 && strategyData < (1 << STRATEGY_DATA_SIZE);
    }
    public StrategyPacket(int id) {
        strategyID = id;
        strategyData = 0;
        assert strategyID >= 0 && strategyID < (1 << STRATEGY_ID_SIZE);
    }
}
