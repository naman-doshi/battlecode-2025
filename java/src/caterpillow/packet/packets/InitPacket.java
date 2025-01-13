package caterpillow.packet.packets;

import battlecode.common.MapLocation;
import caterpillow.packet.Packet;

import static caterpillow.Game.rc;
import static caterpillow.util.TowerTracker.MAX_TOWER_BITS;
import static caterpillow.util.Util.println;

public class InitPacket extends Packet {
    public MapLocation loc;
    public int totTowers, coinTowers;
    public InitPacket(MapLocation loc, int totTowers, int coinTowers) {
        this.loc = loc;
        this.totTowers = totTowers;
        this.coinTowers = coinTowers;
        assert 0 <= loc.x && loc.x < rc.getMapWidth();
        assert 0 <= loc.y && loc.y < rc.getMapHeight();
        assert 0 <= totTowers && totTowers < (1 << MAX_TOWER_BITS);
        assert 0 <= coinTowers && coinTowers <= totTowers;
    }
}
