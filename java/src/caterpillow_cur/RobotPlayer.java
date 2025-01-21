package caterpillow_cur;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import static caterpillow_cur.Game.bot;
import static caterpillow_cur.Game.pm;
import static caterpillow_cur.Game.ticksExisted;
import static caterpillow_cur.Game.time;
import caterpillow_cur.robot.agents.mopper.Mopper;
import caterpillow_cur.robot.agents.soldier.Soldier;
import caterpillow_cur.robot.agents.splasher.Splasher;
import caterpillow_cur.robot.towers.defence.DefenceTower;
import caterpillow_cur.robot.towers.money.MoneyTower;
import caterpillow_cur.robot.towers.paint.PaintTower;
import caterpillow_cur.tracking.CellTracker;
import caterpillow_cur.tracking.RobotTracker;
import caterpillow_cur.tracking.TowerTracker;
import caterpillow_cur.util.*;
import static caterpillow_cur.util.Util.*;
import caterpillow_cur.robot.agents.splasher.SplasherAggroStrategy;

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
            try {
                if(bot instanceof Splasher && ticksExisted > 3) Profiler.begin();
                // Initialiser.upd();
                time = rc.getRoundNum();
                CellTracker.updateTick();
                RobotTracker.updateTick();
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
