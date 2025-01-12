package caterpillow.robot.agents.soldier;

import battlecode.common.*;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.EmptyStrategy;
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

        // bruh i can just get the tower to check
//        RobotInfo nearest = getNearestRobot(bot -> bot.getType().isTowerType());
//        assert nearest != null;
//        assert rc.senseMapInfo(rc.getLocation()).getPaint().isAlly();
//        pm.send(nearest.getID(), new AdoptionPacket(rc.getID()));

        // by default, make sure u can talk to ur tower
//        MapInfo info = rc.senseMapInfo(rc.getLocation());
//        if (!info.getPaint().isAlly()) {
//            assert rc.canAttack(rc.getLocation());
//            // TODO: make this match the pattern to build towers
//            rc.attack(rc.getLocation(), getColour(rc.getLocation()));
//        }

        pathfinder = new BugnavPathfinder();
        primaryStrategy = new EmptyStrategy();
        secondaryStrategy = new LinkStrategy(home);
    }

    @Override
    public void runTick() throws GameActionException {
        super.runTick();
    }

    @Override
    public void handleStrategyPacket(StrategyPacket packet, int senderID) {
        super.handleStrategyPacket(packet, senderID);
        switch (packet.strategyID) {
            case 0:
                primaryStrategy = new WanderStrategy();
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
