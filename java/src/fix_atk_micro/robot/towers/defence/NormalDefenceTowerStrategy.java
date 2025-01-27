package fix_atk_micro.robot.towers.defence;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;
import fix_atk_micro.Game;
import static fix_atk_micro.Game.rc;
import static fix_atk_micro.Game.team;
import fix_atk_micro.robot.towers.RespawnStrategy;
import fix_atk_micro.robot.towers.Tower;
import fix_atk_micro.robot.towers.TowerAttackStrategy;
import fix_atk_micro.robot.towers.TowerStrategy;
import fix_atk_micro.robot.towers.UnstuckStrategy;
import fix_atk_micro.robot.towers.spawner.LoopedSpawner;
import fix_atk_micro.robot.towers.spawner.SpawnerStrategy;
import fix_atk_micro.robot.towers.spawner.splasher.SplasherSpawner;
import static fix_atk_micro.tracking.CellTracker.getNearestCell;
import static fix_atk_micro.tracking.RobotTracker.getNearestRobot;
import static fix_atk_micro.util.Util.indicate;
import static fix_atk_micro.util.Util.isFriendly;

public class NormalDefenceTowerStrategy extends TowerStrategy {

    // in case we get rushed
    int seed;
    Random rng;
    Tower bot;
    int nxt;
    TowerAttackStrategy atkstrat;

    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public NormalDefenceTowerStrategy() throws GameActionException {
        bot = (Tower) Game.bot;
        seed = new Random(rc.getID()).nextInt();
        rng = new Random(seed);

        strats = new ArrayList<>();
        strats.add(new RespawnStrategy());
        strats.add(new UnstuckStrategy());
        strats.add(atkstrat = new TowerAttackStrategy());
        strats.add(new SpawnerStrategy(
                new LoopedSpawner(
                    SplasherSpawner::new
                )
        ));
        nxt = 0;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("NORMAL");

        if (getNearestCell(c -> c.getPaint().isEnemy()) == null && getNearestRobot(b -> !isFriendly(b)) == null && rc.getChips() > 2000 && Game.time - atkstrat.lastAttack > 30) {
            // donate paint to surrounding bots
            RobotInfo[] bots = rc.senseNearbyRobots();
            for (RobotInfo bot : bots) {
                if (bot.getTeam() == team && bot.getType().isRobotType()) {
                    int missing = bot.getType().paintCapacity - bot.getPaintAmount();
                    if (missing > 0) {
                        int amt = Math.min(rc.getPaint(), missing);
                        if (rc.getPaint() == 0) break;
                        if (rc.canTransferPaint(bot.location, amt)) {
                            rc.transferPaint(bot.location, amt);
                        }
                    }
                }
            }
            
            rc.disintegrate();
            return;
        }

        for (TowerStrategy strat : strats) {
            strat.runTick();
        }
    }
}
