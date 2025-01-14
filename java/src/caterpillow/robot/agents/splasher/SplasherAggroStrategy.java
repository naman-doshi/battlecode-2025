package caterpillow.robot.agents.splasher;

import java.util.List;
import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.util.GameSupplier;
import caterpillow.util.TowerTracker;
import static caterpillow.util.Util.getNearestRobot;
import static caterpillow.util.Util.guessEnemyLocs;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.isPaintBelowHalf;
import static caterpillow.util.Util.missingPaint;
import static caterpillow.util.Util.project;
import static caterpillow.util.Util.subtract;

public class SplasherAggroStrategy extends Strategy {

    public Splasher bot;

    // enemyLocs is more like "POI locs"
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;
    public List<GameSupplier<MapInfo>> suppliers;
    WeakRefillStrategy refillStrategy;

    public SplasherAggroStrategy() throws GameActionException {
        bot = (Splasher) Game.bot;
        this.enemyLocs = guessEnemyLocs(bot.home);
        this.enemy = enemyLocs.get(0);
        enemyLocs.addLast(bot.home);

        
    }

    public void safeMove(MapLocation loc) throws GameActionException {
        // conserve bytecode
        Direction[] dirs = Direction.values();
        Direction dir = rc.getLocation().directionTo(loc);
        MapLocation current = rc.getLocation();
        if (dir == Direction.NORTH) {
            if (rc.canMove(Direction.NORTH) && rc.canSenseLocation(current.add(Direction.NORTH)) && !rc.senseMapInfo(current.add(Direction.NORTH)).getPaint().isEnemy()) {
                rc.move(Direction.NORTH);
                return;
            } else if (rc.canMove(Direction.NORTHWEST) && rc.canSenseLocation(current.add(Direction.NORTHWEST)) && !rc.senseMapInfo(current.add(Direction.NORTHWEST)).getPaint().isEnemy()) {
                rc.move(Direction.NORTHWEST);
                return;
            } else if (rc.canMove(Direction.NORTHEAST) && rc.canSenseLocation(current.add(Direction.NORTHEAST)) && !rc.senseMapInfo(current.add(Direction.NORTHEAST)).getPaint().isEnemy()) {
                rc.move(Direction.NORTHEAST);
                return;
            } else if (rc.canMove(Direction.EAST) && rc.canSenseLocation(current.add(Direction.EAST)) && !rc.senseMapInfo(current.add(Direction.EAST)).getPaint().isEnemy()) {
                rc.move(Direction.EAST);
                return;
            } else if (rc.canMove(Direction.WEST) && rc.canSenseLocation(current.add(Direction.WEST)) && !rc.senseMapInfo(current.add(Direction.WEST)).getPaint().isEnemy()) {
                rc.move(Direction.WEST);
                return;
            }
        } else if (dir == Direction.SOUTH) {
            if (rc.canMove(Direction.SOUTH) && rc.canSenseLocation(current.add(Direction.SOUTH)) && !rc.senseMapInfo(current.add(Direction.SOUTH)).getPaint().isEnemy()) {
                rc.move(Direction.SOUTH);
                return;
            } else if (rc.canMove(Direction.SOUTHWEST) && rc.canSenseLocation(current.add(Direction.SOUTHWEST)) && !rc.senseMapInfo(current.add(Direction.SOUTHWEST)).getPaint().isEnemy()) {
                rc.move(Direction.SOUTHWEST);
                return;
            } else if (rc.canMove(Direction.SOUTHEAST) && rc.canSenseLocation(current.add(Direction.SOUTHEAST)) && !rc.senseMapInfo(current.add(Direction.SOUTHEAST)).getPaint().isEnemy()) {
                rc.move(Direction.SOUTHEAST);
                return;
            } else if (rc.canMove(Direction.EAST) && rc.canSenseLocation(current.add(Direction.EAST)) && !rc.senseMapInfo(current.add(Direction.EAST)).getPaint().isEnemy()) {
                rc.move(Direction.EAST);
                return;
            } else if (rc.canMove(Direction.WEST) && rc.canSenseLocation(current.add(Direction.WEST)) && !rc.senseMapInfo(current.add(Direction.WEST)).getPaint().isEnemy()) {
                rc.move(Direction.WEST);
                return;
            }
        } else if (dir == Direction.EAST) {
            if (rc.canMove(Direction.EAST) && rc.canSenseLocation(current.add(Direction.EAST)) && !rc.senseMapInfo(current.add(Direction.EAST)).getPaint().isEnemy()) {
                rc.move(Direction.EAST);
                return;
            } else if (rc.canMove(Direction.NORTHEAST) && rc.canSenseLocation(current.add(Direction.NORTHEAST)) && !rc.senseMapInfo(current.add(Direction.NORTHEAST)).getPaint().isEnemy()) {
                rc.move(Direction.NORTHEAST);
                return;
            } else if (rc.canMove(Direction.SOUTHEAST) && rc.canSenseLocation(current.add(Direction.SOUTHEAST)) && !rc.senseMapInfo(current.add(Direction.SOUTHEAST)).getPaint().isEnemy()) {
                rc.move(Direction.SOUTHEAST);
                return;
            } else if (rc.canMove(Direction.NORTH) && rc.canSenseLocation(current.add(Direction.NORTH)) && !rc.senseMapInfo(current.add(Direction.NORTH)).getPaint().isEnemy()) {
                rc.move(Direction.NORTH);
                return;
            } else if (rc.canMove(Direction.SOUTH) && rc.canSenseLocation(current.add(Direction.SOUTH)) && !rc.senseMapInfo(current.add(Direction.SOUTH)).getPaint().isEnemy()) {
                rc.move(Direction.SOUTH);
                return;
            }
        } else if (dir == Direction.WEST) {
            if (rc.canMove(Direction.WEST) && rc.canSenseLocation(current.add(Direction.WEST)) && !rc.senseMapInfo(current.add(Direction.WEST)).getPaint().isEnemy()) {
                rc.move(Direction.WEST);
                return;
            } else if (rc.canMove(Direction.NORTHWEST) && rc.canSenseLocation(current.add(Direction.NORTHWEST)) && !rc.senseMapInfo(current.add(Direction.NORTHWEST)).getPaint().isEnemy()) {
                rc.move(Direction.NORTHWEST);
                return;
            } else if (rc.canMove(Direction.SOUTHWEST) && rc.canSenseLocation(current.add(Direction.SOUTHWEST)) && !rc.senseMapInfo(current.add(Direction.SOUTHWEST)).getPaint().isEnemy()) {
                rc.move(Direction.SOUTHWEST);
                return;
            } else if (rc.canMove(Direction.NORTH) && rc.canSenseLocation(current.add(Direction.NORTH)) && !rc.senseMapInfo(current.add(Direction.NORTH)).getPaint().isEnemy()) {
                rc.move(Direction.NORTH);
                return;
            } else if (rc.canMove(Direction.SOUTH) && rc.canSenseLocation(current.add(Direction.SOUTH)) && !rc.senseMapInfo(current.add(Direction.SOUTH)).getPaint().isEnemy()) {
                rc.move(Direction.SOUTH);
                return;
            }
        } else if (dir == Direction.NORTHWEST) {
            if (rc.canMove(Direction.NORTHWEST) && rc.canSenseLocation(current.add(Direction.NORTHWEST)) && !rc.senseMapInfo(current.add(Direction.NORTHWEST)).getPaint().isEnemy()) {
                rc.move(Direction.NORTHWEST);
                return;
            } else if (rc.canMove(Direction.NORTH) && rc.canSenseLocation(current.add(Direction.NORTH)) && !rc.senseMapInfo(current.add(Direction.NORTH)).getPaint().isEnemy()) {
                rc.move(Direction.NORTH);
                return;
            } else if (rc.canMove(Direction.WEST) && rc.canSenseLocation(current.add(Direction.WEST)) && !rc.senseMapInfo(current.add(Direction.WEST)).getPaint().isEnemy()) {
                rc.move(Direction.WEST);
                return;
            } else if (rc.canMove(Direction.SOUTHWEST) && rc.canSenseLocation(current.add(Direction.SOUTHWEST)) && !rc.senseMapInfo(current.add(Direction.SOUTHWEST)).getPaint().isEnemy()) {
                rc.move(Direction.SOUTHWEST);
                return;
            } else if (rc.canMove(Direction.NORTHEAST) && rc.canSenseLocation(current.add(Direction.NORTHEAST)) && !rc.senseMapInfo(current.add(Direction.NORTHEAST)).getPaint().isEnemy()) {
                rc.move(Direction.NORTHEAST);
                return;
            }
        } else if (dir == Direction.NORTHEAST) {
            if (rc.canMove(Direction.NORTHEAST) && rc.canSenseLocation(current.add(Direction.NORTHEAST)) && !rc.senseMapInfo(current.add(Direction.NORTHEAST)).getPaint().isEnemy()) {
                rc.move(Direction.NORTHEAST);
                return;
            } else if (rc.canMove(Direction.NORTH) && rc.canSenseLocation(current.add(Direction.NORTH)) && !rc.senseMapInfo(current.add(Direction.NORTH)).getPaint().isEnemy()) {
                rc.move(Direction.NORTH);
                return;
            } else if (rc.canMove(Direction.EAST) && rc.canSenseLocation(current.add(Direction.EAST)) && !rc.senseMapInfo(current.add(Direction.EAST)).getPaint().isEnemy()) {
                rc.move(Direction.EAST);
                return;
            } else if (rc.canMove(Direction.NORTHWEST) && rc.canSenseLocation(current.add(Direction.NORTHWEST)) && !rc.senseMapInfo(current.add(Direction.NORTHWEST)).getPaint().isEnemy()) {
                rc.move(Direction.NORTHWEST);
                return;
            } else if (rc.canMove(Direction.SOUTHEAST) && rc.canSenseLocation(current.add(Direction.SOUTHEAST)) && !rc.senseMapInfo(current.add(Direction.SOUTHEAST)).getPaint().isEnemy()) {
                rc.move(Direction.SOUTHEAST);
                return;
            }
        } else if (dir == Direction.SOUTHWEST) {
            if (rc.canMove(Direction.SOUTHWEST) && rc.canSenseLocation(current.add(Direction.SOUTHWEST)) && !rc.senseMapInfo(current.add(Direction.SOUTHWEST)).getPaint().isEnemy()) {
                rc.move(Direction.SOUTHWEST);
                return;
            } else if (rc.canMove(Direction.SOUTH) && rc.canSenseLocation(current.add(Direction.SOUTH)) && !rc.senseMapInfo(current.add(Direction.SOUTH)).getPaint().isEnemy()) {
                rc.move(Direction.SOUTH);
                return;
            } else if (rc.canMove(Direction.WEST) && rc.canSenseLocation(current.add(Direction.WEST)) && !rc.senseMapInfo(current.add(Direction.WEST)).getPaint().isEnemy()) {
                rc.move(Direction.WEST);
                return;
            } else if (rc.canMove(Direction.SOUTHEAST) && rc.canSenseLocation(current.add(Direction.SOUTHEAST)) && !rc.senseMapInfo(current.add(Direction.SOUTHEAST)).getPaint().isEnemy()) {
                rc.move(Direction.SOUTHEAST);
                return;
            } else if (rc.canMove(Direction.NORTHWEST) && rc.canSenseLocation(current.add(Direction.NORTHWEST)) && !rc.senseMapInfo(current.add(Direction.NORTHWEST)).getPaint().isEnemy()) {
                rc.move(Direction.NORTHWEST);
                return;
            }
        } else if (dir == Direction.SOUTHEAST) {
            if (rc.canMove(Direction.SOUTHEAST) && rc.canSenseLocation(current.add(Direction.SOUTHEAST)) && !rc.senseMapInfo(current.add(Direction.SOUTHEAST)).getPaint().isEnemy()) {
                rc.move(Direction.SOUTHEAST);
                return;
            } else if (rc.canMove(Direction.SOUTH) && rc.canSenseLocation(current.add(Direction.SOUTH)) && !rc.senseMapInfo(current.add(Direction.SOUTH)).getPaint().isEnemy()) {
                rc.move(Direction.SOUTH);
                return;
            } else if (rc.canMove(Direction.EAST) && rc.canSenseLocation(current.add(Direction.EAST)) && !rc.senseMapInfo(current.add(Direction.EAST)).getPaint().isEnemy()) {
                rc.move(Direction.EAST);
                return;
            } else if (rc.canMove(Direction.SOUTHWEST) && rc.canSenseLocation(current.add(Direction.SOUTHWEST)) && !rc.senseMapInfo(current.add(Direction.SOUTHWEST)).getPaint().isEnemy()) {
                rc.move(Direction.SOUTHWEST);
                return;
            } else if (rc.canMove(Direction.NORTHEAST) && rc.canSenseLocation(current.add(Direction.NORTHEAST)) && !rc.senseMapInfo(current.add(Direction.NORTHEAST)).getPaint().isEnemy()) {
                rc.move(Direction.NORTHEAST);
                return;
            }

            for (Direction d : dirs) {
                if (rc.canMove(d)) {
                    rc.move(d);
                    return;
                }
            }

        }


        
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {
        

        // just checking and updating enemy locs:

        if (rc.canSenseLocation(enemy)) {
            enemyLocs.removeFirst();

            while (enemyLocs.size() < 1) {
                Random rng = new Random();
                int x = rng.nextInt(0, rc.getMapWidth() - 1);
                int y = rng.nextInt(0, rc.getMapHeight() - 1);
                if (new MapLocation(x, y).distanceSquaredTo(rc.getLocation()) >= 9) {
                    MapLocation moveDir = subtract(new MapLocation(x, y), rc.getLocation());
                    enemyLocs.addLast(project(rc.getLocation(), moveDir, (double) (rc.getMapWidth() + rc.getMapHeight()) / 2));
                }
            }

            enemy = enemyLocs.get(0);
        }

        MapLocation target = bot.bestAttackLocation();
        if (target != null) {
            if (rc.canAttack(target)) {
                rc.attack(target);
            }
            safeMove(target);
        } else {
            safeMove(enemy);

        }

        if (refillStrategy != null) {
            if (refillStrategy.isComplete()) {
                refillStrategy = null;
                runTick();
            } else {
                refillStrategy.runTick();
                //System.out.println("running refill strat");
            }
            return;
        }

        if (isPaintBelowHalf()) {
            RobotInfo nearest = getNearestRobot(b -> isFriendly(b) && b.getType().isTowerType() && b.getPaintAmount() >= missingPaint());
            if (nearest != null) {
                refillStrategy = new WeakRefillStrategy(nearest.getLocation(), 0.2);
                runTick();
            }
        }

        // build tower if i can lol
        MapInfo[] nearby = rc.senseNearbyMapInfos();
        for (MapInfo info : nearby) {
            if (info.hasRuin() && rc.canCompleteTowerPattern(TowerTracker.getNextType(), info.getMapLocation())) {
                rc.completeTowerPattern(TowerTracker.getNextType(), info.getMapLocation());
            }
        }
        

        

        
    }
}
