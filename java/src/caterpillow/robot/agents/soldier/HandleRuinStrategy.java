package caterpillow.robot.agents.soldier;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.troll.QueueStrategy;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.isTowerBeingBuilt;
import static caterpillow.util.Util.maxedTowers;

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
