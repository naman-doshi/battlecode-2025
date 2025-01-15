package caterpillow_v1.robot.troll;

import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.UnitType;
import caterpillow_v1.packet.Packet;
import caterpillow_v1.util.GameSupplier;

import static caterpillow_v1.util.Util.*;

// bro what am i doing

public class BotBlueprint {
    public UnitType type;
    public GameSupplier<MapLocation> spawnSupplier;
    public GameSupplier<Packet[]> packetSupplier;

    public BotBlueprint(UnitType type, GameSupplier<MapLocation> spawnSupplier, GameSupplier<Packet[]> packetSupplier) {
        this.type = type;
        this.spawnSupplier = spawnSupplier;
        this.packetSupplier = packetSupplier;
    }

    public BotBlueprint(UnitType type, GameSupplier<Packet[]> packetSupplier) {
        this(type, () -> {
            MapInfo res = getSafeSpawnLoc(UnitType.SOLDIER);
            if (res == null) return null;
            else return res.getMapLocation();
        }, packetSupplier);
    }
}
