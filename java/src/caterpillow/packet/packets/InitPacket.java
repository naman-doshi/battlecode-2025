package caterpillow.packet.packets;

import battlecode.common.MapLocation;
import caterpillow.packet.Packet;
import caterpillow.tracking.TowerTracker;

import static caterpillow.Game.*;

public class InitPacket extends Packet {
    public MapLocation loc;
    public int coinTowers;
    public int srps;
    public InitPacket(MapLocation loc, int srps, int coinTowers) {
        this.loc = loc;
        this.srps = srps;
        this.coinTowers = coinTowers;
        assert 0 <= loc.x && loc.x < mapWidth;
        assert 0 <= loc.y && loc.y < mapHeight;
        assert 0 <= coinTowers && coinTowers < (1 << TowerTracker.MAX_TOWER_BITS);
        assert 0 <= srps && srps < (1 << TowerTracker.MAX_SRP_BITS);
    }
}
