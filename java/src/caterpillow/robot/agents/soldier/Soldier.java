package caterpillow.robot.agents.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import static caterpillow.Game.*;
import static caterpillow.util.Util.*;

import caterpillow.packet.packets.StrategyPacket;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.LinkStrategy;
import caterpillow.tracking.CellTracker;

public class Soldier extends Agent {

    @Override
    public void init() throws GameActionException {
        super.init();

        pathfinder = new BugnavPathfinder(c -> rc.getHealth() <= 25 && isInDanger(c.getMapLocation()), c -> {
                MapLocation loc = c.getMapLocation();
                int res = 0;
                if(loc.x < 4) res += 4 - loc.x;
                if(loc.x > mapWidth - 5) res += loc.x - (mapWidth - 5);
                if(loc.y < 4) res += 4 - loc.y;
                if(loc.y > mapHeight - 5) res += loc.y - (mapHeight - 5);
                return res;
        });
        primaryStrategy = new EmptyStrategy();
        secondaryStrategy = new LinkStrategy(home);
    }

    @Override
    public void runTick() throws GameActionException {
        super.runTick();
        // Profiler.end();
    }

    public void checkerboardAttack(MapLocation loc) throws GameActionException {
        rc.attack(loc, checkerboardPaint(loc) == PaintType.ALLY_SECONDARY);
    }

    public static final int STARTER_STRAT = 0, SRP_STRAT = 1, SCOUT_STRAT = 2, RUSH_STRAT = 3;

    @Override
    public void handleStrategyPacket(StrategyPacket packet, int senderID) throws GameActionException {
        super.handleStrategyPacket(packet, senderID);
        indicate("strategy packet received");
        switch (packet.strategyID) {
            case STARTER_STRAT:
                primaryStrategy = new StarterStrategy();
                break;
            case SRP_STRAT:
                primaryStrategy = new SRPStrategy(decodeLoc(packet.strategyData));
                break;
            case SCOUT_STRAT:
                primaryStrategy = new ScoutStrategy(decodeLoc(packet.strategyData));
                break;
            case RUSH_STRAT:
                primaryStrategy = new RushStrategy();
                break;
            default:
                assert false : "INVALID STRATEGY";
        }
    }
}
