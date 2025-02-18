package fix_atk_micro.robot.agents.soldier;

import java.util.List;
import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import fix_atk_micro.Config;
import fix_atk_micro.Game;
import static fix_atk_micro.util.Util.*;
import static fix_atk_micro.Game.*;
import static fix_atk_micro.tracking.CellTracker.*;
import static fix_atk_micro.tracking.TowerTracker.*;
import fix_atk_micro.robot.Strategy;
import fix_atk_micro.robot.agents.Agent;

// when u wanna push in the general direction of the enemy
public class ScoutRoamStrategy extends Strategy {
    Soldier bot;
    public MapLocation target;
    Random rng;

    List<MapLocation> targets;

    public ScoutRoamStrategy() throws GameActionException {
        bot = (Soldier) Game.bot;
        rng = new Random(seed);
        target = Config.genAggroTarget(rng);
    }
    public ScoutRoamStrategy(MapLocation target) throws GameActionException {
        this();
        this.target = target;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        // reset if we run into the other starter tower
        if(time <= 8 && getNearestVisibleTower(info -> info.team.equals(team) && !info.location.equals(origin) && rc.getLocation().distanceSquaredTo(info.location) <= 16) != null) {
            target = null;
        }
        while (target == null || rc.getLocation().distanceSquaredTo(target) <= 8) {
            target = Config.genAggroTarget(rng); // TODO: change this
        }

        bot.pathfinder.makeMove(target);
        rc.setIndicatorLine(rc.getLocation(), target, 0, 0, 255);
    }
}
