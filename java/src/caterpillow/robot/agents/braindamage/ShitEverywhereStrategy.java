package caterpillow.robot.agents.braindamage;

import battlecode.common.*;
import caterpillow.Game;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.WanderStrategy;
import caterpillow.tracking.CellTracker;


import static caterpillow.Game.*;
import static caterpillow.util.Util.*;

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
            MapInfo target = CellTracker.getNearestCell(c -> rc.canPaint(c.getMapLocation()) && c.getPaint().equals(PaintType.EMPTY));
            if (target != null && rc.canAttack(target.getMapLocation())) {
                rc.attack(target.getMapLocation());
            }
        }
    }
}
