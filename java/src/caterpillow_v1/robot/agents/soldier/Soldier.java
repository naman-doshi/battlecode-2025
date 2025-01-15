package caterpillow_v1.robot.agents.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;

import static caterpillow_v1.Game.rc;
import caterpillow_v1.packet.packets.StrategyPacket;
import caterpillow_v1.pathfinding.BugnavPathfinder;
import caterpillow_v1.robot.EmptyStrategy;
import caterpillow_v1.robot.agents.Agent;
import caterpillow_v1.robot.agents.LinkStrategy;

import static caterpillow_v1.util.Util.checkerboardPaint;

public class Soldier extends Agent {

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
