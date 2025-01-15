package caterpillow_v1.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow_v1.Game;
import static caterpillow_v1.Game.rc;
import caterpillow_v1.robot.Strategy;
import static caterpillow_v1.util.Util.indicate;

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
