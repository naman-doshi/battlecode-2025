package caterpillow.robot.towers.paint;

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
import static caterpillow.util.Util.getNearestCell;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.expectedRushDistance;

public class StarterPaintTowerStrategy extends TowerStrategy {

    List<TowerStrategy> strats;
    // in case we get rushed
    int todo;
    Tower bot;
    Random rng;
    boolean anyAlly = false;
    public StarterPaintTowerStrategy() throws GameActionException {
        todo = 1;
        bot = (Tower) Game.bot;
        rng = new Random(seed);
        strats = new ArrayList<>();
        strats.add(new RespawnStrategy());
        strats.add(new TowerAttackStrategy());
        int size = Game.rc.getMapWidth() * Game.rc.getMapHeight();
        int expectedDistance = expectedRushDistance(Game.rc.getLocation());
        if (size < 900 || expectedDistance < 15) {
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

        if (Game.time > 10) {
            strats = new ArrayList<>();
            strats.add(new RespawnStrategy());
            strats.add(new TowerAttackStrategy());
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
        
        
        for (TowerStrategy strat : strats) {
            strat.runTick();
        }

    }
}
