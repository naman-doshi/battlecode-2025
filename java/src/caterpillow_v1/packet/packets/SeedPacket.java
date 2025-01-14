package caterpillow_v1.packet.packets;

import caterpillow_v1.packet.Packet;

import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;

public class SeedPacket extends Packet {
    public int seed;
    public SeedPacket(int seed) {
        this.seed = seed % pm.MAX_PAYLOAD;
        if (this.seed < 0) this.seed += pm.MAX_PAYLOAD;
    }
}
