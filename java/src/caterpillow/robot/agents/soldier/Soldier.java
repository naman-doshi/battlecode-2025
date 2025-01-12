package caterpillow.robot.agents.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;

import static caterpillow.Game.origin;
import static caterpillow.Game.rc;

import battlecode.common.UnitType;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.RecursiveStrategy;
import caterpillow.robot.agents.Agent;
import caterpillow.robot.agents.LinkStrategy;
import caterpillow.robot.agents.braindamage.ShitRushStrategy;
import caterpillow.robot.agents.braindamage.SnipeAndBuildStrategy;
import caterpillow.robot.agents.braindamage.SnipeStrategy;
import static caterpillow.util.Util.checkerboardPaint;
import static caterpillow.util.Util.println;

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
                primaryStrategy = new StarterStrategy(rc.senseRobotAtLocation(origin).getType());
                break;
            case 1:
                primaryStrategy = new SRPStrategy();
                break;
            case 2:
                primaryStrategy = new ScoutStrategy(UnitType.LEVEL_ONE_PAINT_TOWER);
                break;
            case 3:
                primaryStrategy = new ScoutStrategy(UnitType.LEVEL_ONE_MONEY_TOWER);
                break;
            case 4:
                primaryStrategy = new SnipeAndBuildStrategy();
                break;
        }
    }
}
