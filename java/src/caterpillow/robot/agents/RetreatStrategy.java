package caterpillow.robot.agents;

import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.Game.seed;
import caterpillow.robot.Strategy;
import static caterpillow.util.Util.indicate;

// pathfinding testing
public class RetreatStrategy extends Strategy {

    Agent bot;
    MapInfo target;
    Random rng;

    public RetreatStrategy() {
        bot = (Agent) Game.bot;
        rng = new Random(seed);
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return (rc.getPaint() >= 50);
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("RETREATING");
        if (!rc.isMovementReady()) return;

        bot.pathfinder.makeMove(Game.origin);
        
    }
}
