package caterpillow.robot.agents.soldier;

import java.util.List;
import caterpillow.util.CustomRandom;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import caterpillow.Config;
import caterpillow.Game;
import static caterpillow.util.Util.*;
import static caterpillow.Game.*;
import static caterpillow.tracking.CellTracker.*;
import static caterpillow.tracking.TowerTracker.*;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;

// when u wanna push in the general direction of the enemy
public class ScoutRoamStrategy extends Strategy {
    Soldier bot;
    public MapLocation target;
    CustomRandom rng;

    List<MapLocation> targets;

    public ScoutRoamStrategy() throws GameActionException {
        bot = (Soldier) Game.bot;
        rng = new CustomRandom(seed);
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
        if(time <= 8 && getNearestVisibleTower(info -> info.team.equals(team) && !info.location.equals(origin) && rc.getLocation().distanceSquaredTo(info.location) < 16) != null) {
            target = null;
        }
        while (target == null || rc.getLocation().distanceSquaredTo(target) <= 8) {
            target = Config.genAggroTarget(rng); // TODO: change this
        }

        bot.pathfinder.makeMove(target);
        rc.setIndicatorLine(rc.getLocation(), target, 0, 0, 255);
    }
}
