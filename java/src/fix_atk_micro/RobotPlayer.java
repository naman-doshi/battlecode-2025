package fix_atk_micro;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import static fix_atk_micro.Game.bot;
import static fix_atk_micro.Game.pm;
import static fix_atk_micro.Game.ticksExisted;
import static fix_atk_micro.Game.time;
import fix_atk_micro.robot.agents.mopper.Mopper;
import fix_atk_micro.robot.agents.soldier.Soldier;
import fix_atk_micro.robot.agents.splasher.Splasher;
import fix_atk_micro.robot.towers.defence.DefenceTower;
import fix_atk_micro.robot.towers.money.MoneyTower;
import fix_atk_micro.robot.towers.paint.PaintTower;
import fix_atk_micro.tracking.CellTracker;
import fix_atk_micro.tracking.RobotTracker;
import fix_atk_micro.tracking.TowerTracker;
import static fix_atk_micro.util.Util.*;

public class RobotPlayer {

    static boolean test = false;
//    static boolean test = true;

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        if (test) {
            testRun(rc);
        } else {
            actualRun(rc);
        }
    }

    public static void testRun(RobotController rc) throws GameActionException {
        Game.rc = rc;
        Game.preInit();
        RobotTracker.init();
        RobotTracker.updateTick();
        TowerTracker.init();
        CellTracker.init();
        CellTracker.updateTick();

        //TestClass test = new TestClass();

// Profiler.begin();

//         test.test2();

// Profiler.end("method 1");
// Profiler.begin();

//         //TestClass.test1();

// Profiler.end("method 2");

    }

    public static void actualRun(RobotController rc) throws GameActionException {
        time = rc.getRoundNum();
        Game.rc = rc;
        Game.preInit();

        RobotTracker.init();
        RobotTracker.updateTick();
        TowerTracker.init();
        CellTracker.init();
        CellTracker.updateTick();

        switch (rc.getType()) {
            case SOLDIER:
                bot = new Soldier();
                break;
            case MOPPER:
                bot = new Mopper();
                break;
            case SPLASHER:
                bot = new Splasher();
                break;
            case LEVEL_ONE_DEFENSE_TOWER:
            case LEVEL_TWO_DEFENSE_TOWER:
            case LEVEL_THREE_DEFENSE_TOWER:
                bot = new DefenceTower();
                break;
            case LEVEL_ONE_MONEY_TOWER:
            case LEVEL_TWO_MONEY_TOWER:
            case LEVEL_THREE_MONEY_TOWER:
                bot = new MoneyTower();
                break;
            case LEVEL_ONE_PAINT_TOWER:
            case LEVEL_TWO_PAINT_TOWER:
            case LEVEL_THREE_PAINT_TOWER:
                bot = new PaintTower();
                break;
            default:
                assert false : "illegal unit type " + rc.getType().name();
        }

        bot.init();
        Game.postInit();

        while (true) {
            try {
                // Initialiser.upd();
                time = rc.getRoundNum();
                if (ticksExisted > 0) {
                    CellTracker.updateTick();
                    RobotTracker.updateTick();
                }
                Game.upd();
                pm.read();
                TowerTracker.runTick();
                bot.runTick();
                pm.flush();
                ticksExisted++;
                rc.setIndicatorString(indicatorString);
                indicatorString = "";
                Clock.yield();
            } catch (Exception e) {
                e.printStackTrace();
                Clock.yield();
            }
        }
    }
}
