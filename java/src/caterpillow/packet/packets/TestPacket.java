package caterpillow.packet.packets;

import caterpillow.packet.Packet;

public class TestPacket extends Packet {

    public int value;

    public TestPacket(int value) {
        this.value = value;
    }

    @Override
    public int enc() {
        return value;
    }
}
