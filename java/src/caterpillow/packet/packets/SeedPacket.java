package caterpillow.packet.packets;

import caterpillow.packet.Packet;

import static caterpillow.Game.*;

public class SeedPacket extends Packet {
    public int seed;
    public SeedPacket(int seed) {
        this.seed = seed % pm.MAX_PAYLOAD;
        if (this.seed < 0) this.seed += pm.MAX_PAYLOAD;
    }
}
