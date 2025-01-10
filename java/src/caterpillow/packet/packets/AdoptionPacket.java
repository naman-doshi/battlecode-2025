package caterpillow.packet.packets;

import caterpillow.packet.Packet;

public class AdoptionPacket extends Packet {
    public int child_id;

    public AdoptionPacket(int id) {
        child_id = id;
    }

    @Override
    public int enc() {
        return child_id;
    }
}
