package caterpillow_v1.packet.packets;

import battlecode.common.MapLocation;
import caterpillow_v1.packet.Packet;

import static caterpillow_v1.Game.rc;

public class InitPacket extends Packet {
    public MapLocation loc;
    public int coinTowers;
    public InitPacket(MapLocation loc, int coinTowers) {
        this.loc = loc;
        this.coinTowers = coinTowers;
        assert 0 <= loc.x && loc.x < rc.getMapWidth();
        assert 0 <= loc.y && loc.y < rc.getMapHeight();
    }
}
