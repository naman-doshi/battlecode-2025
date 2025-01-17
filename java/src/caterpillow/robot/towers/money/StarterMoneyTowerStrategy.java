package caterpillow.robot.towers.money;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import static caterpillow.Game.seed;
import caterpillow.robot.towers.RespawnStrategy;
import caterpillow.robot.towers.Tower;
import caterpillow.robot.towers.TowerAttackStrategy;
import caterpillow.robot.towers.TowerStrategy;
import caterpillow.robot.towers.spawner.LoopedSpawner;
import caterpillow.robot.towers.spawner.OffenceMopperSpawner;
import caterpillow.robot.towers.spawner.RushSpawner;
import caterpillow.robot.towers.spawner.SRPSpawner;
import caterpillow.robot.towers.spawner.ScoutSpawner;
import caterpillow.robot.towers.spawner.SpawnerStrategy;
import caterpillow.robot.towers.spawner.SplasherSRPSpawner;
import static caterpillow.util.Util.expectedRushDistance;
import static caterpillow.util.Util.getNearestCell;
import static caterpillow.util.Util.indicate;

public class StarterMoneyTowerStrategy extends TowerStrategy {

    // in case we get rushed
    int todo;
    Random rng;
    Tower bot;
    boolean anyAlly = false;

    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public StarterMoneyTowerStrategy() throws GameActionException {
        todo = 2;
        bot = (Tower) Game.bot;
        rng = new Random(seed);
        strats = new ArrayList<>();
        strats.add(new RespawnStrategy());
        strats.add(new TowerAttackStrategy());

        // only rush if map is small, or short dist to enemy
        // we're rushing from money tower to cripple their finances (as the corresponding enemy tower is a money tower)
        // also why not spawn a mopper since we don't have enough paint for anything else anyway
        if (Game.rc.getMapWidth() * Game.rc.getMapHeight() < 1000 || expectedRushDistance(Game.rc.getLocation()) < 20) {
            strats.add(new SpawnerStrategy(
                new RushSpawner(),
                new RushSpawner(),
                new LoopedSpawner(
                        new SRPSpawner(),
                        new SplasherSRPSpawner(),
                        new OffenceMopperSpawner()
                )
            ));
        } else {
            strats.add(new SpawnerStrategy(
                new ScoutSpawner(),
                new ScoutSpawner(),
                new LoopedSpawner(
                        new SRPSpawner(),
                        new SplasherSRPSpawner(),
                        new OffenceMopperSpawner()
                )
            ));
        }

    }

    @Override
    public void runTick() throws GameActionException {
        indicate("STARTER");

        // double check if im surrounded by paint lol
        
        for (MapInfo info : Game.rc.senseNearbyMapInfos(4)) {
            if (!info.getPaint().isEnemy() && !info.hasRuin()) {
                //System.out.println("type " + info.getPaint() + " loc " + info.getMapLocation() + " my loc " + Game.rc.getLocation());
                anyAlly = true;
                break;
            }
        }
        //System.out.println("ally " + anyAlly);

        if (!anyAlly) {
            MapInfo info = getNearestCell(c -> Game.rc.canBuildRobot(UnitType.MOPPER, c.getMapLocation()));
            if (info != null) {
                bot.build(UnitType.MOPPER, info.getMapLocation());
                anyAlly = true;
            }
        } 
        
        
        for (TowerStrategy strat : strats) {
            strat.runTick();
        }
    }
}
