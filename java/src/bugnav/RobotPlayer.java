package bugnav;

import battlecode.common.*;
import bugnav.robot.Robot;
import bugnav.robot.agents.Mopper;
import bugnav.robot.agents.Soldier;
import bugnav.robot.agents.Splasher;
import bugnav.robot.towers.ChipTower;
import bugnav.robot.towers.DefenceTower;
import bugnav.robot.towers.PaintTower;
import bugnav.Util;

import java.util.Random;

public class RobotPlayer {
    static Robot bot;

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

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

        bot.init(rc);
        Util.rng = new Random(rc.getID());

        while (true) {
            Game.time++;



            bot.runTick();
            Clock.yield();
        }
    }
}
