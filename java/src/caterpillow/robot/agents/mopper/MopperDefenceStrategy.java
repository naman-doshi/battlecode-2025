package caterpillow.robot.agents.mopper;

import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Config;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.robot.agents.roaming.AggroRoamStrategy;
import caterpillow.tracking.RobotTracker;
import caterpillow.util.GameSupplier;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.isAllyAgent;
import static caterpillow.util.Util.isEnemyAgent;
import static caterpillow.util.Util.isPaintBelowHalf;

public class MopperDefenceStrategy extends Strategy {

    public Mopper bot;

    public List<GameSupplier<MapInfo>> suppliers;
    Random rng;
    public MapLocation lastSeenRuin;
    Strategy rescueStrategy;
    Strategy refillStrategy;
    Strategy roamStrategy;
    boolean goingHome = false;

    public MopperDefenceStrategy() throws GameActionException {
        bot = (Mopper) Game.bot;
        rng = new Random();
        roamStrategy = new AggroRoamStrategy();
    }


    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("homeland security!!1!11!!");

        if (rescueStrategy == null) {
            RobotInfo nearest = RobotTracker.getNearestRobot(b -> isAllyAgent(b) && Config.shouldRescue(b));
            if (nearest != null) {
                rescueStrategy = new RescueStrategy(nearest.getLocation());
            }
        }

        if (tryStrategy(rescueStrategy)) return;
        rescueStrategy = null;

        if (refillStrategy == null && isPaintBelowHalf()) {
            refillStrategy = new WeakRefillStrategy(0.2);
        }

        if (tryStrategy(refillStrategy)) return;
        refillStrategy = null;

        // chase enemy until you're around 7 squares away from home, then come back
        bot.doBestAttack();


        if (Game.rc.getLocation().distanceSquaredTo(bot.home) < 20) {
            goingHome = false;
        }

        // find nearest enemy
        RobotInfo nearest = RobotTracker.getNearestRobot(b -> isEnemyAgent(b));
        
        if (nearest != null && Game.rc.getLocation().distanceSquaredTo(bot.home) <= 80) {
            bot.pathfinder.makeMove(nearest.getLocation());
        } else if (Game.rc.getLocation().distanceSquaredTo(bot.home) > 100 || goingHome) {
            goingHome = true;
            bot.pathfinder.makeMove(bot.home);
        } else {
            roamStrategy.runTick();
        }

        

        

        

        
    }
}
