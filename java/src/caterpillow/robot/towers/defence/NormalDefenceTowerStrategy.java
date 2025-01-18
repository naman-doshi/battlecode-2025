package caterpillow.robot.towers.defence;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.util.Util.*;

import caterpillow.robot.towers.*;
import caterpillow.robot.towers.spawner.LoopedSpawner;
import caterpillow.robot.towers.spawner.mopper.OffenceMopperSpawner;
import caterpillow.robot.towers.spawner.SpawnerStrategy;

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
        strats.add(new UnstuckStrategy());
        strats.add(atkstrat = new TowerAttackStrategy());
        strats.add(new SpawnerStrategy(
                new LoopedSpawner(
                    OffenceMopperSpawner::new
                )
        ));
        nxt = 0;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("NORMAL");

        if (getNearestCell(c -> c.getPaint().isEnemy()) == null && getNearestRobot(b -> !isFriendly(b)) == null && rc.getChips() > 2000 && Game.time - atkstrat.lastAttack > 60) {
            rc.disintegrate();
            return;
        }

        for (TowerStrategy strat : strats) {
            strat.runTick();
        }
    }
}
