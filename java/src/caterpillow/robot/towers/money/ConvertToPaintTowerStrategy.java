package caterpillow.robot.towers.money;

import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import battlecode.common.RobotInfo;
import static caterpillow.Config.shouldConvertMoneyToPaint;
import static caterpillow.Config.shouldHaveSuicidalMoneyTowers;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.Game.team;
import caterpillow.robot.towers.TowerStrategy;

public class ConvertToPaintTowerStrategy extends TowerStrategy {
    final boolean[][] paintTowerPattern = {
        {true, false, false, false, true},
        {false, true, false, true, false},
        {false, false, true, false, false},
        {false, true, false, true, false},
        {true, false, false, false, true},
    };

    int specialSuicideModulus;

    public ConvertToPaintTowerStrategy() {
        Random r = new Random();
        specialSuicideModulus = r.nextInt(1, 5) * 17;

    }

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
            if(convertToPaintTower && !shouldHaveSuicidalMoneyTowers()) {
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

        // include the suicide case here cause why not
        if (shouldHaveSuicidalMoneyTowers() && rc.getPaint() < 100 && rc.getChips() >= 2000 && Game.time % specialSuicideModulus == 0) {
            System.out.println("suiciding " + rc.getLocation());
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
