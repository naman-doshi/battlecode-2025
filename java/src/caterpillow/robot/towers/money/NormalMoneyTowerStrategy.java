package caterpillow.robot.towers.money;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.*;
import caterpillow.Game;
import static caterpillow.Game.rc;

import caterpillow.robot.towers.*;
import caterpillow.robot.towers.spawner.LoopedSpawner;
import caterpillow.robot.towers.spawner.OffenceMopperSpawner;
import caterpillow.robot.towers.spawner.PassiveMopperSpawner;
import caterpillow.robot.towers.spawner.SRPSpawner;
import caterpillow.robot.towers.spawner.SpawnerStrategy;
import caterpillow.robot.towers.spawner.SplasherSRPSpawner;
import static caterpillow.util.Util.*;
import static caterpillow.Config.*;

public class NormalMoneyTowerStrategy extends TowerStrategy {
    final boolean[][] paintTowerPattern = {
        {true, false, false, false, true},
        {false, true, false, true, false},
        {false, false, true, false, false},
        {false, true, false, true, false},
        {true, false, false, false, true},
    };

    // in case we get rushed
    int seed;
    Random rng;
    Tower bot;
    int nxt;
    boolean anyAlly = false;

    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public NormalMoneyTowerStrategy() throws GameActionException {
        bot = (Tower) Game.bot;
        seed = new Random(rc.getID()).nextInt();
        rng = new Random(seed);

        strats = new ArrayList<>();
        strats.add(new UnstuckStrategy());
        strats.add(new TowerAttackStrategy());
        strats.add(new SpawnerStrategy(
                //new ScoutSpawner(),
                new SRPSpawner(),
                new LoopedSpawner(
                        SplasherSRPSpawner::new,
                        OffenceMopperSpawner::new,
                        SplasherSRPSpawner::new,
                        PassiveMopperSpawner::new,
                        SplasherSRPSpawner::new
                )
        ));
        nxt = 0;
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("NORMAL");
        for (TowerStrategy strat : strats) {
            strat.runTick();
        }
        if(shouldConvertMoneyToPaint()) {
            boolean convertToPaintTower = true;
            MapLocation loc = rc.getLocation();
            for(int x = loc.x - 2; x <= loc.x + 2; x++) {
                for(int y = loc.y - 2; y <= loc.y + 2; y++) {
                    if(x == loc.x && y == loc.y) continue;
                    PaintType paint = rc.senseMapInfo(new MapLocation(x, y)).getPaint();
                    if(paint.isAlly() && paint.equals(PaintType.ALLY_SECONDARY) == paintTowerPattern[x - loc.x + 2][y - loc.y + 2]) {
                        continue;
                    }
                    convertToPaintTower = false;
                    break;
                }
                if(!convertToPaintTower) break;
            }
            if(convertToPaintTower) {
                System.out.println("converting to paint tower at " + loc.toString());
                rc.disintegrate();
            }
        }
    }
}
