package caterpillow.robot.agents.soldier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.TraverseStrategy;
import static caterpillow.util.Util.guessEnemyLocs;
import static caterpillow.util.Util.indicate;

// pathfinding testing
public class RushStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    List<MapLocation> todo;
    Set<MapLocation> ruinsTrolled = new HashSet<>();

    Strategy primary;
    Strategy secondary;

    public RushStrategy() throws GameActionException {
        bot = (Agent) Game.bot;
        todo = guessEnemyLocs(bot.home);
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
        indicate("RUSHING TO " + todo);

        if (primary instanceof ScoutStrategy) {
            primary.runTick();
            return;
        }
        
        // become a scout once done
        if (target == null || todo.isEmpty()) {
            if (!(primary instanceof ScoutStrategy)) {
                primary = new ScoutStrategy();
            }
            primary.runTick();
            return;
        }

        // if (secondary == null) {
        //     if (Game.rc.canSenseLocation(target)) {
        //         RobotInfo botThere = Game.rc.senseRobotAtLocation(target);
        //         if (botThere != null && botThere.getType().isTowerType() && !isFriendly(botThere)) {
        //             primary = new ScoutStrategy();
        //             return;
        //         }
        //     }
        // }

        if (secondary == null) {
            MapInfo[] neighbours = Game.rc.senseNearbyMapInfos();
            for (MapInfo info : neighbours) {
                RobotInfo botThere = Game.rc.senseRobotAtLocation(info.getMapLocation());
                if (info.hasRuin() && botThere == null && !ruinsTrolled.contains(info.getMapLocation())) {
                    secondary = new TrollRuinStrategy(info.getMapLocation());
                    ruinsTrolled.add(info.getMapLocation());
                    break;
                }
            }
        }

        if (secondary != null) {
            if (secondary.isComplete()) {
                indicate("SECONDARY STRATEGY COMPLETE");
                secondary = null;
            } else {
                secondary.runTick();
                return;
            }
        }

        if (primary.isComplete()) {
            if (todo.isEmpty()) {
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
