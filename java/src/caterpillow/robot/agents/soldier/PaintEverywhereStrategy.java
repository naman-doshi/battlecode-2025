package caterpillow.robot.agents.soldier;

import java.util.HashSet;
import java.util.Set;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.roaming.RandomRoamStrategy;
import caterpillow.tracking.CellTracker;
import caterpillow.tracking.TowerTracker;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.isFriendly;

// pathfinding testing
public class PaintEverywhereStrategy extends Strategy {

    Agent bot;

    Strategy roamStrategy;
    SoldierAttackTowerStrategy attackTowerStrategy;
    Strategy trollRuinStrategy;
    Set<MapLocation> ruinsTrolled = new HashSet<>();
    public PaintEverywhereStrategy() throws GameActionException {
        bot = (Agent) Game.bot;
        roamStrategy = new RandomRoamStrategy();
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    boolean tryStrats() throws GameActionException {
        if (attackTowerStrategy == null) {
            RobotInfo enemyTower = TowerTracker.getNearestVisibleTower(b -> !isFriendly(b));
            if (enemyTower != null) {
                attackTowerStrategy = new SoldierAttackTowerStrategy(enemyTower.getLocation());
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
        indicate("painting everywhere");

        if(tryStrats()) return;

        MapInfo nearest = CellTracker.getNearestCell(c -> c.getPaint()==PaintType.EMPTY && c.isPassable());
        if (nearest != null) {
            indicate("i see " + nearest.getMapLocation());
            if (rc.canAttack(nearest.getMapLocation())) {
                rc.attack(nearest.getMapLocation());
            } else {
                indicate("moving to " + nearest.getMapLocation());
                bot.pathfinder.makeMove(nearest.getMapLocation());
            }
        } else {
            indicate("roaming");
            roamStrategy.runTick();
        }
        
        if(rc.isActionReady()) tryStrats();
    }
}
