package caterpillow.robot.agents.mopper;

import battlecode.common.*;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.agents.Agent;

import static java.lang.Math.min;

public class Mopper extends Agent {
    @Override
    public void init() throws GameActionException {
        super.init();

        pathfinder = new BugnavPathfinder();
        primaryStrategy = new MopperDefenceStrategy();
    }
}
