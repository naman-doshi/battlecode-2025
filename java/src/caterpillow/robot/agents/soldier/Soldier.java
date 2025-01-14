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

public class Soldier extends Agent {

    boolean getColour(MapLocation loc) {
        return false;
    }

    @Override
    public void init() throws GameActionException {
        super.init();

        pathfinder = new BugnavPathfinder();
        primaryStrategy = new EmptyStrategy();
        secondaryStrategy = new LinkStrategy(home);
    }

    @Override
    public void runTick() throws GameActionException {
        super.runTick();
    }

    public void checkerboardAttack(MapLocation loc) throws GameActionException {
        rc.attack(loc, checkerboardPaint(loc) == PaintType.ALLY_SECONDARY);
    }

    public static final int STARTER_STRAT = 0, SRP_STRAT = 1, SCOUT_STRAT = 2;

    @Override
    public void handleStrategyPacket(StrategyPacket packet, int senderID) throws GameActionException {
        super.handleStrategyPacket(packet, senderID);
        switch (packet.strategyID) {
            case STARTER_STRAT:
                // init to root
                primaryStrategy = new StarterStrategy();
                break;
            case SRP_STRAT:
                primaryStrategy = new SRPStrategy();
                break;
            case SCOUT_STRAT:
                primaryStrategy = new ScoutStrategy();
                break;
            default:
                assert false : "INVALID STRATEGY";
        }
    }
}
