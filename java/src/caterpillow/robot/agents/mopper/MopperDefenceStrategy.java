package caterpillow.robot.agents.mopper;

import static java.lang.Math.min;

import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.Strategy;
import static caterpillow.tracking.RobotTracker.getNearestRobot;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.isFriendly;

public class MopperDefenceStrategy extends Strategy {

    public Mopper bot;

    public MopperDefenceStrategy() {
        bot = (Mopper) Game.bot;
        bot.pathfinder = new BugnavPathfinder(c -> !c.getPaint().isAlly());
    }

    private boolean isInDanger() throws GameActionException {
        return getNearestRobot(bot -> !isFriendly(bot)) != null;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return !isInDanger();
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("DEFENCE MOPPER");
        RobotInfo target = getNearestRobot(bot -> !isFriendly(bot));
        bot.pathfinder.makeMove(target.getLocation());

        // temporary shitty solution to dump paint in tower if u can reach it
        if (rc.canSenseLocation(bot.home)) {
            RobotInfo tower = rc.senseRobotAtLocation(bot.home);
            if (tower != null) {
                int amt = min(min(50, rc.getPaint()), tower.getType().paintCapacity - tower.getPaintAmount());
                if (rc.canTransferPaint(bot.home, amt)) {
                    rc.transferPaint(bot.home, amt);
                }
            } else {
                // rip this guy is homeless
            }
        }

        bot.doBestAttack(null);

        
    }
}
