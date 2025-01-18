package caterpillow.robot.agents.roaming;

import java.util.List;
import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import caterpillow.Config;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.Game.seed;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.soldier.Soldier;

// when u wanna push in the general direction of the enemy
public class WeakAggroRoamStrategy extends Strategy {

    Agent bot;
    public MapLocation target;
    Random rng;

    List<MapLocation> targets;

    public WeakAggroRoamStrategy() throws GameActionException {
        bot = (Agent) Game.bot;
        rng = new Random(seed);
        target = Config.genAggroTarget(rng);
    }
    public WeakAggroRoamStrategy(MapLocation target) throws GameActionException {
        this();
        this.target = target;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        while (rc.canSenseLocation(target)) {
            target = Config.genAggroTarget(rng);
        }

        Direction dir = bot.pathfinder.getMove(target);

        // make sure we're painting ahead of us, if we're a soldier
        // soldier uses weakaggro
        if (bot.getClass()==Soldier.class) {
            Soldier bot1 = (Soldier) bot;
            if (dir != null && rc.onTheMap(rc.getLocation().add(dir))) {
                MapLocation next = rc.getLocation().add(dir);
                MapInfo info = rc.senseMapInfo(next);
                if (rc.canAttack(next) && info.getPaint().equals(PaintType.EMPTY)) {
                    bot1.checkerboardAttack(next);
                }
            }
        }

        bot.pathfinder.makeMove(dir);
        rc.setIndicatorLine(rc.getLocation(), target, 0, 0, 255);
    }
}
