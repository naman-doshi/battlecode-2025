package fix_atk_micro.robot.agents.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import fix_atk_micro.Game;
import fix_atk_micro.robot.agents.Agent;
import fix_atk_micro.robot.troll.QueueStrategy;
import static fix_atk_micro.util.Util.indicate;

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
