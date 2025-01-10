package lmkaepillow;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import lmkaepillow.robot.Robot;
import lmkaepillow.robot.agents.Mopper;
import lmkaepillow.robot.agents.Soldier;
import lmkaepillow.robot.agents.Splasher;
import lmkaepillow.robot.towers.ChipTower;
import lmkaepillow.robot.towers.DefenceTower;
import lmkaepillow.robot.towers.PaintTower;

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
