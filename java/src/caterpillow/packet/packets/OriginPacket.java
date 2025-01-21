package caterpillow.packet.packets;

import battlecode.common.MapLocation;
import caterpillow.packet.Packet;

import static caterpillow.Game.*;

public class OriginPacket extends Packet {
    public MapLocation loc;
    public OriginPacket(MapLocation loc) {
        this.loc = loc;
        assert 0 <= loc.x && loc.x < mapWidth;
        assert 0 <= loc.y && loc.y < mapHeight;
    }
}
