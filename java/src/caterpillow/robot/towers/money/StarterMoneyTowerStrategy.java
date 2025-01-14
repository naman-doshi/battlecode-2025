package caterpillow.robot.towers.money;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.UnitType;
import caterpillow.Game;
import static caterpillow.Game.gameStage;
import static caterpillow.Game.rc;
import static caterpillow.Game.seed;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.robot.towers.RespawnStrategy;
import caterpillow.robot.towers.Tower;
import caterpillow.robot.towers.TowerAttackStrategy;
import caterpillow.robot.towers.TowerStrategy;
import static caterpillow.util.Util.getSafeSpawnLoc;
import static caterpillow.util.Util.indicate;

public class StarterMoneyTowerStrategy extends TowerStrategy {

    // in case we get rushed
    int todo;
    Random rng;
    Tower bot;

    // we can *maybe* turn this into a special class if it gets too repetitive
    // ill just hardcode for now to make sure it works
    List<TowerStrategy> strats;

    public StarterMoneyTowerStrategy() {
        todo = 2;
        bot = (Tower) Game.bot;
        rng = new Random(seed);
        strats = new ArrayList<>();
        strats.add(new RespawnStrategy());
        strats.add(new TowerAttackStrategy());
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("STARTER");
        for (TowerStrategy strat : strats) {
            strat.runTick();
        }
        switch (gameStage) {
            // TODO: implement strategies for all game times
            // currently just spam soldiers
            case LATE:
            case MID:
            case EARLY:
                if (rc.getPaint() >= UnitType.SOLDIER.paintCost + UnitType.MOPPER.paintCost && rc.getChips() >= UnitType.SOLDIER.moneyCost + UnitType.MOPPER.moneyCost) {
                    todo += 1;
                }
                if (todo > 0) {
                    // just spawn adjacent
                    MapInfo spawn = getSafeSpawnLoc(UnitType.SOLDIER);
                    if (spawn != null && rc.canBuildRobot(UnitType.SOLDIER, spawn.getMapLocation())) {
                        bot.build(UnitType.SOLDIER, spawn.getMapLocation(), new SeedPacket(rng.nextInt()), new StrategyPacket(1));
                        todo--;
                        return;
                    }
                }
                break;
        }
    }
}
