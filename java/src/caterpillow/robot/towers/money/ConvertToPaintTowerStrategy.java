package caterpillow.robot.towers.money;

import battlecode.common.*;
import caterpillow.robot.towers.TowerStrategy;
import static caterpillow.Game.rc;
import static caterpillow.Config.*;

public class ConvertToPaintTowerStrategy extends TowerStrategy {
    final boolean[][] paintTowerPattern = {
        {true, false, false, false, true},
        {false, true, false, true, false},
        {false, false, true, false, false},
        {false, true, false, true, false},
        {true, false, false, false, true},
    };

    @Override
    public void runTick() throws GameActionException {
        if(shouldConvertMoneyToPaint()) {
            boolean convertToPaintTower = true;
            MapLocation loc = rc.getLocation();
            for(int x = loc.x - 2; x <= loc.x + 2; x++) {
                for(int y = loc.y - 2; y <= loc.y + 2; y++) {
                    if(x == loc.x && y == loc.y) continue;
                    PaintType paint = rc.senseMapInfo(new MapLocation(x, y)).getPaint();
                    if(paint.isAlly() && paint.equals(PaintType.ALLY_SECONDARY) == paintTowerPattern[x - loc.x + 2][y - loc.y + 2]) {
                        continue;
                    }
                    convertToPaintTower = false;
                    break;
                }
                if(!convertToPaintTower) break;
            }
            if(convertToPaintTower) {
                System.out.println("converting to paint tower at " + loc.toString());
                rc.disintegrate();
            }
        }
    }
}
