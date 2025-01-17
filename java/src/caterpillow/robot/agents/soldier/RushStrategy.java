package caterpillow.robot.agents.soldier;

import java.util.ArrayList;
import java.util.List;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.TraverseStrategy;
import static caterpillow.util.Util.getNearestRobot;
import static caterpillow.util.Util.guessEnemyLocs;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.isFriendly;

// pathfinding testing
public class RushStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    ArrayList<MapLocation> todo;

    Strategy primary;
    Strategy secondary;

    public RushStrategy() throws GameActionException {
        bot = (Agent) Game.bot;
        List<MapLocation> todo = guessEnemyLocs(bot.home);
        target = todo.get(0);
        todo.remove(0);

        // starting strategy. vision radius squared is 20
        primary = new TraverseStrategy(target, 20);
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return primary instanceof ScoutStrategy && primary.isComplete();
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("RUSHING");
        
        // become a scout once done
        if (target == null && todo==null) {
            if (!(primary instanceof ScoutStrategy)) {
                primary = new ScoutStrategy();
            }
            primary.runTick();
            return;
        }

        if (secondary == null) {
            RobotInfo nearest = getNearestRobot(b -> !isFriendly(b) && b.getType().isTowerType());
            if (nearest != null) {
                secondary = new AttackTowerStrategy(nearest.getLocation());
            }
        }

        if (secondary == null) {
            MapInfo[] neighbours = Game.rc.senseNearbyMapInfos();
            for (MapInfo info : neighbours) {
                RobotInfo botThere = Game.rc.senseRobotAtLocation(info.getMapLocation());
                if (info.hasRuin() && botThere == null) {
                    secondary = new TrollRuinStrategy(info.getMapLocation());
                    break;
                }
            }
        }

        if (secondary != null) {
            if (secondary.isComplete()) {
                secondary = null;
            } else {
                secondary.runTick();
                return;
            }
        }

        if (primary.isComplete()) {
            if (todo==null) {
                // become a scout
                target = null;
                runTick();
                return;
            }
            target = todo.get(0);
            todo.remove(0);
            primary = new TraverseStrategy(target, 20);
        }
        
        primary.runTick();
    }
}
