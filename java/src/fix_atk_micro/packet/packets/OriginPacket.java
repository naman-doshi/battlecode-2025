package fix_atk_micro.packet.packets;

import battlecode.common.MapLocation;
import fix_atk_micro.packet.Packet;

import static fix_atk_micro.Game.*;

public class OriginPacket extends Packet {
    public MapLocation loc;
    public OriginPacket(MapLocation loc) {
        this.loc = loc;
        assert 0 <= loc.x && loc.x < mapWidth;
        assert 0 <= loc.y && loc.y < mapHeight;
    }
}
