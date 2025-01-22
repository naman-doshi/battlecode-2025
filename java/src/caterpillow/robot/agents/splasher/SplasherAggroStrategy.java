package caterpillow.robot.agents.splasher;

import java.util.List;

import battlecode.common.*;
import caterpillow.robot.agents.roaming.AggroRoamStrategy;
import caterpillow.Game;
import static caterpillow.Game.*;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.StrongRefillStrategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.robot.agents.roaming.StrongAggroRoamStrategy;
import caterpillow.robot.agents.splasher.SplasherAttackTowerStrategy;
import static caterpillow.tracking.RobotTracker.getNearestRobot;
import static caterpillow.tracking.TowerTracker.*;
import caterpillow.util.GameSupplier;
import static caterpillow.util.Util.*;
import caterpillow.util.Pair;

public class SplasherAggroStrategy extends Strategy {

    public Splasher bot;

    // enemyLocs is more like "POI locs"
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;
    public List<GameSupplier<MapInfo>> suppliers;
    MapLocation lastSeenTower;

    Strategy strongRefillStrategy;
    Strategy weakRefillStrategy;
    Strategy attackTowerStrategy;
    Strategy roamStrategy;

    public SplasherAggroStrategy() throws GameActionException {
        bot = (Splasher) Game.bot;
        //assert (Game.origin != null) : "origin is null";
        roamStrategy = new AggroRoamStrategy(); // test\
        lastSeenTower = origin;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {

        indicate("SPLASHER");
        // don't delete this, i want to test whether it should retreat to lastSeenTower or Game.origin on actual scrims
        RobotInfo nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType());
        if (nearest != null) {
            lastSeenTower = nearest.getLocation();
        }

        if(strongRefillStrategy == null && getPaintLevel() < 0.2) {
            strongRefillStrategy = new StrongRefillStrategy(0.5);
        }
        if (weakRefillStrategy == null && getPaintLevel() < 0.5) {
            weakRefillStrategy = new WeakRefillStrategy(0.6);
        }
        if (tryStrategy(weakRefillStrategy)) return;
        weakRefillStrategy = null;

        if(attackTowerStrategy == null) {
            RobotInfo tower = getNearestVisibleTower(info -> info.team != team);
            if(tower != null) {
                attackTowerStrategy = new SplasherAttackTowerStrategy(tower.getLocation());
            }
        }
        if(tryStrategy(attackTowerStrategy)) return;
        attackTowerStrategy = null;

        Pair<MapLocation, Boolean> res = bot.bestAttackLocation();
        MapLocation target = res.first;
        boolean paintType = res.second;
        bot.lastMove = true;
        if (target != null) {
            if(rc.canAttack(target)) {
                rc.attack(target, paintType);
                // move towards next target (too bytecode expensive)
                // target = bot.bestAttackLocation().first;
                // if(target != null) bot.pathfinder.makeMove(target);
            } else {
                bot.pathfinder.makeMove(bot.pathfinder.getMove(target));
                if(rc.canAttack(target)) rc.attack(target, paintType);
            }
        }
        roamStrategy.runTick();
    }
}
