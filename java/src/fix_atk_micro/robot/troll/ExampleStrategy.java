package fix_atk_micro.robot.troll;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import fix_atk_micro.Game;
import fix_atk_micro.robot.agents.UpgradeTowerStrategy;
import fix_atk_micro.robot.agents.WeakRefillStrategy;
import fix_atk_micro.robot.agents.soldier.AttackTowerStrategy;
import fix_atk_micro.robot.agents.soldier.Soldier;
import fix_atk_micro.tracking.RobotTracker;

import static fix_atk_micro.Config.canUpgrade;
import static fix_atk_micro.util.Util.*;
import static fix_atk_micro.Game.*;
import static fix_atk_micro.world.GameStage.MID;

public class ExampleStrategy extends StackableStrategy {

    Soldier bot;
    MapLocation target;

    public ExampleStrategy(MapLocation target) {
        this.bot = (Soldier) Game.bot;
        this.target = target;
    }

    @Override
    public boolean isBaseComplete() {
        return rc.getLocation().distanceSquaredTo(target) < 2;
    }

    @Override
    public void runBaseTick() throws GameActionException {

//        if (isPaintBelowHalf()) {
//            RobotInfo nearest = RobotTracker.getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getPaintAmount() >= missingPaint());
//            if (nearest != null) {
//                if (tryStrategy(new WeakRefillStrategy(nearest.getLocation(), 0.3))) return;
//            }
//        }
//
//        if (gameStage.equals(MID)) {
//            RobotInfo enemyTower = RobotTracker.getNearestRobot(b -> b.getType().isTowerType() && !isFriendly(b));
//            if (enemyTower != null) {
//                if (tryStrategy(new AttackTowerStrategy(enemyTower.getLocation()))) return;
//            }
//        }
//
//        if (gameStage.equals(MID)) {
//            for (int level = 2; level <= 3; level++) {
//                if (canUpgrade(level)) {
//                    int finalLevel = level;
//                    RobotInfo nearest = RobotTracker.getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getType().level == finalLevel - 1);
//                    if (nearest != null) {
//                        if (tryStrategy(new UpgradeTowerStrategy(nearest.getLocation(), level))) return;
//                    }
//                }
//            }
//        }

        bot.pathfinder.makeMove(target);
    }
}
