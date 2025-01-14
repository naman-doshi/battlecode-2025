package caterpillow_v1.packet.packets;

import battlecode.common.MapLocation;
import caterpillow_v1.packet.Packet;

import static caterpillow_v1.Game.rc;
import static caterpillow_v1.util.TowerTracker.MAX_TOWER_BITS;
import static caterpillow_v1.util.Util.println;

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
