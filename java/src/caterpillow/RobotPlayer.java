package caterpillow;

import static caterpillow.Game.*;
import battlecode.common.*;
import caterpillow.packet.PacketManager;
import caterpillow.robot.agents.Mopper;
import caterpillow.robot.agents.Soldier;
import caterpillow.robot.agents.Splasher;
import caterpillow.robot.towers.ChipTower;
import caterpillow.robot.towers.DefenceTower;
import caterpillow.robot.towers.PaintTower;

import static caterpillow.util.Util.*;
import static caterpillow.Game.*;

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
                bot = new DefenceTower();
                break;
            case LEVEL_ONE_MONEY_TOWER:
                bot = new ChipTower();
                break;
            case LEVEL_ONE_PAINT_TOWER:
                bot = new PaintTower();
                break;
            default:
                assert false : "illegal unit type";
        }

        bot.init();
        Game.postInit();

        while (true) {
            Game.upd();
            time = rc.getRoundNum();
            pm.read();
            bot.runTick();
            pm.flush();
            Clock.yield();
        }
    }
}
