package caterpillow.util;

import java.util.concurrent.atomic.AtomicLong;

public class CustomRandom {

    private static final long MULTIPLIER = 0x5DEECE66DL;
    private static final long ADDEND = 0xBL;
    private static final long MASK = (1L << 48) - 1;

    private AtomicLong seed;

    public CustomRandom() {
        setSeed(69);
    }

    public CustomRandom(long seed) {
        setSeed(seed);
    }

    public void setSeed(long seed) {
        this.seed = new AtomicLong((seed ^ MULTIPLIER) & MASK);
    }

    protected int next(int bits) {
        long oldSeed = seed.get();
        long nextSeed = (oldSeed * MULTIPLIER + ADDEND) & MASK;
        seed.set(nextSeed); // No retries, deterministic time
        return (int) (nextSeed >>> (48 - bits));
    }

    public int nextInt() {
        return next(32);
    }

    public int nextInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        }
        return next(31) % bound;
    }

    public int nextInt(int lowerBound, int upperBound) {
        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("Lower bound must be less than upper bound");
        }
        return lowerBound + nextInt(upperBound - lowerBound);
    }

    public long nextLong() {
        return ((long) next(32) << 32) + next(32);
    }

    public long nextLong(long lowerBound, long upperBound) {
        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("Lower bound must be less than upper bound");
        }
        long range = upperBound - lowerBound;
        return lowerBound + (Math.abs(nextLong()) % range);
    }

    public boolean nextBoolean() {
        return next(1) != 0;
    }

    public float nextFloat() {
        return next(24) / ((float) (1 << 24));
    }

    public float nextFloat(float lowerBound, float upperBound) {
        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("Lower bound must be less than upper bound");
        }
        return lowerBound + (upperBound - lowerBound) * nextFloat();
    }

    public double nextDouble() {
        return (((long) next(26) << 27) + next(27)) / (double) (1L << 53);
    }

    public double nextDouble(double lowerBound, double upperBound) {
        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("Lower bound must be less than upper bound");
        }
        return lowerBound + (upperBound - lowerBound) * nextDouble();
    }
}
