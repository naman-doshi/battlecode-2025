package caterpillow;

import battlecode.common.*;
import caterpillow.robot.Robot;
import caterpillow.robot.agents.Mopper;
import caterpillow.robot.agents.Soldier;
import caterpillow.robot.agents.Splasher;
import caterpillow.robot.towers.ChipTower;
import caterpillow.robot.towers.DefenceTower;
import caterpillow.robot.towers.PaintTower;

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

        while (true) {
            Game.time++;



            bot.runTick(rc);
            Clock.yield();
        }
    }
}
