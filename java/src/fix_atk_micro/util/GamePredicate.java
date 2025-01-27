package fix_atk_micro.util;

import battlecode.common.GameActionException;

@FunctionalInterface
public interface GamePredicate<T> {
    boolean test(T t) throws GameActionException;

    static <T> java.util.function.Predicate<T> unchecked(GamePredicate<T> predicate) {
        return t -> {
            try {
                return predicate.test(t);
            } catch (GameActionException e) {
                throw new RuntimeException(e); // Wrap GameException into RuntimeException
            }
        };
    }
}
