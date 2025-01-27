package caterpillow.robot.agents;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.Config;
import caterpillow.Game;
import static caterpillow.Game.*;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.Robot;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.mopper.Mopper;
import caterpillow.robot.agents.soldier.Soldier;
import caterpillow.tracking.CellTracker;
import static caterpillow.tracking.CellTracker.postMove;
import caterpillow.tracking.RobotTracker;
import caterpillow.tracking.TowerTracker;
import caterpillow.util.Util;
import static caterpillow.util.Util.*;

public abstract class Agent extends Robot {
    public BugnavPathfinder pathfinder;

    public MapLocation home;
    public int homeID;
    public int ticksRanOutOfPaint = 0;

    public Strategy primaryStrategy;
    public Strategy secondaryStrategy;
    public int noPaintThreshold = 1; // exclusive
    public int noPaintTicks = 20;

    public void build(UnitType type, MapLocation loc) throws GameActionException{
        rc.completeTowerPattern(type, loc);
        TowerTracker.sendInitPacket(loc);
    }

    public int refill(RobotInfo bot) throws GameActionException {
        int pool = bot.getPaintAmount();
        int missing = missingPaint();
        if (rc.canTransferPaint(bot.getLocation(), -Math.min(pool, missing))) {
            rc.transferPaint(bot.getLocation(), -Math.min(pool, missing));
            return Math.min(pool, missing);
        }
        return 0;
    }

    public int donate(RobotInfo bot) throws GameActionException {
        int missing = missingPaint(bot);
        int available = rc.getPaint() - 20;
        if (available < 0) {
            return 0;
        }
        if (rc.canTransferPaint(bot.getLocation(), Math.min(available, missing))) {
            rc.transferPaint(bot.getLocation(), Math.min(available, missing));
            return Math.min(available, missing);
        }
        return 0;
    }

    public boolean lastMove = false;
    public void move(Direction dir) throws GameActionException {
        rc.move(dir);
        Game.pos = rc.getLocation();
        postMove(dir, false);
        // we can skip updating some stuff if moving is the last thing we do in a turn
        if(!lastMove) {
            RobotTracker.updateTick();
        }
    }


    public void setParent(RobotInfo parent) {
        assert parent != null;
        home = parent.getLocation();
        homeID = parent.getID();
    }

    @Override
    public void init() throws GameActionException {
        // set home
        RobotInfo parent = TowerTracker.getNearestVisibleTower(Util::isFriendly);
        if (parent != null) {
            setParent(parent);
        } else {
            // rip parent died
            // tiny edge case where your spawning tower dies at the same time you spawn
            MapLocation ruin = CellTracker.getNearestRuin(c -> true);
            assert ruin != null;
            UnitType type = Config.nextTowerType(ruin);
            if (rc.canCompleteTowerPattern(type, ruin)) {
                rc.completeTowerPattern(type, ruin);
                assert rc.senseRobotAtLocation(ruin) != null;
                setParent(rc.senseRobotAtLocation(ruin));
            } else {
                // there is this stupid edge case where you get spawned, and then the tower dies before it can send over a message
                origin = ruin;
                home = ruin;
            }
        }
    }

    @Override
    public void runTick() throws GameActionException {
        lastMove = false;
        // kms so i dont bleed paint from other bots
        if (rc.getPaint() < noPaintThreshold) {
            ticksRanOutOfPaint++;
        } else {
            ticksRanOutOfPaint = 0;
        }

        if (ticksRanOutOfPaint >= noPaintTicks) {
            dead("ran out of paint");
            System.out.println("kms");
            return;
        }

        if (secondaryStrategy != null) {
            if (secondaryStrategy.isComplete()) {
                secondaryStrategy = null;
            }
        }
        if (secondaryStrategy != null) {
            secondaryStrategy.runTick();
        } else {
            if (primaryStrategy.isComplete()) {
                dead("primary strategy completed");
                return;
            }
            primaryStrategy.runTick();
        }
    }
}
