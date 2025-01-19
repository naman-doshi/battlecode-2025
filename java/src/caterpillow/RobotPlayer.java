package caterpillow;

import static caterpillow.Game.*;

import battlecode.common.*;
import caterpillow.robot.agents.mopper.Mopper;
import caterpillow.robot.agents.soldier.Soldier;
import caterpillow.robot.agents.splasher.Splasher;
import caterpillow.robot.towers.money.MoneyTower;
import caterpillow.robot.towers.defence.DefenceTower;
import caterpillow.robot.towers.paint.PaintTower;
import caterpillow.tracking.CellTracker;
import caterpillow.tracking.RobotTracker;
import caterpillow.util.Profiler;
import caterpillow.tracking.TowerTracker;
import caterpillow.util.*;
import static caterpillow.util.Util.*;

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
        Profiler.begin();
        CellTracker.init();
        Profiler.end("init celltracker cost");
        Profiler.begin();
        CellTracker.updateTick();
        Profiler.end("update cost");
        Profiler.begin();
        CellTracker.getNearestCell(c -> false);
        Profiler.end("util nearest cell cost");
        Profiler.begin();
        CellTracker.getNearestCell(c -> false);
        Profiler.end("tracker nearest cell cost");
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
//            Initialiser.upd();
            time = rc.getRoundNum();
            Game.upd();
            pm.read();
            TowerTracker.runTick();
            bot.runTick();
            pm.flush();
            ticksExisted++;
            rc.setIndicatorString(indicatorString);
            indicatorString = "";
            Clock.yield();
            CellTracker.updateTick();
            RobotTracker.updateTick();
        }
    }
}
