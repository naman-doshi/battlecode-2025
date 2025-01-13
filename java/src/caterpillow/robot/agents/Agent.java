package caterpillow.robot.agents;

import battlecode.common.*;
import caterpillow.packet.packets.InitPacket;
import caterpillow.packet.packets.OriginPacket;
import caterpillow.pathfinding.AbstractPathfinder;
import caterpillow.robot.Robot;
import caterpillow.robot.Strategy;
import caterpillow.util.TowerTracker;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;
import static java.lang.Math.max;

public abstract class Agent extends Robot {
    public AbstractPathfinder pathfinder;

    public MapLocation home;
    public int homeID;

    public Strategy primaryStrategy;
    public Strategy secondaryStrategy;

    public void build(UnitType type, MapLocation loc) throws GameActionException{
        rc.completeTowerPattern(type, loc);

        int tot = TowerTracker.totTowers + 1;
        int coin = TowerTracker.coinTowers;
        if (type.equals(UnitType.LEVEL_ONE_MONEY_TOWER)) {
            coin++;
        }

        if (TowerTracker.broken || tot >= 16) {
            pm.send(loc, new InitPacket(origin, 0, 0));
        } else {
            pm.send(loc, new InitPacket(origin, tot, coin));
        }
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

    public void setParent(RobotInfo parent) {
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
            UnitType type = TowerTracker.getNextType();
            if (rc.canCompleteTowerPattern(type, ruin.getMapLocation())) {
                rc.completeTowerPattern(type, ruin.getMapLocation());
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
