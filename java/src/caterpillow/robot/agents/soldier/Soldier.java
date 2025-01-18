package caterpillow.robot.agents.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import static caterpillow.Game.rc;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.LinkStrategy;
import static caterpillow.util.Util.checkerboardPaint;
import static caterpillow.util.Util.isInDanger;

public class Soldier extends Agent {

    @Override
    public void init() throws GameActionException {
        super.init();

        pathfinder = new BugnavPathfinder(c -> rc.getHealth() <= 25 && isInDanger(c.getMapLocation()));
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
        switch (packet.strategyID) {
            case STARTER_STRAT:
                primaryStrategy = new StarterStrategy();
                break;
            case SRP_STRAT:
                primaryStrategy = new SRPStrategy(new MapLocation(packet.strategyData / 64, packet.strategyData % 64));
                break;
            case SCOUT_STRAT:
                primaryStrategy = new ScoutStrategy(new MapLocation(packet.strategyData / 64, packet.strategyData % 64));
                break;
            case RUSH_STRAT:
                primaryStrategy = new RushStrategy();
                break;
            default:
                assert false : "INVALID STRATEGY";
        }
    }
}
