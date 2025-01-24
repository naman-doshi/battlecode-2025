package caterpillow.robot.agents;

import battlecode.common.MapLocation;
import caterpillow.robot.agents.soldier.BuildTowerStrategy;
import caterpillow.robot.troll.DequeStrategy;

public class BuildTowerStrategyWrapper extends DequeStrategy {
    public BuildTowerStrategyWrapper(MapLocation target) {
        super();
        push_back(new BuildTowerStrategy(target));
    }
}
