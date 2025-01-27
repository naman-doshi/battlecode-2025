package fix_atk_micro.robot.towers.money;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import battlecode.common.RobotInfo;
import static fix_atk_micro.Config.shouldConvertMoneyToPaint;
import static fix_atk_micro.Game.*;
import fix_atk_micro.robot.towers.TowerStrategy;

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
                // donate paint to surrounding bots
                RobotInfo[] bots = rc.senseNearbyRobots();
                for (RobotInfo bot : bots) {
                    if (bot.getTeam() == team && bot.getType().isRobotType()) {
                        int missing = bot.getType().paintCapacity - bot.getPaintAmount();
                        if (missing > 0) {
                            int amt = Math.min(rc.getPaint(), missing);
                            if (rc.getPaint() == 0) break;
                            if (rc.canTransferPaint(bot.location, amt)) {
                                rc.transferPaint(bot.location, amt);
                            }
                        }
                    }
                }
                rc.disintegrate();
            }
        }
    }
}
