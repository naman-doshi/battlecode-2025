package fix_atk_micro.util;

import battlecode.common.GameActionException;
import java.util.function.Consumer;

@FunctionalInterface
public interface GameConsumer<T> {
    /**
     * Performs this operation on the given argument, potentially throwing a GameActionException.
     *
     * @param t the input argument
     * @throws GameActionException if the operation fails
     */
    void accept(T t) throws GameActionException;

    /**
     * Wraps a GameConsumer into a standard Consumer, converting GameActionException into RuntimeException.
     *
     * @param consumer the GameConsumer to wrap
     * @param <T> the type of the input to the operation
     * @return a standard Consumer that handles GameActionException
     */
    static <T> Consumer<T> unchecked(GameConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (GameActionException e) {
                throw new RuntimeException(e); // Wrap GameActionException into RuntimeException
            }
        };
    }
}
