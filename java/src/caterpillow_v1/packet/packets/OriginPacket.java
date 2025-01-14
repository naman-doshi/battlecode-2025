package caterpillow_v1.packet.packets;

import battlecode.common.MapLocation;
import caterpillow_v1.packet.Packet;

import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;

public class OriginPacket extends Packet {
    public MapLocation loc;
    public OriginPacket(MapLocation loc) {
        this.loc = loc;
        assert 0 <= loc.x && loc.x < rc.getMapWidth();
        assert 0 <= loc.y && loc.y < rc.getMapHeight();
    }
}
