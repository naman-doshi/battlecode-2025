package caterpillow;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import static caterpillow.Game.bot;
import static caterpillow.Game.pm;
import static caterpillow.Game.ticksExisted;
import static caterpillow.Game.time;
import caterpillow.robot.agents.mopper.Mopper;
import caterpillow.robot.agents.soldier.Soldier;
import caterpillow.robot.agents.splasher.Splasher;
import caterpillow.robot.towers.defence.DefenceTower;
import caterpillow.robot.towers.money.MoneyTower;
import caterpillow.robot.towers.paint.PaintTower;
import caterpillow.tracking.CellTracker;
import caterpillow.tracking.RobotTracker;
import caterpillow.tracking.TowerTracker;
import caterpillow.util.Profiler;

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
        Game.preInit();
        RobotTracker.init();
        RobotTracker.updateTick();
        TowerTracker.init();
        CellTracker.init();
        CellTracker.updateTick();

        //TestClass test = new TestClass();

        int score = 0;
        boolean isTrue = true;

 Profiler.begin();

        if (isTrue) score++;

 Profiler.end("method 1");
 Profiler.begin();

        score += (isTrue ? 1 : 0);

 Profiler.end("method 2");

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
