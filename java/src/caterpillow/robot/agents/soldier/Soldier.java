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
import caterpillow.robot.agents.braindamage.SnipeAndBuildStrategy;
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
        if (rc.canAttack(loc)) {
            rc.attack(loc, checkerboardPaint(loc)==PaintType.ALLY_SECONDARY);
        }
    }

    @Override
    public void handleStrategyPacket(StrategyPacket packet, int senderID) throws GameActionException {
        super.handleStrategyPacket(packet, senderID);
        switch (packet.strategyID) {
            case 0:
                // init to root
                primaryStrategy = new StarterStrategy(rc.senseRobotAtLocation(home).getType());
                break;
            case 1:
                primaryStrategy = new SRPStrategy();
                break;
            case 2:
                primaryStrategy = new ScoutStrategy();
                break;
            case 3:
                primaryStrategy = new SnipeAndBuildStrategy();
                break;
        }
    }
}
