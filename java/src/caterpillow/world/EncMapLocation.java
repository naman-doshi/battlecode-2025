package caterpillow.world;

import battlecode.common.MapLocation;

public class EncMapLocation{
    final int SIZE = 60;

    public int x, y;

    public EncMapLocation(int id) {
        x = id / SIZE;
        y = id % SIZE;
    }

    EncMapLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int enc() {
        return x * SIZE + y;
    }
}
