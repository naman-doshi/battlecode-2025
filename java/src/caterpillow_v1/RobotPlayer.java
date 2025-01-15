package caterpillow_v1;

import static caterpillow_v1.Game.*;
import battlecode.common.*;
import caterpillow_v1.robot.agents.mopper.Mopper;
import caterpillow_v1.robot.agents.soldier.Soldier;
import caterpillow_v1.robot.agents.splasher.Splasher;
import caterpillow_v1.robot.towers.money.MoneyTower;
import caterpillow_v1.robot.towers.defence.DefenceTower;
import caterpillow_v1.robot.towers.paint.PaintTower;
import caterpillow_v1.util.TowerTracker;

public class RobotPlayer {
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        time = rc.getRoundNum();
        Game.rc = rc;
        Game.preInit();

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
            Game.upd();
            time = rc.getRoundNum();
            pm.read();
            TowerTracker.runTick();
            bot.runTick();
            pm.flush();
            ticksExisted++;
            Clock.yield();
        }
    }
}
