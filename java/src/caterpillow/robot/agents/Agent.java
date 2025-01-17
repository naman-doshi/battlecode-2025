package caterpillow.robot.agents;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;
import caterpillow.Config;
import caterpillow.Game;
import static caterpillow.Game.origin;
import static caterpillow.Game.pm;
import static caterpillow.Game.rc;
import static caterpillow.Game.ticksExisted;
import caterpillow.pathfinding.AbstractPathfinder;
import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.Robot;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.mopper.MopperOffenceStrategy;
import caterpillow.util.TowerTracker;
import static caterpillow.util.Util.dead;
import static caterpillow.util.Util.getNearestCell;
import static caterpillow.util.Util.getNearestRobot;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.isSRPCenter;
import static caterpillow.util.Util.missingPaint;

public abstract class Agent extends Robot {
    public AbstractPathfinder pathfinder;

    public MapLocation home;
    public int homeID;
    public int ticksRanOutOfPaint = 0;

    public Strategy primaryStrategy;
    public Strategy secondaryStrategy;

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
        int available = rc.getPaint() - UnitType.MOPPER.paintCapacity / 2;
        if (available < 0) {
            return 0;
        }
        if (rc.canTransferPaint(bot.getLocation(), Math.min(available, missing))) {
            rc.transferPaint(bot.getLocation(), Math.min(available, missing));
            return Math.min(available, missing);
        }
        return 0;
    }

    public void setParent(RobotInfo parent) {
        assert parent != null;
        home = parent.getLocation();
        homeID = parent.getID();
    }

    @Override
    public void init() throws GameActionException {
        // set home
        RobotInfo parent = getNearestRobot(info -> info.getType().isTowerType() && isFriendly(info));
        if (parent != null) {
            setParent(parent);
        } else {
            // rip parent died
            // tiny edge case where your spawning tower dies at the same time you spawn
            MapInfo ruin = getNearestCell(c -> c.hasRuin());
            assert ruin != null;
            UnitType type = Config.getNextType();
            if (rc.canCompleteTowerPattern(type, ruin.getMapLocation())) {
                rc.completeTowerPattern(type, ruin.getMapLocation());
                assert rc.senseRobotAtLocation(ruin.getMapLocation()) != null;
                setParent(rc.senseRobotAtLocation(ruin.getMapLocation()));
            } else {
                // there is this stupid edge case where you get spawned, and then the tower dies before it can send over a message
                origin = ruin.getMapLocation();
                home = ruin.getMapLocation();
            }
        }
    }

    @Override
    public void runTick() throws GameActionException {
        
        if (ticksExisted >= 2) {
            for (MapInfo cell : rc.senseNearbyMapInfos()) {
                MapLocation loc = cell.getMapLocation();
                if (isSRPCenter(loc)) {
                    if (rc.getChips() >= 1200 && rc.canCompleteResourcePattern(loc)) {
                        rc.completeResourcePattern(loc);
                    }
                }
            }
        }

        // kms so i dont bleed paint from other bots
        if (rc.getPaint() < 5) {
            ticksRanOutOfPaint++;
        } else {
            ticksRanOutOfPaint = 0;
        }

        if (ticksRanOutOfPaint >= 20) {
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
