package caterpillow.robot.agents;

import battlecode.common.*;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.agents.strategies.mopper.MopperDefenceStrategy;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;
import static java.lang.Math.min;

public class Mopper extends Agent {
    @Override
    public void init() throws GameActionException {
        super.init();

        pathfinder = new BugnavPathfinder();
        primaryStrategy = new MopperDefenceStrategy();
    }
}
