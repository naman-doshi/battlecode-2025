package caterpillow_v1.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;
import caterpillow_v1.Game;
import caterpillow_v1.packet.packets.AdoptionPacket;
import caterpillow_v1.robot.Strategy;

import static caterpillow_v1.util.Util.*;
import static caterpillow_v1.Game.*;

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
                RobotInfo sus = getNearestRobot(info -> info.getTeam().isPlayer() && info.getType().isTowerType());
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