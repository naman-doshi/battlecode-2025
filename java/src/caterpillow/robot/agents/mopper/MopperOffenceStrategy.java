package caterpillow.robot.agents.mopper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;
import caterpillow.util.GameSupplier;
import static caterpillow.util.Util.getNearestCell;
import static caterpillow.util.Util.guessEnemyLocs;
import static caterpillow.util.Util.isCellInTowerBounds;
import static caterpillow.util.Util.isEnemyAgent;
import static caterpillow.util.Util.isInAttackRange;
import static caterpillow.util.Util.project;
import static caterpillow.util.Util.subtract;

public class MopperOffenceStrategy extends Strategy {

    public Mopper bot;

    // enemyLocs is more like "POI locs"
    public List<MapLocation> enemyLocs;
    public MapLocation enemy;
    public List<GameSupplier<MapInfo>> suppliers;

    public MopperOffenceStrategy() throws GameActionException {
        bot = (Mopper) Game.bot;
        this.enemyLocs = guessEnemyLocs(bot.home);
        this.enemy = enemyLocs.get(0);
        enemyLocs.addLast(bot.home);

        suppliers = new ArrayList<>();
        // mop and attack (in range)
        suppliers.add(() -> getNearestCell(c -> isInAttackRange(c.getMapLocation()) && rc.senseRobotAtLocation(c.getMapLocation()) != null && isEnemyAgent(rc.senseRobotAtLocation(c.getMapLocation())) && c.getPaint().isEnemy()));
//         attack (anything visible)
        suppliers.add(() -> getNearestCell(c -> rc.senseRobotAtLocation(c.getMapLocation()) != null && isEnemyAgent(rc.senseRobotAtLocation(c.getMapLocation()))));
        // mop cell near ruin
        suppliers.add(() -> {
            ArrayList<MapLocation> ruins = new ArrayList<>();
            for (MapInfo c : rc.senseNearbyMapInfos()) {
                if (c.hasRuin()) {
                    ruins.add(c.getMapLocation());
                }
            }
            return getNearestCell(c -> {
                if (!c.getPaint().isEnemy()) {
                    return false;
                }
                for (MapLocation ruin : ruins) {
                    if (isCellInTowerBounds(ruin, c.getMapLocation())) {
                        return true;
                    }
                }
                return false;
            });
        });
        // chase enemy cell
        suppliers.add(() -> getNearestCell(c -> c.getPaint().isEnemy()));
    }

