package caterpillow.robot.towers;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.UnitType;
import caterpillow.Game;

import static caterpillow.Game.*;
import static caterpillow.util.Util.*;

import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.agents.mopper.Mopper;

public class UnstuckStrategy extends TowerStrategy {

    Tower bot;

    public UnstuckStrategy() {
        bot = (Tower) Game.bot;
    }

    @Override
    public void runTick() throws GameActionException {
        if (countNearbyMoppers(rc.getLocation()) == 0) {
            MapInfo nearest = getClosestNeighbourTo(centre, c -> !c.getPaint().isEnemy() && c.getMapLocation().distanceSquaredTo(rc.getLocation()) == 1);
            if (nearest == null) {
                MapLocation loc = getClosestNeighbourTo(centre, c -> true).getMapLocation();
                if (rc.canBuildRobot(UnitType.MOPPER, loc)) {
                    bot.build(UnitType.MOPPER, loc);
                }
            }
        }
    }
}
