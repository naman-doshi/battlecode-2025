package caterpillow.robot.agents.soldier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import static caterpillow.Game.time;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.WeakRefillStrategy;
import caterpillow.util.Pair;
import caterpillow.util.TowerTracker;
import static caterpillow.util.Util.checkerboardPaint;
import static caterpillow.util.Util.decodeLoc;
import static caterpillow.util.Util.getNearestCell;
import static caterpillow.util.Util.getNearestRobot;
import static caterpillow.util.Util.getSRPIds;
import static caterpillow.util.Util.guessEnemyLocs;
import static caterpillow.util.Util.indicate;
import static caterpillow.util.Util.isFriendly;
import static caterpillow.util.Util.isPaintBelowHalf;
import static caterpillow.util.Util.missingPaint;
import static caterpillow.util.Util.project;
import static caterpillow.util.Util.subtract;

public class SRPStrategy extends Strategy {

    public Soldier bot;

    // enemyLocs is more like "POI locs"
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;
    HandleRuinStrategy handleRuinStrategy;
    WeakRefillStrategy refillStrategy;
    int skipCooldown = 0;
    ArrayList<MapLocation> visitedRuins;
    LinkedList<Pair<MapLocation, Integer>> skippedRuins;
    int towerstratcooldown;

    public SRPStrategy() throws GameActionException {
        bot = (Soldier) Game.bot;

        // cursed way to only keep the first elem but idc
        this.enemyLocs = guessEnemyLocs(bot.home);
        this.enemyLocs.removeLast();
        this.enemyLocs.removeLast();
        
        this.enemy = enemyLocs.get(0);

        visitedRuins = new ArrayList<>();
        skippedRuins = new LinkedList<>();
        towerstratcooldown = 0;

        skipCooldown = (rc.getMapHeight() + rc.getMapWidth()) / 2;
        Direction[] dirs = Direction.values();
        
    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
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

    void refresh() {
        skippedRuins.removeIf(el -> time >= el.second + skipCooldown);
    }

    @Override
    public void runTick() throws GameActionException {
        indicate("srp");
        //refresh();
        towerstratcooldown--;
        // TODO: better scouting system!!!

        //rc.setIndicatorLine(rc.getLocation(), enemy, 255, 255, 0);

        
        if (rc.canSenseLocation(enemy)) {
            // if we can see the enemy, just go to the next enemy loc.
            enemyLocs.removeFirst();

            // procedurally gen the next one
            while (enemyLocs.size() < 1) {
                Random rng = new Random();
                int x = rng.nextInt(0, rc.getMapWidth() - 1);
                int y = rng.nextInt(0, rc.getMapHeight() - 1);
                if (new MapLocation(x, y).distanceSquaredTo(rc.getLocation()) >= 9) {
                    MapLocation moveDir = subtract(new MapLocation(x, y), rc.getLocation());
                    enemyLocs.addLast(project(rc.getLocation(), moveDir, (double) (rc.getMapWidth() + rc.getMapHeight()) / 2));
                }
            }
            
            enemy = enemyLocs.getFirst();
            
            //indicate("NEW ENEMY LOC: " + enemy);
        }

        if (handleRuinStrategy != null) {
            if (handleRuinStrategy.isComplete()) {
                if (handleRuinStrategy.didSkip()) {
                    skippedRuins.add(new Pair<>(handleRuinStrategy.target, time));
                } else {
                    visitedRuins.add(handleRuinStrategy.target);
                }
                handleRuinStrategy = null;
                towerstratcooldown = 40;
                runTick();
            } else {
                handleRuinStrategy.runTick();
            }
            return;
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
                refillStrategy = new WeakRefillStrategy(nearest.getLocation(), 0.1);
                runTick();
            }
        }

        

        // first: it obviously needs to be the wrong colour, non-enemy paint, and passable
        // second: if it's outside a ruin OR a neutral colour, obviously paint it
        // but if it's inside and painted w ally, only paint it if there's more than one SRP on it (since one of them will be the ruin's SRP)
        List<MapLocation> ruins = new ArrayList<>();
        List<MapLocation> towers = new ArrayList<>();
        for (MapInfo ruin : rc.senseNearbyMapInfos()) {
            if (ruin.hasRuin()) {
                RobotInfo info = rc.senseRobotAtLocation(rc.getLocation());
                if (info == null/* || !isFriendly(info)*/) {
                    ruins.add(ruin.getMapLocation());
                } else if (isFriendly(info)) {
                    towers.add(ruin.getMapLocation());
                }
            }
        }

        MapInfo target = null;
        for (MapInfo cell : rc.senseNearbyMapInfos()) {
            if (cell.getPaint() != checkerboardPaint(cell.getMapLocation()) && cell.isPassable() && !cell.getPaint().isEnemy()) {
                // needs painting and is paintable
                if (target == null || target.getMapLocation().distanceSquaredTo(rc.getLocation()) > cell.getMapLocation().distanceSquaredTo(rc.getLocation())) {
                        target = cell;
                }
            }
        }

        //System.out.println("Left after target selection: " + Clock.getBytecodesLeft());
        
        if (target != null) {
            if (rc.canAttack(target.getMapLocation())) {
                bot.checkerboardAttack(target.getMapLocation());
                // try complete the SRP i just attacked
                List<Integer> srpIDs = getSRPIds(target.getMapLocation());
                for (int id : srpIDs) {
                    MapLocation srpLoc = decodeLoc(id);
                    if (rc.canCompleteResourcePattern(srpLoc)) {
                        rc.completeResourcePattern(srpLoc);
                    }
                }
            } else {
                indicate("moving to target " + target.getMapLocation());
                safeMove(target.getMapLocation());
                //indicate("moving to target " + target.getMapLocation());
            }
        } else {
            indicate("moving to enemy " + enemy);
            safeMove(enemy);
            //indicate("moving to enemy");
        }

        // if (Clock.getBytecodesLeft() < 100) {
        //     System.out.println("Ran out of bytecodes");
        // }

        
        if (towerstratcooldown > 0) {
            return;
        }
        
        MapInfo target1 = getNearestCell(c -> c.hasRuin() && !visitedRuins.contains(c.getMapLocation()) && rc.senseRobotAtLocation(c.getMapLocation()) == null && skippedRuins.stream().noneMatch(el -> el.first.equals(c.getMapLocation())));
        if (target1 != null) {
            System.out.println("starting handle ruin strat");
            handleRuinStrategy = new HandleRuinStrategy(target1.getMapLocation(), TowerTracker.getNextType());
            runTick();
            return;
        }

    }
}
