package caterpillow;

import static caterpillow.Game.*;
import static caterpillow.util.Util.println;

import battlecode.common.*;
import caterpillow.robot.agents.mopper.Mopper;
import caterpillow.robot.agents.soldier.Soldier;
import caterpillow.robot.agents.splasher.Splasher;
import caterpillow.robot.towers.money.MoneyTower;
import caterpillow.robot.towers.defence.DefenceTower;
import caterpillow.robot.towers.paint.PaintTower;
import caterpillow.util.Initialiser;
import caterpillow.util.TowerTracker;

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
            Initialiser.upd();
            Game.upd();
            time = rc.getRoundNum();
            pm.read();
            TowerTracker.runTick();
            println("pre bot tick " + Clock.getBytecodeNum());
            bot.runTick();
            pm.flush();
            ticksExisted++;
            Clock.yield();
        }
    }
}
