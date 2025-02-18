package caterpillow.robot.towers.defence;

import java.util.ArrayList;
import java.util.List;
import caterpillow.util.CustomRandom;

import battlecode.common.*;
import caterpillow.Game;
import static caterpillow.Game.*;
import caterpillow.robot.towers.RespawnStrategy;
import caterpillow.robot.towers.Tower;
import caterpillow.robot.towers.TowerAttackStrategy;
import caterpillow.robot.towers.TowerStrategy;
import caterpillow.robot.towers.UnstuckStrategy;
import caterpillow.robot.towers.spawner.LoopedSpawner;
import caterpillow.robot.towers.spawner.SpawnerStrategy;
import caterpillow.robot.towers.spawner.splasher.SplasherSpawner;
import static caterpillow.tracking.CellTracker.getNearestCell;
import static caterpillow.tracking.RobotTracker.getNearestRobot;
import static caterpillow.util.Util.*;
import static caterpillow.Config.*;

public class NormalDefenceTowerStrategy extends TowerStrategy {

    // in case we get rushed
    int seed;
    CustomRandom rng;
    Tower bot;
    int nxt;
    TowerAttackStrategy atkstrat;

    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public NormalDefenceTowerStrategy() throws GameActionException {
        bot = (Tower) Game.bot;
        seed = new CustomRandom(rc.getID()).nextInt();
        rng = new CustomRandom(seed);

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

        if (!isCentral(rc.getLocation()) && getNearestCell(c -> c.getPaint().isEnemy()) == null && getNearestRobot(b -> !isFriendly(b)) == null && rc.getChips() > 2000 && Game.time - atkstrat.lastAttack > 30) {
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
