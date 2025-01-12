package caterpillow.robot.agents.soldier;

import java.util.List;

import battlecode.common.*;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;
import caterpillow.util.GameSupplier;
import static caterpillow.util.Util.checkerboardPaint;
import static caterpillow.util.Util.guessEnemyLocs;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

public class SRPStrategy extends Strategy {

    public Soldier bot;

    // enemyLocs is more like "POI locs"
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;

    public SRPStrategy() throws GameActionException {
        bot = (Soldier) Game.bot;
        this.enemyLocs = guessEnemyLocs(bot.home);
        this.enemy = enemyLocs.get(0);
        enemyLocs.addLast(bot.home);
    }


    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    public void safeMove(MapLocation loc) throws GameActionException {
        if (rc.getLocation().isAdjacentTo(loc) && rc.senseMapInfo(loc).getPaint().isEnemy()) {
            return;
        }
        // wait until andy's buffed pathfinder
        rc.move(bot.pathfinder.getMove(loc));
    }

    @Override
    public void runTick() throws GameActionException {
        rc.setIndicatorString("PAINTING SRPF");
        // TODO: better scouting system!!!
        if (rc.canSenseLocation(enemy)) {
            // if we can see the enemy, just go to the next enemy loc. it's kinda cyclic for now
            enemyLocs.addLast(enemy);
            enemyLocs.removeFirst();
            enemy = enemyLocs.getFirst();

        }

        MapInfo target = getNearestCell(c -> c.getPaint().equals(PaintType.EMPTY) && c.isPassable());
        if (target != null) {
            if (rc.canAttack(target.getMapLocation())) {
                bot.checkerboardAttack(target.getMapLocation());
            } else {
                safeMove(target.getMapLocation());
            }
        } else {
            safeMove(enemy);
        }
    }
}
