package caterpillow.robot.agents.soldier;

import battlecode.common.*;
import caterpillow.Game;
import caterpillow.robot.*;
import caterpillow.robot.agents.*;
import caterpillow.tracking.CellTracker;

import static caterpillow.Game.*;
import static java.lang.Math.*;

public class PaintSRPStrategy extends Strategy {

    final boolean[][] SRP = {
        {true, true, false, true, true},
        {true, false, false, false, true},
        {false, false, true, false, false},
        {true, false, false, false, true},
        {true, true, false, true, true}
    };

    Agent bot;
    public MapLocation centre;

    public PaintSRPStrategy(MapLocation centre) {
        bot = (Agent)Game.bot;
        this.centre = centre;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        for(int i = centre.x - 2; i <= centre.x + 2; i++) {
            for(int j = centre.y - 2; j <= centre.y + 2; j++) {
                if(!rc.canSenseLocation(new MapLocation(i, j))) return false;
                MapInfo cell = rc.senseMapInfo(new MapLocation(i, j));
                if(cell.getPaint().isEnemy()) return true;
                if(!cell.getPaint().isAlly()) return false;
                if(cell.getPaint().equals(PaintType.ALLY_SECONDARY) != SRP[i - centre.x + 2][j - centre.y + 2]) return false;
            }
        }
        return rc.senseMapInfo(centre).isResourcePatternCenter();
    }

    @Override
    public void runTick() throws GameActionException {
        if(rc.isMovementReady()) {
            Direction dir = null;
            if(rc.getLocation().equals(centre)) {
                dir = Direction.NORTH;
            } else if(rc.getLocation().isAdjacentTo(centre)) {
                dir = rc.getLocation().directionTo(centre).rotateRight();
            } else {
                bot.pathfinder.makeMove(centre);
            }
            if(dir != null && rc.canMove(dir)) bot.move(dir);
        }
        rc.setIndicatorLine(rc.getLocation(), centre, 255, 255, 0);
        if(rc.canCompleteResourcePattern(centre)) {
            rc.completeResourcePattern(centre);
        }
        MapInfo cell = CellTracker.getNearestCell(c -> {
                MapLocation loc = c.getMapLocation();
                if(abs(loc.x - centre.x) > 2 || abs(loc.y - centre.y) > 2) return false;
                if(c.getPaint().isEnemy()) return false;
                if(!c.getPaint().isAlly()) return true;
                return c.getPaint().equals(PaintType.ALLY_SECONDARY) != SRP[loc.x - centre.x + 2][loc.y - centre.y + 2];
            });
        MapLocation loc = cell == null ? null : cell.getMapLocation();
        if(loc != null && rc.canAttack(loc)) {
            rc.attack(loc, SRP[loc.x - centre.x + 2][loc.y - centre.y + 2]);
        }
    }
}
