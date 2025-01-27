package fix_atk_micro.robot.agents.braindamage;

import battlecode.common.*;
import fix_atk_micro.Game;
import fix_atk_micro.robot.Strategy;
import fix_atk_micro.robot.agents.Agent;
import fix_atk_micro.robot.agents.WanderStrategy;
import fix_atk_micro.tracking.CellTracker;


import static fix_atk_micro.Game.*;
import static fix_atk_micro.util.Util.*;

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
