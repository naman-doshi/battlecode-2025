package caterpillow.util;

public class Tuple<F, S, T> {
    public F first;
    public S second;
    public T third;

    public Tuple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ", " + third + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tuple<?, ?, ?> pair = (Tuple<?, ?, ?>) obj;
        return first.equals(pair.first) && second.equals(pair.second) && third.equals(pair.third);
    }

    @Override
    public int hashCode() {
        return 31 * 31 * first.hashCode() + 31 * second.hashCode() + third.hashCode();
    }
}
