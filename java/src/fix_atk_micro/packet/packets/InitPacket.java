package fix_atk_micro.packet.packets;

import battlecode.common.MapLocation;
import fix_atk_micro.packet.Packet;
import fix_atk_micro.tracking.TowerTracker;

import static fix_atk_micro.Game.*;

public class InitPacket extends Packet {
    public MapLocation loc;
    public int coinTowers;
    public int srps;
    public int hasStarterCoinDied;
    public InitPacket(MapLocation loc, int srps, int coinTowers, int hasStarterCoinDied) {
        this.loc = loc;
        this.srps = srps;
        this.coinTowers = coinTowers;
        this.hasStarterCoinDied = hasStarterCoinDied;
        assert 0 <= loc.x && loc.x < mapWidth;
        assert 0 <= loc.y && loc.y < mapHeight;
        assert 0 <= coinTowers && coinTowers < (1 << TowerTracker.MAX_TOWER_BITS);
        assert 0 <= srps && srps < (1 << TowerTracker.MAX_SRP_BITS);
    }
}
