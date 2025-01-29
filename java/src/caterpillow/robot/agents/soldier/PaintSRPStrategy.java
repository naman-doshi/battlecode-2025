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

    public int[][] lastChange; // prevent deadlocks with tower builders
    public boolean[][] done; // track which cells have been painted correctly
    public boolean[][] lastDone; // last turn's done
    public int doneTimestamp;
    public boolean shouldGiveUp = false;

    public PaintSRPStrategy(MapLocation centre) {
        bot = (Agent)Game.bot;
        this.centre = centre;
        lastChange = new int[5][5];
        done = new boolean[5][5];
        lastDone = new boolean[5][5];
        doneTimestamp = -1;
    }

    public void updateDone() throws GameActionException { // can unroll this if slow
        if(doneTimestamp == time) return;
        doneTimestamp = time;
        for(int i = 4; i >= 0; i--) {
            for(int j = 4; j >= 0; j--) {
                lastDone[i][j] = done[i][j];
                MapLocation loc = new MapLocation(centre.x - 2 + i, centre.y - 2 + j);
                if(rc.canSenseLocation(loc)) {
                    MapInfo cell = rc.senseMapInfo(loc);
                    if(cell.getPaint().isAlly() && cell.getPaint().equals(PaintType.ALLY_SECONDARY) == SRP[i][j]) {
                        done[i][j] = true;
                    } else {
                        done[i][j] = false;
                    }
                    if(lastDone[i][j] && !done[i][j] && cell.getPaint().isAlly()) {
                        if(lastChange[i][j] >= time - 5) {
                            shouldGiveUp = true;
                        }
                        lastChange[i][j] = time;
                    }
                }
            }
        }
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if(CellTracker.ignoreCooldown[centre.x][centre.y] >= time) return true;
        updateDone();
        for(int i = 4; i >= 0; i--) {
            for(int j = 4; j >= 0; j--) {
                if(!rc.canSenseLocation(new MapLocation(centre.x + i - 2, centre.y + j - 2))) return false;
                if(!done[i][j]) return false;
            }
        }
        return rc.senseMapInfo(centre).isResourcePatternCenter();
    }

    @Override
    public void runTick() throws GameActionException {
        updateDone();
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
        } else {
            MapLocation loc = CellTracker.getNearestLocation(loc2 -> abs(loc2.x - centre.x) <= 2 && abs(loc2.y - centre.y) <= 2 && !done[loc2.x - centre.x + 2][loc2.y - centre.y + 2]);
            if(loc != null && rc.canAttack(loc)) {
                rc.attack(loc, SRP[loc.x - centre.x + 2][loc.y - centre.y + 2]);
            }
        }
    }
}
