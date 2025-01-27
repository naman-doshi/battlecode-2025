package fix_atk_micro.util;

import battlecode.common.GameActionException;
import java.util.function.BinaryOperator;

@FunctionalInterface
public interface GameBinaryOperator<T> {
    T apply(T t1, T t2) throws GameActionException;

    /**
     * Wraps a GameBinaryOperator into a standard BinaryOperator, converting GameActionException into RuntimeException.
     *
     * @param operator the GameBinaryOperator to wrap
     * @param <T> the type of operands and result
     * @return a standard BinaryOperator that handles GameActionException
     */
    static <T> BinaryOperator<T> unchecked(GameBinaryOperator<T> operator) {
        return (t1, t2) -> {
            try {
                return operator.apply(t1, t2);
            } catch (GameActionException e) {
                throw new RuntimeException(e); // Wrap GameActionException into RuntimeException
            }
        };
    }
}
