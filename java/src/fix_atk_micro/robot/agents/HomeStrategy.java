package fix_atk_micro.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;
import fix_atk_micro.Game;
import fix_atk_micro.packet.packets.AdoptionPacket;
import fix_atk_micro.robot.Strategy;
import fix_atk_micro.tracking.RobotTracker;

import static fix_atk_micro.util.Util.*;
import static fix_atk_micro.Game.*;

// TODO: make this smarter in the future ig?
// half of this is untested
public class HomeStrategy extends Strategy {

    Agent bot;

    public HomeStrategy() {
        bot = (Agent) Game.bot;
    }

    @Override
    public boolean isComplete() {
        return bot.home != null && rc.getLocation().isAdjacentTo(bot.home);
    }

    @Override
    public void runTick() throws GameActionException {
        if (bot.home != null) {
            // try to return to the last known location of your home tower
            if (rc.canSenseLocation(bot.home)) {
                RobotInfo sus = rc.senseRobotAtLocation(bot.home);
                if (sus == null) {
                    bot.home = null;
                } else {
                    bot.setParent(sus);
                    pm.send(sus.getID(), new AdoptionPacket());
                }
            }
        }
        if (bot.home == null) {
            if (rc.isActionReady()) {
                RobotInfo sus = RobotTracker.getNearestRobot(info -> info.getTeam().isPlayer() && info.getType().isTowerType());
                if (sus != null) {
                    bot.setParent(sus);
                    pm.send(sus.getID(), new AdoptionPacket());
                }
            }
        }
        if (rc.isMovementReady()) {
            if (bot.home == null) {
                indicate("RETURNING ORIGIN " + origin.toString());
                bot.pathfinder.makeMove(origin);
                rc.setIndicatorLine(rc.getLocation(), origin, 0, 255, 0);
            } else {
                indicate("RETURNING HOME " + bot.home.toString());
                bot.pathfinder.makeMove(bot.home);
                rc.setIndicatorLine(rc.getLocation(), bot.home, 0, 255, 0);
            }
        }
    }
}