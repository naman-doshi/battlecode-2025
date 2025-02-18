package caterpillow.robot.agents.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import static caterpillow.Game.mapHeight;
import static caterpillow.Game.mapWidth;
import static caterpillow.Game.origin;
import static caterpillow.Game.rc;
import static caterpillow.Game.time;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.LinkStrategy;
import caterpillow.tracking.TowerTracker;

import static caterpillow.util.Util.checkerboardPaint;
import static caterpillow.util.Util.decodeLoc;
import static caterpillow.util.Util.indicate;


public class Soldier extends Agent {
    public boolean syncAttacks; // whether to sync tower attacks (true for early scouts and rushers)

    @Override
    public void init() throws GameActionException {
        super.init();

        pathfinder = new BugnavPathfinder(c -> rc.getHealth() <= 25 && TowerTracker.isCellInDanger(c.getMapLocation()), c -> {
                MapLocation loc = c.getMapLocation();
                int res = 0;
                if(loc.x < 4) res += 4 - loc.x;
                if(loc.x > mapWidth - 5) res += loc.x - (mapWidth - 5);
                if(loc.y < 4) res += 4 - loc.y;
                if(loc.y > mapHeight - 5) res += loc.y - (mapHeight - 5);
                return res;
        });
        primaryStrategy = new EmptyStrategy();
        if(!rc.senseMapInfo(rc.getLocation()).getPaint().isEnemy()) secondaryStrategy = new LinkStrategy(home);
        else secondaryStrategy = null;
        syncAttacks = time < 15;
        noPaintThreshold = 5;
        noPaintTicks = 20;
    }

    @Override
    public void runTick() throws GameActionException {
        if(time < 6 && primaryStrategy instanceof EmptyStrategy) {
            if (home != null) {
                origin = home;
            } else {
                origin = rc.getLocation();
            }
            primaryStrategy = new ScoutStrategy();
        }
        // if(time > 100) syncAttacks = false;
        super.runTick();
        // Profiler.end();
    }

    public void checkerboardAttack(MapLocation loc) throws GameActionException {
        rc.attack(loc, checkerboardPaint(loc) == PaintType.ALLY_SECONDARY);
    }

    public static final int STARTER_STRAT = 0, SRP_STRAT = 1, SCOUT_STRAT = 2, RUSH_STRAT = 3, PAINT_EVERYWHERE_STRAT = 4;

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
                primaryStrategy = new RushStrategy(packet.strategyData);
                break;
            case PAINT_EVERYWHERE_STRAT:
                primaryStrategy = new PaintEverywhereStrategy();
                break;
            default:
                assert false : "INVALID STRATEGY";
        }
    }
}
