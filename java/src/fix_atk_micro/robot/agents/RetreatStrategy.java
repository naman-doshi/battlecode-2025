package fix_atk_micro.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import fix_atk_micro.Game;
import static fix_atk_micro.Game.rc;
import fix_atk_micro.robot.Strategy;
import static fix_atk_micro.util.Util.indicate;

// pathfinding testing
public class RetreatStrategy extends Strategy {

    Agent bot;
    MapLocation target;

    public RetreatStrategy() {
        bot = (Agent) Game.bot;
        target = Game.origin;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return (rc.getPaint() >= 50);
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("RETREATING to " + target);
        if (!rc.isMovementReady()) return;
        bot.pathfinder.makeMove(target);
        
    }
}
