package caterpillow_v1.robot.agents.braindamage;

import battlecode.common.*;
import caterpillow_v1.Game;
import caterpillow_v1.robot.Strategy;
import caterpillow_v1.robot.agents.Agent;
import caterpillow_v1.robot.agents.WanderStrategy;


import static caterpillow_v1.Game.*;
import static caterpillow_v1.util.Util.*;

// pathfinding testing
public class ShitEverywhereStrategy extends Strategy {

    Agent bot;

    Strategy wander;

    public ShitEverywhereStrategy() {
        bot = (Agent) Game.bot;
        this.wander = new WanderStrategy();
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("SHITTING EVERYWHERE");
        wander.runTick();
        if (rc.isActionReady()) {
            MapInfo target = getNearestCell(c -> rc.canPaint(c.getMapLocation()) && c.getPaint().equals(PaintType.EMPTY));
            if (target != null && rc.canAttack(target.getMapLocation())) {
                rc.attack(target.getMapLocation());
            }
        }
    }
}
