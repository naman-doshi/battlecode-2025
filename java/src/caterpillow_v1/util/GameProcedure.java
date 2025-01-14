package caterpillow_v1.util;

import battlecode.common.GameActionException;

@FunctionalInterface
public interface GameProcedure {
    /**
     * Executes this procedure, potentially throwing a GameActionException.
     *
     * @throws GameActionException if the procedure fails
     */
    void execute() throws GameActionException;

    /**
     * Wraps a GameProcedure into a Runnable, converting GameActionException into a RuntimeException.
     *
     * @param procedure the GameProcedure to wrap
     * @return a standard Runnable that handles GameActionException
     */
    static Runnable unchecked(GameProcedure procedure) {
        return () -> {
            try {
                procedure.execute();
            } catch (GameActionException e) {
                throw new RuntimeException(e); // Wrap GameActionException into RuntimeException
            }
        };
    }
}
