package fix_atk_micro.robot.agents.soldier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import battlecode.common.*;
import fix_atk_micro.Game;
import static fix_atk_micro.Game.*;
import fix_atk_micro.robot.Strategy;
import fix_atk_micro.robot.agents.Agent;
import fix_atk_micro.robot.agents.TraverseStrategy;
import fix_atk_micro.tracking.TowerTracker;
import static fix_atk_micro.util.Util.*;
import fix_atk_micro.util.Profiler;

// pathfinding testing
public class RushStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    List<MapLocation> todo;
    Set<MapLocation> ruinsTrolled = new HashSet<>();

    Strategy traverseStrategy;
    AttackTowerStrategy attackTowerStrategy;
    Strategy trollRuinStrategy;

    int distanceThreshold; // max chebyshev distance to rush
    int timeThreshold = 100; // round after which we switch to scouting

    public RushStrategy(int distanceThreshold) throws GameActionException {
        bot = (Agent) Game.bot;
        this.distanceThreshold = distanceThreshold;
        todo = guessEnemyLocs(origin, true);
        while(true) {
            if (todo.isEmpty()) {
                // become a scout
                target = null;
                break;
            }
            target = todo.get(0);
            todo.remove(0);
            if(chebyshevDistance(origin, target) > distanceThreshold) continue;
            traverseStrategy = new TraverseStrategy(target, 9);
            break;
        }
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    boolean tryStrats() throws GameActionException {
        if (attackTowerStrategy == null) {
            RobotInfo enemyTower = TowerTracker.getNearestVisibleTower(b -> !isFriendly(b));
            if (enemyTower != null) {
                attackTowerStrategy = new AttackTowerStrategy(enemyTower.getLocation());
            }
        }
        if (tryStrategy(attackTowerStrategy)) return true;
        attackTowerStrategy = null;

        if (trollRuinStrategy == null) {
            MapInfo[] neighbours = Game.rc.senseNearbyMapInfos();
            for (MapInfo info : neighbours) {
                RobotInfo botThere = Game.rc.senseRobotAtLocation(info.getMapLocation());
                if (info.hasRuin() && botThere == null && !ruinsTrolled.contains(info.getMapLocation())) {
                    trollRuinStrategy = new TrollRuinStrategy(info.getMapLocation());
                    ruinsTrolled.add(info.getMapLocation());
                    break;
                }
            }
        }
        if(!tryStrategy(trollRuinStrategy)) trollRuinStrategy = null; // we want to "fall through" even if this strategy is not complete
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
//        Profiler.begin();
        indicate("RUSHING TO " + todo);

        // become a scout once done
        if (target == null || time > timeThreshold || rc.getHealth() <= 40) {
            System.out.println("converting to scout");
            bot.primaryStrategy = new ScoutStrategy();
            ((ScoutStrategy)bot.primaryStrategy).attackTowerStrategy = attackTowerStrategy;
            bot.primaryStrategy.runTick();
            return;
        }

        RobotInfo robot = null;
        if(rc.canSenseLocation(target)) {
            robot = rc.senseRobotAtLocation(target);
        }
        if (traverseStrategy.isComplete() || rc.canSenseLocation(target) && (robot == null || robot.team == team || !robot.type.isTowerType())) {
            while(true) {
                if (todo.isEmpty()) {
                    // become a scout
                    target = null;
                    runTick();
                    return;
                }
                target = todo.get(0);
                todo.remove(0);
                if(chebyshevDistance(origin, target) > distanceThreshold) {
                    continue;
                }
                traverseStrategy = new TraverseStrategy(target, 9);
                break;
            }
        }
        if(tryStrats()) return;
        indicate("TRAVERSE");
        traverseStrategy.runTick();
        if(rc.isActionReady()) tryStrats();
//        Profiler.end("rush");
    }
}
