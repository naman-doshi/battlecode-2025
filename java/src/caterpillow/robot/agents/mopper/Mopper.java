package caterpillow.robot.agents.mopper;

import java.util.List;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.pathfinding.BugnavPathfinder;
import caterpillow.robot.agents.Agent;
import caterpillow.util.GamePredicate;
import static caterpillow.util.Util.getBestRobot;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.orthDirections;

public class Mopper extends Agent {

    Mopper bot;
    List<MapLocation> enemyLocs;
    MapLocation spawnLoc;

    public RobotInfo getBestTarget(GamePredicate<RobotInfo> pred) throws GameActionException {
        return getBestRobot((a, b) -> {
            int a1 = a.getType().ordinal();
            int b1 = b.getType().ordinal();
            int h1 = a.getPaintAmount();
            int h2 = b.getPaintAmount();
            if (a1 == b1) {
                if (h1 > h2) return b;
                else return a;
            } else {
                if (a1 < b1) return a;
                else return b;
            }
        }, e -> !isFriendly(e) && e.getType().isRobotType() && pred.test(e));
    }

    public RobotInfo getBestTarget() throws GameActionException {
        return getBestTarget(e -> true);
    }

    public void doBestAttack() throws GameActionException {
        if (!rc.isActionReady()) {
            return;
        }
        // try mop sweep
        for (Direction dir : orthDirections) {
            if (!rc.canMopSwing(dir)) {
                continue;
            }
            // stupid code
            MapLocation mainLoc = rc.getLocation().add(dir);
            int cnt = 0;
            for (Direction dir2 : directions) {
                MapLocation loc = rc.getLocation().add(dir2);
                if (mainLoc.distanceSquaredTo(loc) <= 1) {
                    cnt++;
                }
            }
            if (cnt == 3) {
                // just do the mop sweep
                // if there are >= 5 enemies around us its a lost cause anyways
                rc.mopSwing(dir);
                return;
            }
        }

        // normal attack
        RobotInfo target = getBestTarget(e -> rc.canAttack(e.getLocation()));
        if (target != null) {
            rc.attack(target.getLocation());
        }
    }

    @Override
    public void init() throws GameActionException {
        super.init();

        pathfinder = new BugnavPathfinder();
        primaryStrategy = new MopperOffenceStrategy();
        bot = (Mopper) Game.bot;
    }

    @Override
    public void handleStrategyPacket(StrategyPacket packet, int senderID) throws GameActionException {
        super.handleStrategyPacket(packet, senderID);
        switch (packet.strategyID) {
            case 0:
                primaryStrategy = new MopperDefenceStrategy();
                break;
            case 1:
                primaryStrategy = new MopperOffenceStrategy();
                System.out.println("set strategy to offence");
                break;
            case 2:
                // TODO: assign a primary strategy
                secondaryStrategy = new MopperRespawnStrategy();
                break;
        }
    }
}