    public MapLocation safeMove(MapLocation loc) throws GameActionException {
        // conserve bytecode
        Direction dir = rc.getLocation().directionTo(loc);
        MapLocation current = rc.getLocation();
        Direction[] dirs = Direction.values();
        if (dir == Direction.NORTH) {
            if (rc.canMove(Direction.NORTH) && rc.canSenseLocation(current.add(Direction.NORTH)) && rc.senseMapInfo(current.add(Direction.NORTH)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.NORTH);
            } else if (rc.canMove(Direction.NORTHWEST) && rc.canSenseLocation(current.add(Direction.NORTHWEST)) && rc.senseMapInfo(current.add(Direction.NORTHWEST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.NORTHWEST);
            } else if (rc.canMove(Direction.NORTHEAST) && rc.canSenseLocation(current.add(Direction.NORTHEAST)) && rc.senseMapInfo(current.add(Direction.NORTHEAST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.NORTHEAST);
            } else if (rc.canMove(Direction.EAST) && rc.canSenseLocation(current.add(Direction.EAST)) && rc.senseMapInfo(current.add(Direction.EAST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.EAST);
            } else if (rc.canMove(Direction.WEST) && rc.canSenseLocation(current.add(Direction.WEST)) && rc.senseMapInfo(current.add(Direction.WEST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.WEST);
            }
        } else if (dir == Direction.SOUTH) {
            if (rc.canMove(Direction.SOUTH) && rc.canSenseLocation(current.add(Direction.SOUTH)) && rc.senseMapInfo(current.add(Direction.SOUTH)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.SOUTH);
            } else if (rc.canMove(Direction.SOUTHWEST) && rc.canSenseLocation(current.add(Direction.SOUTHWEST)) && rc.senseMapInfo(current.add(Direction.SOUTHWEST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.SOUTHWEST);
            } else if (rc.canMove(Direction.SOUTHEAST) && rc.canSenseLocation(current.add(Direction.SOUTHEAST)) && rc.senseMapInfo(current.add(Direction.SOUTHEAST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.SOUTHEAST);
            } else if (rc.canMove(Direction.EAST) && rc.canSenseLocation(current.add(Direction.EAST)) && rc.senseMapInfo(current.add(Direction.EAST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.EAST);
            } else if (rc.canMove(Direction.WEST) && rc.canSenseLocation(current.add(Direction.WEST)) && rc.senseMapInfo(current.add(Direction.WEST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.WEST);
            }
        } else if (dir == Direction.EAST) {
            if (rc.canMove(Direction.EAST) && rc.canSenseLocation(current.add(Direction.EAST)) && rc.senseMapInfo(current.add(Direction.EAST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.EAST);
            } else if (rc.canMove(Direction.NORTHEAST) && rc.canSenseLocation(current.add(Direction.NORTHEAST)) && rc.senseMapInfo(current.add(Direction.NORTHEAST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.NORTHEAST);
            } else if (rc.canMove(Direction.SOUTHEAST) && rc.canSenseLocation(current.add(Direction.SOUTHEAST)) && rc.senseMapInfo(current.add(Direction.SOUTHEAST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.SOUTHEAST);
            } else if (rc.canMove(Direction.NORTH) && rc.canSenseLocation(current.add(Direction.NORTH)) && rc.senseMapInfo(current.add(Direction.NORTH)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.NORTH);
            } else if (rc.canMove(Direction.SOUTH) && rc.canSenseLocation(current.add(Direction.SOUTH)) && rc.senseMapInfo(current.add(Direction.SOUTH)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.SOUTH);
            }
        } else if (dir == Direction.WEST) {
            if (rc.canMove(Direction.WEST) && rc.canSenseLocation(current.add(Direction.WEST)) && rc.senseMapInfo(current.add(Direction.WEST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.WEST);
            } else if (rc.canMove(Direction.NORTHWEST) && rc.canSenseLocation(current.add(Direction.NORTHWEST)) && rc.senseMapInfo(current.add(Direction.NORTHWEST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.NORTHWEST);
            } else if (rc.canMove(Direction.SOUTHWEST) && rc.canSenseLocation(current.add(Direction.SOUTHWEST)) && rc.senseMapInfo(current.add(Direction.SOUTHWEST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.SOUTHWEST);
            } else if (rc.canMove(Direction.NORTH) && rc.canSenseLocation(current.add(Direction.NORTH)) && rc.senseMapInfo(current.add(Direction.NORTH)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.NORTH);
            } else if (rc.canMove(Direction.SOUTH) && rc.canSenseLocation(current.add(Direction.SOUTH)) && rc.senseMapInfo(current.add(Direction.SOUTH)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.SOUTH);
            }
        } else if (dir == Direction.NORTHWEST) {
            if (rc.canMove(Direction.NORTHWEST) && rc.canSenseLocation(current.add(Direction.NORTHWEST)) && rc.senseMapInfo(current.add(Direction.NORTHWEST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.NORTHWEST);
            } else if (rc.canMove(Direction.NORTH) && rc.canSenseLocation(current.add(Direction.NORTH)) && rc.senseMapInfo(current.add(Direction.NORTH)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.NORTH);
            } else if (rc.canMove(Direction.WEST) && rc.canSenseLocation(current.add(Direction.WEST)) && rc.senseMapInfo(current.add(Direction.WEST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.WEST);
            } else if (rc.canMove(Direction.SOUTHWEST) && rc.canSenseLocation(current.add(Direction.SOUTHWEST)) && rc.senseMapInfo(current.add(Direction.SOUTHWEST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.SOUTHWEST);
            } else if (rc.canMove(Direction.NORTHEAST) && rc.canSenseLocation(current.add(Direction.NORTHEAST)) && rc.senseMapInfo(current.add(Direction.NORTHEAST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.NORTHEAST);
            }
        } else if (dir == Direction.NORTHEAST) {
            if (rc.canMove(Direction.NORTHEAST) && rc.canSenseLocation(current.add(Direction.NORTHEAST)) && rc.senseMapInfo(current.add(Direction.NORTHEAST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.NORTHEAST);
            } else if (rc.canMove(Direction.NORTH) && rc.canSenseLocation(current.add(Direction.NORTH)) && rc.senseMapInfo(current.add(Direction.NORTH)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.NORTH);
            } else if (rc.canMove(Direction.EAST) && rc.canSenseLocation(current.add(Direction.EAST)) && rc.senseMapInfo(current.add(Direction.EAST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.EAST);
            } else if (rc.canMove(Direction.NORTHWEST) && rc.canSenseLocation(current.add(Direction.NORTHWEST)) && rc.senseMapInfo(current.add(Direction.NORTHWEST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.NORTHWEST);
            } else if (rc.canMove(Direction.SOUTHEAST) && rc.canSenseLocation(current.add(Direction.SOUTHEAST)) && rc.senseMapInfo(current.add(Direction.SOUTHEAST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.SOUTHEAST);
            }
        } else if (dir == Direction.SOUTHWEST) {
            if (rc.canMove(Direction.SOUTHWEST) && rc.canSenseLocation(current.add(Direction.SOUTHWEST)) && rc.senseMapInfo(current.add(Direction.SOUTHWEST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.SOUTHWEST);
            } else if (rc.canMove(Direction.SOUTH) && rc.canSenseLocation(current.add(Direction.SOUTH)) && rc.senseMapInfo(current.add(Direction.SOUTH)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.SOUTH);
            } else if (rc.canMove(Direction.WEST) && rc.canSenseLocation(current.add(Direction.WEST)) && rc.senseMapInfo(current.add(Direction.WEST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.WEST);
            } else if (rc.canMove(Direction.SOUTHEAST) && rc.canSenseLocation(current.add(Direction.SOUTHEAST)) && rc.senseMapInfo(current.add(Direction.SOUTHEAST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.SOUTHEAST);
            } else if (rc.canMove(Direction.NORTHWEST) && rc.canSenseLocation(current.add(Direction.NORTHWEST)) && rc.senseMapInfo(current.add(Direction.NORTHWEST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.NORTHWEST);
            }
        } else if (dir == Direction.SOUTHEAST) {
            if (rc.canMove(Direction.SOUTHEAST) && rc.canSenseLocation(current.add(Direction.SOUTHEAST)) && rc.senseMapInfo(current.add(Direction.SOUTHEAST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.SOUTHEAST);
            } else if (rc.canMove(Direction.SOUTH) && rc.canSenseLocation(current.add(Direction.SOUTH)) && rc.senseMapInfo(current.add(Direction.SOUTH)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.SOUTH);
            } else if (rc.canMove(Direction.EAST) && rc.canSenseLocation(current.add(Direction.EAST)) && rc.senseMapInfo(current.add(Direction.EAST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.EAST);
            } else if (rc.canMove(Direction.SOUTHWEST) && rc.canSenseLocation(current.add(Direction.SOUTHWEST)) && rc.senseMapInfo(current.add(Direction.SOUTHWEST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.SOUTHWEST);
            } else if (rc.canMove(Direction.NORTHEAST) && rc.canSenseLocation(current.add(Direction.NORTHEAST)) && rc.senseMapInfo(current.add(Direction.NORTHEAST)).getPaint() != PaintType.EMPTY) {
                return current.add(Direction.NORTHEAST);
            }
        }

        for (Direction d : dirs) {
            if (rc.canMove(d)) {
                return rc.getLocation().add(d);
            }
        }

        return rc.getLocation();

    }

    @Override
    public boolean isComplete() throws GameActionException {
        return false;
    }

    @Override
    public void runTick() throws GameActionException {

        // just checking and updating enemy locs:

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

        for (GameSupplier<MapInfo> pred : suppliers) {
            MapInfo res = pred.get();
            if (res != null) {
                // go towards, and attack if possible
                MapLocation next = safeMove(res.getMapLocation());
                Direction dir = rc.getLocation().directionTo(next);
                if (rc.canAttack(next) && rc.senseMapInfo(next).getPaint().isEnemy()) {
                    rc.attack(next);
                    if (rc.canMove(dir)) {
                        rc.move(dir);
                    }
                } else if (rc.senseMapInfo(next).getPaint().isAlly()) {
                    if (rc.canMove(dir)) {
                        rc.move(dir);
                    }
                }
            }
        }

        // run towards goal
        if (rc.isMovementReady()) {
            MapLocation next = safeMove(enemy);
            Direction dir = rc.getLocation().directionTo(next);
            rc.move(dir);
        }
    }
}
