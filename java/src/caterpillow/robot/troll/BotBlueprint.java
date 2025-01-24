package caterpillow.robot.troll;

import battlecode.common.*;
import caterpillow.packet.Packet;
import caterpillow.util.GameSupplier;

import static caterpillow.util.Util.*;

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
            MapLocation loc = getSafeSpawnLoc(UnitType.SOLDIER);
            if (loc == null) return null;
            return loc;
        }, packetSupplier);
    }
}
