package caterpillow.robot.agents.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import caterpillow.Game;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.troll.QueueStrategy;
import static caterpillow.util.Util.indicate;

public class HandleRuinStrategy extends QueueStrategy {

    MapLocation target;
    Agent bot;

    public HandleRuinStrategy(MapLocation target) {
        bot = (Agent) Game.bot;
        this.target = target;
        push(new BuildTowerStrategy(target));
        push(new TrollRuinStrategy(target));
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("HANDLING RUIN");
        super.runTick();
        // indicate("HANDLING RUIN " + todo.size());
    }
}
