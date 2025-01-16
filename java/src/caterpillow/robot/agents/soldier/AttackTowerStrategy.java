package caterpillow.robot.agents.soldier;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import caterpillow.Game;
import static caterpillow.Game.rc;
import caterpillow.robot.Strategy;
import caterpillow.robot.agents.Agent;
import static caterpillow.util.Util.indicate;

// pathfinding testing
public class AttackTowerStrategy extends Strategy {

    Agent bot;
    MapLocation target;
    int numAttacks;
    boolean shouldAbort = false;
    int numBlocked = 0;

    public AttackTowerStrategy(MapLocation target) {
        bot = (Agent) Game.bot;
        this.target = target;
    }

    @Override
    public boolean isComplete() throws GameActionException {
        if (rc.canSenseLocation(target)) {
            RobotInfo bot = rc.senseRobotAtLocation(target);
            return (bot == null || !bot.getType().isTowerType() || bot.getTeam() == rc.getTeam());
        }
        return false;
    }

    // TODO: combat micro
    @Override
    public void runTick() throws GameActionException {
        indicate("ATTACKING TOWER AT " + target);

        // kiting

        // :(( there r barely any constants

        // move towards the tower until it's just out of reach (i.e. as soon as distance^2 <= 16)
       int distanceSquared = rc.getLocation().distanceSquaredTo(target);
       if (distanceSquared > 16 && numAttacks == 0) {
           bot.pathfinder.makeMove(target);
           return;
       }

       numAttacks++;

       // we NEED to attack and move at the same time for this to work, so make sure we can do both and have enough paint
       if (rc.isActionReady() && rc.isMovementReady() && rc.getPaint() >= 8) {
           Direction plannedMove = bot.pathfinder.getMove(target);
           if (plannedMove == null) {
                indicate("ATTACKING: NO MOVE");
                return;
           }

           // if our move can't get us closer to the target, smth is very wrong. just move and dont do anything else
           // waiting for andy's pathfinder buff
           if (rc.getLocation().add(plannedMove).distanceSquaredTo(target) >= distanceSquared) {
               bot.pathfinder.makeMove(target);
               indicate("ATTACKING: BLOCKED");
               numBlocked++;
               return;
           }

           numBlocked = 0;

           // if we're out of range: move, then attack
           if (!rc.canAttack(target) && rc.getLocation().add(plannedMove).distanceSquaredTo(target) <= 9) {
               bot.pathfinder.makeMove(target);
               if (rc.canAttack(target)) rc.attack(target);
           }

           // if in range: attack, then move away
           if (rc.canAttack(target)) {
               rc.attack(target);
               bot.pathfinder.makeMove(rc.getLocation().add(rc.getLocation().directionTo(target).opposite()));
           }
       }

       
    }
}
