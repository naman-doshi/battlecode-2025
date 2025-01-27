package fix_atk_micro.packet.packets;

import fix_atk_micro.packet.Packet;

import static fix_atk_micro.Game.*;

public class SeedPacket extends Packet {
    public int seed;
    public SeedPacket(int seed) {
        this.seed = seed % pm.MAX_PAYLOAD;
        if (this.seed < 0) this.seed += pm.MAX_PAYLOAD;
    }
}
