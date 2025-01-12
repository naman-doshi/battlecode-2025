package caterpillow.robot.agents.soldier;

import battlecode.common.*;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.EmptyStrategy;
import caterpillow.robot.RecursiveStrategy;
import caterpillow.robot.agents.*;
import caterpillow.robot.agents.braindamage.SnipeAndBuildStrategy;
import caterpillow.robot.agents.braindamage.ShitRushStrategy;
import caterpillow.robot.agents.braindamage.SnipeStrategy;

import static caterpillow.util.Util.*;

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

    @Override
    public void handleStrategyPacket(StrategyPacket packet, int senderID) throws GameActionException {
        super.handleStrategyPacket(packet, senderID);
        switch (packet.strategyID) {
            case 0:
                primaryStrategy = new StarterStrategy();
                break;
            case 1:
                println("set strategy to rush");
                primaryStrategy = new RecursiveStrategy(RushStrategy::new);
                break;
            case 2:
                primaryStrategy = new ShitRushStrategy();
                break;
            case 3:
                primaryStrategy = new SnipeStrategy();
                break;
            case 4:
                primaryStrategy = new SnipeAndBuildStrategy();
                break;
        }
    }
}
