package caterpillow_v1.util;

import battlecode.common.GameActionException;
import java.util.function.Function;

@FunctionalInterface
public interface GameFunction<T, R> {
    /**
     * Applies this function to the given argument, potentially throwing a GameActionException.
     *
     * @param t the function argument
     * @return the function result
     * @throws GameActionException if the function cannot compute a result
     */
    R apply(T t) throws GameActionException;

    /**
     * Wraps a GameFunction into a standard Function, converting GameActionException into RuntimeException.
     *
     * @param function the GameFunction to wrap
     * @param <T> the type of the input to the function
     * @param <R> the type of the result of the function
     * @return a standard Function that handles GameActionException
     */
    static <T, R> Function<T, R> unchecked(GameFunction<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (GameActionException e) {
                throw new RuntimeException(e); // Wrap GameActionException into RuntimeException
            }
        };
    }
}
