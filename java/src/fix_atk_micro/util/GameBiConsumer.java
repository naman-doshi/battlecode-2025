package fix_atk_micro.util;

import battlecode.common.GameActionException;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface GameBiConsumer<T, U> {
    /**
     * Performs this operation on the given arguments, potentially throwing a GameActionException.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @throws GameActionException if the operation fails
     */
    void accept(T t, U u) throws GameActionException;

    /**
     * Wraps a GameBiConsumer into a standard BiConsumer, converting GameActionException into RuntimeException.
     *
     * @param consumer the GameBiConsumer to wrap
     * @param <T> the type of the first argument to the operation
     * @param <U> the type of the second argument to the operation
     * @return a standard BiConsumer that handles GameActionException
     */
    static <T, U> BiConsumer<T, U> unchecked(GameBiConsumer<T, U> consumer) {
        return (t, u) -> {
            try {
                consumer.accept(t, u);
            } catch (GameActionException e) {
                throw new RuntimeException(e); // Wrap GameActionException into RuntimeException
            }
        };
    }
}
