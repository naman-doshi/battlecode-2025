package caterpillow_v1.util;

import battlecode.common.GameActionException;
import java.util.function.Supplier;

@FunctionalInterface
public interface GameSupplier<T> {
    /**
     * Supplies a result, potentially throwing a GameActionException.
     *
     * @return a result
     * @throws GameActionException if the supplier cannot supply a result
     */
    T get() throws GameActionException;

    /**
     * Wraps a GameSupplier into a standard Supplier, converting GameActionException into RuntimeException.
     *
     * @param supplier the GameSupplier to wrap
     * @param <T> the type of the result
     * @return a standard Supplier that handles GameActionException
     */
    static <T> Supplier<T> unchecked(GameSupplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (GameActionException e) {
                throw new RuntimeException(e); // Wrap GameActionException into RuntimeException
            }
        };
    }
}
